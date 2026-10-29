# Itihasa — Android app

A WebView wrapper around the Itihasa web app. All web files live in `app/src/main/assets/` and load offline from the APK.

## Get the APK (no Android Studio needed)
1. Create a new GitHub repository and upload everything in this folder. The `.github` folder is hidden and drag-and-drop often skips it; if the Actions tab shows no workflow, use "Add file → Create new file", name it `.github/workflows/build-apk.yml`, and paste in the contents of that file.
2. Open the repo's **Actions** tab → **Build Android APK** → wait about 3–5 minutes.
3. Open the finished run, open the repo's **Releases** page and download `Itihasa.apk`.
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

## Profiles
On first launch the app asks the user to create a profile (username, email, password). Profiles, bookmarks, reading progress and language are stored **on the device only** (browser/WebView local storage); passwords are stored as salted hashes (PBKDF2-SHA-256, with a SHA-256 fallback). There is no server, so there is no password reset and data does not sync across devices. To sync across devices later, the profile code (`doSignup`, `doLogin`, `saveData` in `index.html`) is the part to point at a backend.

## Reading
Stories are read like reels: swipe up (or scroll / press ↓) at the end of a story for the next one, swipe down at the top for the previous one.
