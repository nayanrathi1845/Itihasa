# Story illustrations (Firebase Hosting)

`public/img/` holds every story illustration (WebP). The app loads them from
`https://itihasa-9c5bf.web.app/img/<name>.webp` and keeps a copy on the phone after the first view.

Deployed by `.github/workflows/deploy-hosting.yml` on every push that changes `hosting/`.
The workflow needs the repository secret `FIREBASE_SERVICE_ACCOUNT` (a Firebase service-account key, JSON).

File names never change meaning: to replace a picture, add it under a NEW name and update `storyImgs`
in `app/src/main/assets/index.html` (files are cached for a year).
