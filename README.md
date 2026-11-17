# Dormly — Android Studio Project

Kotlin + XML Android prototype for dormitory management.

## Requirements
- Android Studio (recent stable version)
- JDK 17
- Android SDK 35
- Minimum Android version: API 24

## Demo accounts
Password for all accounts: `dormly123`

| Role | Email |
|---|---|
| Admin | admin@dormly.com |
| Resident | resident@dormly.com |
| Maintenance | maintenance@dormly.com |

## Features
- Role-based demo login for Admin, Resident, and Maintenance
- Admin dashboard, resident/unit directory, request assignment, announcements
- Resident room overview, maintenance request submission, request tracking
- Maintenance assigned-task list, status updates, and work remarks
- Shared in-session sample request data across the three role views

## Open in Android Studio
Clone/download the repository and open its root folder in Android Studio. Let Gradle sync, then run the `app` configuration on an emulator or Android device.

## Build
In Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

Or, after Gradle sync, run:
```bash
gradle assembleDebug
```
APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Prototype data note
This classroom prototype stores sample records in memory for the current app process. Data resets after the app process is restarted. It is not connected to a remote server or production authentication system, and it does not yet synchronize across devices.

## GitHub Actions
The included workflow builds the debug APK on GitHub and uploads it as a downloadable workflow artifact when a commit is pushed to `main`.
