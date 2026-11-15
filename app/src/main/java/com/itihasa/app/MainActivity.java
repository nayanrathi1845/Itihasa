package com.itihasa.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.window.OnBackInvokedDispatcher;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Hosts the bundled Itihasa web app (app/src/main/assets/index.html) in a full-screen WebView.
 * Story text runs offline from the APK; story illustrations are fetched once from Firebase Hosting and
 * then kept on the device. Bookmarks and progress live in the WebView's localStorage.
 * Firebase Analytics (anonymous usage events) and Firebase Authentication (accounts) are reached
 * through {@link NativeBridge}.
 */
public class MainActivity extends Activity {

    private static final String APP_URL = "file:///android_asset/index.html";
    private static final String IMG_PREFIX = "https://itihasa-9c5bf.web.app/img/";
    private static final Pattern IMG_NAME = Pattern.compile("[a-z0-9-]{1,120}\\.webp");
    private static final long MAX_IMG_BYTES = 3L * 1024 * 1024;

    private WebView webView;
    private FirebaseAnalytics analytics;
    private View topSpacer;
    private View bottomSpacer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        analytics = FirebaseAnalytics.getInstance(this);
        NativeBridge bridge = new NativeBridge(this, analytics);
        analytics.setAnalyticsCollectionEnabled(bridge.isAnalyticsEnabled());

        // Layout: status-bar spacer / WebView / navigation-bar spacer. On Android 15+ apps draw
        // edge-to-edge, so the spacers keep the page clear of the system bars (and keep the colours).
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#fbf6ed"));

        topSpacer = new View(this);
        topSpacer.setBackgroundColor(Color.parseColor("#8c482e"));
        bottomSpacer = new View(this);
        bottomSpacer.setBackgroundColor(Color.parseColor("#fffdf8"));

        webView = new WebView(this);
        webView.setBackgroundColor(Color.parseColor("#fbf6ed"));

