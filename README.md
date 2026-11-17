# Dormly — Android High-Fidelity Classroom Prototype

Dormly is a Kotlin + XML Android prototype for residential management with three role-based demo accounts.

## Technology
- Android Studio, Kotlin, XML layouts
- SQLite local database
- Minimum Android API 24; compile/target SDK 35
- JDK 17 and Android Gradle Plugin 8.7.3

## Demo accounts
Password for all accounts: dormly123

| Role | Email |
|---|---|
| Building Admin | admin@dormly.com |
| Resident | resident@dormly.com |
| Maintenance Personnel | maintenance@dormly.com |

## Functional features
- Role-based login and dashboards
- Resident maintenance request submission and tracking
- Admin request review, staff assignment, and status management
- Maintenance team task queue, status updates, and work remarks
- Resident and housing-unit directory
- Admin announcements, stored and displayed from SQLite
- Requests and announcements persist after the app is closed and reopened

## Demonstration workflow
1. Sign in as Resident and submit a maintenance request.
2. Sign in as Admin, open Requests, and assign the new request.
3. Sign in as Maintenance Personnel, open Requests, and update the task to In Progress or Completed with a work note.
4. Sign back in as Resident and confirm the updated request status is visible.

All three roles share the same local SQLite database on one device. The prototype does not synchronize across separate phones and uses classroom demo credentials rather than production authentication.

## Open in Android Studio
1. Download the repository ZIP or clone this repository.
2. Open the extracted Dormly folder in Android Studio.
3. Use JDK 17 and allow Gradle sync to finish.
4. Run the app configuration on an Android emulator or device.

## Build APK
In Android Studio, select Build > Build Bundle(s) / APK(s) > Build APK(s). The debug APK is normally created at app/build/outputs/apk/debug/app-debug.apk.

A GitHub Actions workflow is included at .github/workflows/android-build.yml. It builds the debug APK and packages the project source ZIP as workflow artifacts when changes are pushed to main.
