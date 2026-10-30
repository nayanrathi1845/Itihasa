# Itihasa: Google Play launch pack (Stage 5)
Developer name: NR1845 · Contact: nayan.rathi1845@gmail.com · Package: com.nr1845.itihasa · Version 1.0.0 (code 3)
Policy and deletion pages (publish via GitHub Pages first, see step 1): https://nayanrathi1845.github.io/Itihasa/privacy.html and https://nayanrathi1845.github.io/Itihasa/delete-data.html

> I am not a lawyer. The privacy policy is a good-faith description of what the app actually does; please read it once and confirm you are comfortable with it.

## 0. Order of work (what needs YOU)
1. **Enable GitHub Pages** (I was blocked from doing this): repo → Settings → Pages → Source "Deploy from a branch" → Branch `main`, folder `/docs` → Save. After about a minute open the two URLs above to confirm they load.
2. **Create the Play Developer account** (Personal, one-time $25, identity verification). The 14-day closed test below is the longest wait, so do this first.
3. In Play Console: Create app → name, default language English (United Kingdom/US), App, Free. Accept the declarations.
4. **Set up the app** (Dashboard checklist): use sections 3–7 below.
5. **Closed testing → Create track → upload `Itihasa-release.aab`** (let Play manage app signing; your key is the *upload* key), add testers (section 8), roll out.
6. Keep 12+ testers opted in for 14 continuous days, then **Apply for production access** in the Dashboard, then create the production release.
7. Firebase console clean-up (section 9).

## 1. Store listing: English
**App name (25/30):** Itihasa: Indian Mythology
**Short description (71/80):** Illustrated Ramayana stories in English & Hindi. Free, offline reading.
**Full description (1337/4000):**

Itihasa is a calm, beautiful place to read the stories of India's epics, one story at a time.

Start with the Ramayana: 40 short stories told across the six Kandas, from the birth of Rama in Bala Kanda to the return to Ayodhya, with illustrations for most stories.

WHAT YOU CAN DO
• Read in English or Hindi and switch whenever you like
• Swipe up for the next story and down for the previous one, like reels
• Bookmark favourite stories and pick up where you left off
• Read fully offline: every story is stored in the app
• Browse the scripture library: Ramayana is available now; Mahabharata, Bhagavata Purana, Shiva Purana, Vishnu Purana, the Upanishads, the Vedas, the Bhagavad Gita and more are marked "Coming soon"

ABOUT THE STORIES
These are concise retellings, not translations. The main narrative follows the broad sequence of Valmiki's Ramayana, and each story is written to be read in about two minutes.

YOUR PRIVACY
Create a simple profile on your phone to save your bookmarks and progress. Your profile never leaves your device. Itihasa sends only anonymous usage statistics (for example which stories are opened) to Google Firebase Analytics, and you can switch this off any time in Profile. This version has no advertisements.

Itihasa is made for curious readers who want to rediscover the stories that shaped Bharat.

**Category:** Books & Reference · **Tags (pick from list):** Education, Reference, Religion/Culture if offered
**Email:** nayan.rathi1845@gmail.com · **Website:** https://nayanrathi1845.github.io/Itihasa/ · **Privacy policy:** https://nayanrathi1845.github.io/Itihasa/privacy.html

## 2. Store listing: Hindi (add language हिन्दी)
**App name (28/30):** इतिहास: भारतीय पौराणिक कथाएँ
**Short description (70/80):** चित्रों सहित रामायण की कथाएँ, हिन्दी व अंग्रेज़ी में। मुफ़्त, ऑफ़लाइन।
**Full description (1203/4000):**

इतिहास भारत के महाकाव्यों की कथाएँ पढ़ने की एक शांत और सुंदर जगह है, एक-एक कथा करके।

शुरुआत रामायण से करें: बालकाण्ड में राम के जन्म से लेकर अयोध्या वापसी तक, छह काण्डों में कही गई 40 छोटी कथाएँ, अधिकांश कथाओं के साथ चित्र।

आप क्या कर सकते हैं
• हिन्दी या अंग्रेज़ी में पढ़ें और जब चाहें भाषा बदलें
• अगली कथा के लिए ऊपर और पिछली के लिए नीचे स्वाइप करें, रील की तरह
• पसंदीदा कथाओं को सहेजें और जहाँ छोड़ा था वहीं से आगे पढ़ें
• पूरी तरह ऑफ़लाइन पढ़ें: हर कथा ऐप में ही मौजूद है
• ग्रंथ-पुस्तकालय देखें: रामायण अभी उपलब्ध है; महाभारत, भागवत पुराण, शिव पुराण, विष्णु पुराण, उपनिषद, वेद, भगवद्गीता और अन्य "जल्द आ रहे हैं"

कथाओं के बारे में
ये संक्षिप्त पुनर्कथन हैं, अनुवाद नहीं। मुख्य कथा वाल्मीकि रामायण के व्यापक क्रम का अनुसरण करती है, और हर कथा लगभग दो मिनट में पढ़ी जा सकती है।

आपकी गोपनीयता
अपने बुकमार्क और प्रगति सहेजने के लिए अपने फ़ोन पर एक सरल प्रोफ़ाइल बनाएँ। आपकी प्रोफ़ाइल कभी आपके डिवाइस से बाहर नहीं जाती। इतिहास केवल गुमनाम उपयोग-आँकड़े (जैसे कौन-सी कथाएँ खोली गईं) Google Firebase Analytics को भेजता है, जिन्हें आप प्रोफ़ाइल में कभी भी बंद कर सकते हैं। इस संस्करण में कोई विज्ञापन नहीं है।

