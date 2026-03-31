# LVHS Portal — Android Native App

Native Android wrapper for the Life Veneration High School management portal ([lvhs.ng](https://lvhs.ng)).

## Tech Stack

- Kotlin + Android SDK 34
- WebView with Hotwire/Turbo Native pattern
- Material 3 bottom navigation
- Swipe-to-refresh
- Deep link support

## Setup

1. Open in Android Studio
2. Sync Gradle
3. Build > Assemble
4. APK at `app/build/outputs/apk/debug/app-debug.apk`

## Configuration

| Setting | File | Current Value |
|---|---|---|
| App URL | `app/build.gradle.kts` → `BASE_URL` | `https://lvhs.ng` |
| Package | `app/build.gradle.kts` → `applicationId` | `com.lvhs.school` |
| App Name | `res/values/strings.xml` | `LVHS Portal` |
| Colors | `res/values/colors.xml` | Forest Green + Gold |

## Bottom Tabs

| Tab | Route | Purpose |
|---|---|---|
| Home | `/` | School website / landing |
| Portal | `/admin/dashboard` | Role-based dashboard |
| Alerts | `/profile` | Notifications & profile |
| Profile | `/profile` | User profile & settings |

## Distribution

APK is hosted at `https://lvhs.ng/download` for direct download. No Play Store required.
