package com.itihasa.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Minimal bridge exposed to the bundled page as window.ItihasaNative.
 * Only the whitelisted events/parameters below can reach Firebase Analytics; no personal data
 * (name, email, search text) is ever sent.
 */
class NativeBridge {
    private static final Set<String> EVENTS = new HashSet<>(Arrays.asList(
            "app_open", "scripture_opened", "story_opened", "story_completed", "language_selected",
            "story_bookmarked", "search_performed", "reading_session", "share_clicked"));
    private static final Set<String> STRING_PARAMS = new HashSet<>(Arrays.asList(
            "app_version", "scripture_id", "story_id", "language", "search_category"));

    private final Activity activity;
    private final FirebaseAnalytics analytics;

    NativeBridge(Activity activity, FirebaseAnalytics analytics) {
        this.activity = activity;
        this.analytics = analytics;
    }

    /** Called from native code and from the page. */
    void log(String name, Bundle params) {
        if (EVENTS.contains(name)) analytics.logEvent(name, params);
    }

    @JavascriptInterface
    public void logEvent(String name, String json) {
        if (name == null || !EVENTS.contains(name)) return;
        Bundle b = new Bundle();
        try {
            JSONObject o = new JSONObject(json == null ? "{}" : json);
            Iterator<String> it = o.keys();
            while (it.hasNext()) {
                String k = it.next();
                if (STRING_PARAMS.contains(k)) {
                    String v = String.valueOf(o.get(k));
                    b.putString(k, v.length() > 100 ? v.substring(0, 100) : v);
                } else if ("duration".equals(k)) {
                    b.putLong(k, o.getLong(k)); // seconds
                }
            }
        } catch (Exception ignored) {
            return;
        }
        analytics.logEvent(name, b);
    }

    @JavascriptInterface
    public void setAnalyticsEnabled(boolean enabled) {
        analytics.setAnalyticsCollectionEnabled(enabled);
        activity.getSharedPreferences("itihasa_prefs", Activity.MODE_PRIVATE)
                .edit().putBoolean("analytics_enabled", enabled).apply();
    }

    @JavascriptInterface
    public boolean isAnalyticsEnabled() {
        return activity.getSharedPreferences("itihasa_prefs", Activity.MODE_PRIVATE)
                .getBoolean("analytics_enabled", true);
    }

    /** Clears Firebase's local analytics data and app-instance ID (used when a profile is deleted). */
    @JavascriptInterface
    public void resetAnalytics() {
        analytics.resetAnalyticsData();
    }

    @JavascriptInterface
    public void share(final String text) {
        if (text == null || text.isEmpty()) return;
        activity.runOnUiThread(() -> {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_TEXT, text);
            activity.startActivity(Intent.createChooser(send, null));
        });
    }
}