इतिहास उन जिज्ञासु पाठकों के लिए है जो भारत को गढ़ने वाली कथाओं को फिर से जानना चाहते हैं।

## 3. Graphics (all in the repo folder `store-assets/`)
- App icon 512×512: `play-icon-512.png`
- Feature graphic 1024×500: `feature-graphic-1024x500.png`
- Phone screenshots (1080×1920): `screenshots/` (01 home, 02 library, 03 Ramayana Kandas, 04 story, 04b story text, 05 saved, 06 Hindi reader, 07 profile). **Note:** these are renders of the app's own screens produced in a phone-sized browser window, not captures from a physical phone. They show the real UI and content. If you prefer, replace them with real phone screenshots (Play needs at least 2). Suggested upload order: 04, 06, 03, 01, 05.
- Tablet screenshots: optional.

## 4. App content declarations (answers to give)
- **App access:** Some functionality needs a profile. Instructions for reviewers: "Open the app, choose Create profile, and enter any username (3+ characters), any email address and any password of 8+ characters. Nothing is verified or sent anywhere; the profile exists only on the device."
- **Ads:** No, the app contains no ads.
- **Advertising ID:** the app does not use it (permission removed from the manifest).
- **Target audience:** recommended **13+ (13–15, 16–17, 18+)**. I do not recommend selecting under-13: that triggers Google's Families policy with stricter SDK and data rules which I have not verified for Firebase Analytics. Risk: an illustrated story app can appeal to children, so Google may question a 13+ choice; the decision is yours.
- **Content rating (IARC questionnaire):** answer honestly: the stories and illustrations include mythological battles, killing in war and an abduction, told in a non-graphic way; no sex, drugs, gambling, user-to-user communication, location sharing or purchases. Google/IARC assigns the rating; do not guess it.
- **News / health / financial / government / COVID apps:** No to all.
- **Data deletion:** In-app: Profile → Delete profile. Web link to enter: https://nayanrathi1845.github.io/Itihasa/delete-data.html

## 5. Data safety form (recommended answers)
Google's own pages I read did not give an exact Firebase-to-form mapping, so I chose to **over-disclose** (safer than under-disclosing). Follow Play Console's current wording if it differs.
| Question | Answer |
|---|---|
| Does the app collect or share required data types? | Yes, collects. **Shares: No** (Google acts as our service provider for analytics) |
| Personal info (name, email, user IDs) | **Not collected.** Username/email/password stay on the device and are never transmitted (Google defines "collect" as sending data off the device) |
| App activity → App interactions | Collected · not shared · optional (user can switch it off) · purpose: Analytics |
| Device or other IDs (Firebase app-instance ID) | Collected · not shared · optional · purpose: Analytics |
| Location → Approximate location (derived from IP by Firebase) | Collected · not shared · optional · purpose: Analytics |
| Financial, health, messages, photos, contacts, audio, files, calendar | Not collected |
| Is all data encrypted in transit? | Yes (TLS) |
| Can users request data deletion? | Yes: in-app Delete profile + web page + email |
| Independent security review / Families policy | No |

## 6. Release notes (first release)
First release: 40 Ramayana stories in English and Hindi, with illustrations, bookmarks and reading progress. Works offline.

## 7. Console technical items
- Upload `Itihasa-release.aab` (from GitHub release `rc-1`; for any later upload, **raise versionCode in `app/build.gradle` first**, then run the Release workflow again).
- Keep **Play App Signing** on (default). After first upload Play shows its own app-signing certificate; the upload key stays with you (`itihasa-upload.jks`).
- Countries: start with India (add more later if you wish).

## 8. Closed testing guide (for your 12+ testers)
Send each tester this message once the track is live (replace the link with the opt-in link Play Console gives you):
> Hi! I'm testing my Ramayana stories app, Itihasa, and need testers. 1) Use the Google account whose email I added. 2) Open this link and tap "Become a tester": [OPT-IN LINK]. 3) Install Itihasa from the Play Store link on that page. 4) Please open it a few times over the next two weeks and **don't leave the test early**, it must stay on for 14 days. 5) Reply with anything confusing, slow or broken.
What testers should try: create a profile; read 3+ stories (swipe up/down); switch English/Hindi; save a story and close and reopen the app; turn on airplane mode and read offline; share a story; Profile → delete profile.
Tips: add testers by email list or a Google Group; Google needs 12+ opted in for 14 consecutive days (Play Help, closed testing); recruit 15–20 in case some drop out.

## 9. Firebase console (manual)
- Analytics → Admin (gear) → **Custom definitions**: add custom dimensions story_id, scripture_id, language, search_category, app_version (event-scoped) and custom metric duration (seconds).
- Admin → Data collection and modification: keep **Google signals OFF** (no ad personalisation); Data settings → **Data retention**: 2 or 14 months (policy allows up to 14).
- Use **DebugView** (adb setprop debug.firebase.analytics.app com.nr1845.itihasa) to check events from the release build.
- Optional, last: restricting the API key in Google Cloud is not required for Analytics; if you do, use the Play app-signing certificate (not the upload key) or analytics may stop.

## 10. Needs decisions or later
- 2 of 40 stories (38 The Fall of Ravana, 40 Rama's Return to Ayodhya) have no illustration yet; the app handles this, and the listing says "most stories".
- Optional later: Crashlytics (adds more data to disclose), more screenshots, tablet layout.
