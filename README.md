# Unswipe

Unswipe is a minimalist productivity and screen-time management Android app inspired by Minimalist Phone. It's designed to help users reduce doomscrolling, limit time spent on addictive apps like TikTok, Instagram, and YouTube, and build healthier screen habits through awareness and gentle friction.

## Table of Contents

*   [Project Goals](#project-goals)
*   [Key Features](#key-features)
*   [Technology Stack & Architecture](#technology-stack--architecture)
    *   [Frontend](#frontend)
    *   [Backend](#backend)
    *   [Build & Structure](#build--structure)
*   [Project Structure Overview](#project-structure-overview)
*   [Getting Started](#getting-started)
*   [Future Implementation / Roadmap](#future-implementation--roadmap)
*   [License](#license)

## Project Goals

*   **Reduce Mindless Scrolling:** Help users become aware of and reduce time spent on potentially addictive social media and video platforms.
*   **Promote Digital Wellbeing:** Encourage healthier screen habits by tracking usage patterns and providing context (e.g., usage near bedtime).
*   **Increase Productivity:** By limiting distractions during work or focus hours (inferred or explicitly set).
*   **Minimalist Approach:** Provide functionality without being intrusive or overly complex itself.
*   **Actionable Insights:** Offer simple data visualizations and feedback to motivate users.

## Key Features

### Core (Free Tier)

*   **Screen Time Tracking:** Monitors foreground usage time for selected applications using Android's `UsageStatsManager`.
*   **App Open & Unlock Tracking:** Logs how many times specific apps are opened and the device is unlocked.
*   **Swipe Count Estimation:** Uses Accessibility Service (with user permission) to *estimate* swipe gestures within specific target apps (Note: This is inherently fragile and requires careful implementation and user consent).
*   **Daily Dashboard:**
    *   Displays time used / time remaining against the daily threshold.
    *   Provides a simple weekly progress chart (e.g., bar chart of daily usage).
    *   Displays swipe and unlock counts for the day.
*   **App Launch Confirmation:** If enabled by the user for specific apps (e.g., TikTok, Instagram), presents a confirmation dialog ("Do you really want to open this?") before launching the app, adding a moment of friction.
*   **User Accounts:** Firebase Authentication for user registration and login (Email/Password).

### Premium Features (Subscription-based)

*   **Custom Time Limits:** Allow users to set custom daily usage thresholds (e.g., 1hr, 2hr, 4hr) instead of the default.
*   **Historical Analytics:** Access to detailed usage data and trends beyond the standard 7 days.
*   **Custom App Blocklists:** Allow users to add any installed app to the list requiring launch confirmation (not just the defaults).
*   **Dynamic Reminders:** (Future) Provide notifications or motivational messages based on usage patterns or goals.
*   **Cloud Backup & Sync:** Securely back up usage summaries and settings to Firestore, enabling potential multi-device sync in the future.

## Technology Stack & Architecture

### Frontend (Android App)

*   **Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose (declarative UI) with Material 3 design system.
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Core Jetpack Libraries:**
    *   ViewModel: Manage UI-related data in a lifecycle-conscious way.
    *   Navigation Compose: Handle navigation between screens.
    *   Room: Local database persistence for usage events, summaries, and potentially settings.
    *   DataStore (Preferences): Storing user settings like daily limits, premium status, blocked app list.
    *   WorkManager: Reliable background task execution for usage processing.
    *   Hilt: Dependency Injection.
*   **APIs:**
    *   `UsageStatsManager`: Track app foreground time (requires user permission).
    *   `AccessibilityService`: Detect swipes and app launches (requires explicit user permission and careful handling).
*   **Billing:** Google Play Billing Library (for handling subscriptions).

### Backend (Cloud Services)

*   **Platform:** Firebase (Backend-as-a-Service)
*   **Services:**
    *   **Firebase Authentication:** Handle user sign-up, login, and identity.
    *   **Firebase Firestore:** NoSQL database for storing user profile information (like premium status), backing up settings and aggregated usage summaries (for premium sync).
    *   **(Potential) Firebase Cloud Functions:** For securely verifying purchase tokens from Google Play Billing before granting premium status in Firestore.

### Build & Structure

*   **Build System:** Gradle with Kotlin DSL (`build.gradle.kts`).
*   **Modularity:** Code organized into distinct layers (`data`, `domain`, `ui`) for separation of concerns and maintainability.

## Project Structure Overview

*   `app/src/main/java/com/unswipe/android/`: Root package.
    *   `core/`: Base utilities, common code, potentially database/datastore setup helpers.
    *   `data/`: Handles all data operations.
        *   `local/`: Room database (DAO, entities, database class), DataStore helpers.
        *   `model/`: Data Transfer Objects (DTOs), Room Entities.
        *   `remote/`: Firebase interaction logic (Firestore, Auth).
        *   `repository/`: Implementations of repository interfaces (combining local/remote data sources).
        *   `workers/`: `WorkManager` workers (e.g., `UsageTrackingWorker`).
        *   `services/`: Android Services (e.g., `SwipeAccessibilityService`).
        *   `receivers/`: Broadcast Receivers (e.g., `UnlockReceiver`).
    *   `di/`: Hilt Dependency Injection modules (providing dependencies like Repositories, Daos, Firebase instances).
    *   `domain/`: Business logic, independent of Android framework.
        *   `model/`: Clean business objects/models used by UI and Use Cases.
        *   `repository/`: Interfaces defining data contracts required by the domain layer.
        *   `usecase/`: Encapsulates specific business logic operations.
    *   `ui/`: Presentation layer (Android framework dependent).
        *   Contains sub-packages for each feature/screen (`auth`, `dashboard`, `settings`, `confirmation`).
        *   `navigation/`: Navigation graph setup using Navigation Compose.
        *   `theme/`: Compose Material 3 theme definition (colors, typography, shapes).
        *   `common/`: Reusable Composable UI components.
        *   `MainActivity.kt`: The main entry point activity.
*   `app/src/main/res/`: Android resources (layouts (minimal), drawables, values, xml).
*   `app/google-services.json`: Firebase configuration file ( **Add this yourself!** ).
*   `app/proguard-rules.pro`: Rules for code shrinking and obfuscation.

## Getting Started

1.  **Clone the Repository:**
    ```bash
    git clone <your-repository-url>
    cd Unswipe
    ```
2.  **Firebase Setup (CRUCIAL):**
    *   Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com/).
    *   Register an Android app with the package name `com.unswipe.android`.
    *   Enable **Authentication** (Email/Password provider) and **Firestore Database** (start in test mode for development).
    *   Download the `google-services.json` config file from your Firebase project settings.
    *   Place the downloaded `google-services.json` file inside the `app/` directory of this project.
3.  **Open in Android Studio:** Open the cloned project directory in Android Studio (latest stable version recommended).
4.  **Gradle Sync:** Allow Android Studio to download dependencies and sync the project.
5.  **Build:** `Build` > `Make Project`.
6.  **Run:** Select an emulator or connect a device and click the "Run 'app'" button.
7.  **Permissions:** The app will require special permissions later:
    *   **Usage Access:** Needs to be granted manually via system settings (`ACTION_USAGE_ACCESS_SETTINGS`) for screen time tracking.
    *   **Accessibility Service:** Needs to be enabled manually via system settings (`ACTION_ACCESSIBILITY_SETTINGS`) for swipe counting and app launch confirmation. Provide clear explanations to the user why these are needed.

## UI/UX Design

Design assets are stored in the [`designs/`](designs) directory. The file
`dashboard-wireframe.fig` is a placeholder intended for future Figma designs.
You can replace it with the exported Figma file once a finalized dashboard
layout is ready.

## Future Implementation / Roadmap

*   **More Granular Controls:** Allow setting different limits for different apps or app categories.
*   **Scheduled Blocking:** Implement time windows (e.g., work hours, sleep hours) where specific apps are blocked or require stricter confirmation.
*   **Enhanced Analytics:** More detailed charts, insights into usage patterns over time (e.g., "most used app during evenings").
*   **Widget Support:** Add home screen widgets showing daily progress.
*   **Refine Swipe Detection:** Improve the accuracy and robustness of swipe detection via the Accessibility Service (an ongoing challenge).
*   **Cross-Device Sync:** Fully implement Firestore sync for premium users to see their settings across multiple devices.
*   **Goal Setting:** Allow users to set specific reduction goals beyond the daily limit.
*   **(Maybe) Social Accountability:** Optional features to share progress or goals with friends (privacy-focused).

*(This roadmap is tentative and subject to change based on development progress and user feedback)*

## License

[Choose a License - e.g., MIT, Apache 2.0]

Copyright (c) [Year] [Your Name or Organization Name]
