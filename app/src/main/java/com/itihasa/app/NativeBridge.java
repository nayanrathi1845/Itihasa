package com.itihasa.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Minimal bridge exposed to the bundled page as window.ItihasaNative.
 * Only the whitelisted events/parameters below can reach Firebase Analytics; no personal data
 * (name, email, search text) is ever sent to Analytics.
 * Accounts use Firebase Authentication (email + password). Results of the auth* calls are delivered
 * to the page through window.__authCb(callbackId, result).
 */
class NativeBridge {
    private static final Set<String> EVENTS = new HashSet<>(Arrays.asList(
            "app_open", "scripture_opened", "story_opened", "story_completed", "language_selected",
            "story_bookmarked", "search_performed", "reading_session", "share_clicked"));
    private static final Set<String> STRING_PARAMS = new HashSet<>(Arrays.asList(
            "app_version", "scripture_id", "story_id", "language", "search_category"));

    private final Activity activity;
    private final FirebaseAnalytics analytics;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private WebView webView;

    NativeBridge(Activity activity, FirebaseAnalytics analytics) {
        this.activity = activity;
        this.analytics = analytics;
    }

    void attach(WebView webView) {
        this.webView = webView;
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

    // ---------------------------------------------------------------- accounts (Firebase Authentication)

    private static boolean validCb(String cb) {
        return cb != null && cb.matches("c\\d{1,9}");
    }

    private static JSONObject userJson(FirebaseUser u) throws Exception {
        JSONObject o = new JSONObject();
        o.put("uid", u.getUid());
        o.put("email", u.getEmail() == null ? "" : u.getEmail());
        o.put("name", u.getDisplayName() == null ? "" : u.getDisplayName());
        return o;
    }

    private static String errorCode(Exception e) {
        if (e instanceof FirebaseNetworkException) return "network";
        if (e instanceof FirebaseTooManyRequestsException) return "too-many";
        if (e instanceof FirebaseAuthUserCollisionException) return "email-in-use";
        if (e instanceof FirebaseAuthWeakPasswordException) return "weak-password";
        if (e instanceof FirebaseAuthInvalidUserException) return "wrong-credentials";
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            String c = ((FirebaseAuthException) e).getErrorCode();
            return "ERROR_INVALID_EMAIL".equals(c) ? "invalid-email" : "wrong-credentials";
        }
        if (e instanceof FirebaseAuthException) {
            String c = ((FirebaseAuthException) e).getErrorCode();
            if ("ERROR_OPERATION_NOT_ALLOWED".equals(c)) return "not-enabled";
            if ("ERROR_TOO_MANY_REQUESTS".equals(c)) return "too-many";
            if ("ERROR_NETWORK_REQUEST_FAILED".equals(c)) return "network";
        }
        return "unknown";
    }

    private void reply(final String cb, final JSONObject result) {
        if (webView == null) return;
        final String js = "window.__authCb&&window.__authCb(" + JSONObject.quote(cb) + "," + result + ")";
        activity.runOnUiThread(() -> webView.evaluateJavascript(js, null));
    }

    private void replyError(String cb, Exception e) {
        try {
            reply(cb, new JSONObject().put("err", e == null ? "unknown" : errorCode(e)));
        } catch (Exception ignored) {
        }
    }

    private void replyUser(String cb, FirebaseUser u) {
        try {
            if (u == null) {
                replyError(cb, null);
            } else {
                reply(cb, userJson(u));
            }
        } catch (Exception ex) {
            replyError(cb, null);
        }
    }

    /** Current signed-in account as JSON, or "" when signed out. Works offline (Firebase keeps the session). */
    @JavascriptInterface
    public String authState() {
        try {
            FirebaseUser u = auth.getCurrentUser();
            return u == null ? "" : userJson(u).toString();
        } catch (Exception e) {
            return "";
        }
    }

    @JavascriptInterface
    public void authSignUp(final String cb, String email, String password, final String name) {
        if (!validCb(cb)) return;
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            replyError(cb, null);
            return;
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null || task.getResult().getUser() == null) {
                replyError(cb, task.getException());
                return;
            }
            final FirebaseUser u = task.getResult().getUser();
            String display = name == null ? "" : name.trim();
            if (display.length() > 20) display = display.substring(0, 20);
            if (display.isEmpty()) {
                replyUser(cb, u);
                return;
            }
            final String shown = display;
            u.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(shown).build())
                    .addOnCompleteListener(t2 -> {
                        // Even if saving the display name fails, the account exists: report it with the name.
                        try {
                            reply(cb, userJson(u).put("name", shown));
                        } catch (Exception ex) {
                            replyError(cb, null);
                        }
                    });
        });
    }

    @JavascriptInterface
    public void authSignIn(final String cb, String email, String password) {
        if (!validCb(cb)) return;
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            replyError(cb, null);
            return;
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                replyUser(cb, task.getResult().getUser());
            } else {
                replyError(cb, task.getException());
            }
        });
    }

    @JavascriptInterface
    public void authSignOut() {
        auth.signOut();
    }

    @JavascriptInterface
    public void authReset(final String cb, String email) {
        if (!validCb(cb)) return;
        if (email == null || email.isEmpty()) {
            replyError(cb, null);
            return;
        }
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            Exception e = task.getException();
            // Do not reveal whether an account exists for this address.
            if (task.isSuccessful() || e instanceof FirebaseAuthInvalidUserException) {
                reply(cb, new JSONObject());
            } else {
                replyError(cb, e);
            }
        });
    }

    /** Deletes the signed-in account after confirming the password again. */
    @JavascriptInterface
    public void authDelete(final String cb, String password) {
        if (!validCb(cb)) return;
        final FirebaseUser u = auth.getCurrentUser();
        if (u == null || u.getEmail() == null || password == null || password.isEmpty()) {
            replyError(cb, null);
            return;
        }
        AuthCredential cred = EmailAuthProvider.getCredential(u.getEmail(), password);
        u.reauthenticate(cred).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                replyError(cb, task.getException());
                return;
            }
            u.delete().addOnCompleteListener(t2 -> {
                if (t2.isSuccessful()) {
                    auth.signOut();
                    reply(cb, new JSONObject());
                } else {
                    replyError(cb, t2.getException());
                }
            });
        });
    }
}
