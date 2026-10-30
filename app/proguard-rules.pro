# The page calls these methods through window.ItihasaNative (WebView JavaScript interface).
-keepclassmembers class com.itihasa.app.NativeBridge {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface
