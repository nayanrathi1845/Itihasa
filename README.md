# Itihasa — Android app

A WebView wrapper around the Itihasa web app. All web files live in `app/src/main/assets/` and load offline from the APK.

## Get the APK (no Android Studio needed)
1. Create a new GitHub repository and upload everything in this folder. The `.github` folder is hidden and drag-and-drop often skips it; if the Actions tab shows no workflow, use "Add file → Create new file", name it `.github/workflows/build-apk.yml`, and paste in the contents of that file.
2. Open the repo's **Actions** tab → **Build Android APK** → wait about 3–5 minutes.
3. Open the finished run, download the **Itihasa-APK** artifact (a zip), and unzip it to get `app-debug.apk`.
4. Copy it to your phone and install it (allow "Install unknown apps" when asked).

Run the workflow again any time from Actions → Build Android APK → Run workflow.

## Or build in Android Studio
Open this folder in Android Studio → Build → Build Bundle(s) / APK(s) → Build APK(s).

## Adding your images later
Put them in `app/src/main/assets/assets/images/`, reference them with relative paths such as `assets/images/ramayana-hero.jpg` in `app/src/main/assets/index.html`, and push (or rebuild).
Keep the web copy in sync: `index.html` here is the same file as the PWA version.

## Notes
- The APK is a *debug* build: it installs and runs fine, but it is not for the Play Store. A signed release build needs your own keystore.
- Back button returns to Home first, then exits.
- The app needs no internet permission.