        root.addView(topSpacer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        root.addView(webView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        root.addView(bottomSpacer, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        setContentView(root);
        root.setOnApplyWindowInsetsListener(this::applyInsets);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true); // needed for localStorage (profiles, bookmarks, progress, language)
        // The page only needs its own bundled assets: no content:// access, no cross-file reads.
        s.setAllowContentAccess(false);
        s.setAllowFileAccess(false); // file:///android_asset/ stays readable; other file:// paths do not
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        if (Build.VERSION.SDK_INT >= 26) s.setSafeBrowsingEnabled(true);
        s.setAllowFileAccessFromFileURLs(false);
        s.setAllowUniversalAccessFromFileURLs(false);

        bridge.attach(webView);
        webView.addJavascriptInterface(bridge, "ItihasaNative");
        webView.setWebViewClient(new WebViewClient() {
            // Only the bundled page may load in the WebView (the bridge must never be reachable by remote pages).
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return !request.getUrl().toString().startsWith("file:///android_asset/");
            }

            // Story illustrations live on Firebase Hosting. Each one is downloaded once, kept in the
            // app's private storage and served from there afterwards (so it also works offline).
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return storyImage(request);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            // The app uses confirm() for "Clear local reading data"; WebView shows nothing unless handled.
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, (d, w) -> result.confirm())
                        .setNegativeButton(android.R.string.cancel, (d, w) -> result.cancel())
                        .setOnCancelListener(d -> result.cancel())
                        .show();
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, (d, w) -> result.confirm())
                        .setOnCancelListener(d -> result.confirm())
                        .show();
                return true;
            }
        });

        // Back handling: Android 13+ uses the back-invoked callback (required for targetSdk 36,
        // where onBackPressed() is no longer called); older versions use onBackPressed().
        if (Build.VERSION.SDK_INT >= 33) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, this::handleBack);
        }

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(APP_URL);
            bridge.log("app_open", appOpenParams());
        }
    }

    private WebResourceResponse storyImage(WebResourceRequest request) {
        try {
            String url = request.getUrl().toString();
            if (!url.startsWith(IMG_PREFIX) || !"GET".equalsIgnoreCase(request.getMethod())) return null;
            String name = url.substring(IMG_PREFIX.length());
            if (!IMG_NAME.matcher(name).matches()) return null;
            File dir = new File(getFilesDir(), "img");
            File file = new File(dir, name);
            if (!file.isFile() || file.length() == 0) downloadImage(url, dir, file);
            if (!file.isFile() || file.length() == 0) return null; // let the WebView try (and fail quietly offline)
            WebResourceResponse resp = new WebResourceResponse("image/webp", null, new FileInputStream(file));
            Map<String, String> headers = new HashMap<>();
            headers.put("Cache-Control", "max-age=31536000");
            resp.setResponseHeaders(headers);
            return resp;
        } catch (Exception e) {
            return null;
        }
    }

    private static void downloadImage(String url, File dir, File target) {
        HttpURLConnection c = null;
        File tmp = null;
        try {
            if (!dir.isDirectory() && !dir.mkdirs()) return;
            c = (HttpURLConnection) new URL(url).openConnection();
            c.setConnectTimeout(10000);
            c.setReadTimeout(20000);
            c.setInstanceFollowRedirects(false);
            if (c.getResponseCode() != 200) return;
            String type = c.getContentType();
            if (type == null || !type.startsWith("image/")) return;
            tmp = new File(dir, target.getName() + "." + System.nanoTime() + ".tmp");
            long total = 0;
            try (InputStream in = c.getInputStream(); FileOutputStream out = new FileOutputStream(tmp)) {
                byte[] buf = new byte[16384];
                int n;
                while ((n = in.read(buf)) > 0) {
                    total += n;
                    if (total > MAX_IMG_BYTES) return;
                    out.write(buf, 0, n);
                }
            }
            if (total > 0 && tmp.renameTo(target)) tmp = null;
        } catch (Exception ignored) {
        } finally {
            if (tmp != null) tmp.delete();
            if (c != null) c.disconnect();
        }
    }

    private Bundle appOpenParams() {
        Bundle b = new Bundle();
        String version = "unknown";
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (pi.versionName != null) version = pi.versionName;
        } catch (Exception ignored) {
        }
        b.putString("app_version", version);
        return b;
    }

    @SuppressWarnings("deprecation")
    private WindowInsets applyInsets(View v, WindowInsets insets) {
        int top, bottom;
        if (Build.VERSION.SDK_INT >= 30) {
            android.graphics.Insets bars = insets.getInsets(
                    WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
            android.graphics.Insets ime = insets.getInsets(WindowInsets.Type.ime());
            top = bars.top;
            bottom = Math.max(bars.bottom, ime.bottom);
        } else {
            top = insets.getSystemWindowInsetTop();
            bottom = insets.getSystemWindowInsetBottom();
        }
        // Horizontal insets (landscape cut-outs / gesture bars) become padding on the root.
        int left, right;
        if (Build.VERSION.SDK_INT >= 30) {
            android.graphics.Insets bars = insets.getInsets(
                    WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
            left = bars.left;
            right = bars.right;
        } else {
            left = insets.getSystemWindowInsetLeft();
            right = insets.getSystemWindowInsetRight();
        }
        v.setPadding(left, 0, right, 0);
        topSpacer.getLayoutParams().height = top;
        bottomSpacer.getLayoutParams().height = bottom;
        topSpacer.requestLayout();
        bottomSpacer.requestLayout();
        return Build.VERSION.SDK_INT >= 30 ? WindowInsets.CONSUMED : insets.consumeSystemWindowInsets();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    // Tell the page to close the current reading session when the app leaves the foreground.
    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.evaluateJavascript("window.__itihasaFlush&&window.__itihasaFlush()", null);
        }
    }

    // Back button: from any inner screen go to Home first; leave the app only when already on Home.
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        handleBack(); // Android 12 and older; Android 13+ goes through the callback registered in onCreate
    }

    private void handleBack() {
        webView.evaluateJavascript(
                "(function(){try{if(page!=='home'){showPage('home');return 'handled';}}catch(e){}return 'exit';})()",
                value -> {
                    if (value == null || !value.contains("handled")) {
                        leaveApp();
                    }
                });
    }

    private void leaveApp() {
        if (Build.VERSION.SDK_INT >= 31 && isTaskRoot()) {
            moveTaskToBack(true); // same behaviour as the system default for a launcher activity
        } else {
            finish();
        }
    }
}
