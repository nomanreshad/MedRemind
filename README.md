![License](https://img.shields.io/badge/License-MIT-green.svg)

# MedRemind Project Summary

<img width="200" height="200" alt="AppIcon~ios-marketing" src="https://github.com/user-attachments/assets/047d143b-4456-4c6d-aa9b-fe8f300c7a72" />

## Overview
MedRemind is a Medication Reminder application built as a Kotlin Multiplatform project targeting Android and iOS platforms. It uses Compose Multiplatform for the shared UI, enabling a largely shared codebase across both platforms. The app is **offline-first**, meaning all core functionality works without an internet connection. An internet connection is only required for AI-powered prescription scanning features. The app helps users manage their medications, track reminders for doses, and offers rich features such as dose scheduling, reminders, prescription and medication image management, and theme customization.

## Demo Video
https://github.com/user-attachments/assets/055ed58f-1931-44c8-8be3-e06d2426856a

## Features
- **Medication Management:** Add, view, edit, and delete medications with support for name, dosage, frequency, type, notes, doctor/hospital info, images, and date range.
- **Smart Reminders:** Schedules reminders based on user-defined medication frequency and time, with platform-specific alarm/notification integration for both Android and iOS.
- **Intelligent Image Uploads:** Upload and attach prescription and medication images. Optionally use AI-driven prescription scanning for auto-filling medication data (requires internet connection). Configure AI analysis language in settings to match prescription language, use device language, or specify a preferred language.
- **Multi-Select & Bulk Delete:** Long-press on medication list items to enable multi-select mode, allowing users to select multiple medications and delete them at once.
- **Swipe-to-Delete:** Swipe left or right on any medication item in the list to quickly delete it, with an undo option to restore if deleted accidentally.
- **Filtering and Search:** Filter medications by time of day, frequency, or via search. View all, or only morning/afternoon/evening/night/interval meds, or by custom time slot.
- **Multi-Language Support:** 
  - **App UI Language:** Supports 3 languages for the app interface - English, Bangla (Bengali), and German.
  - **AI Prescription Analysis Language:** Configurable language setting for AI prescription scanning with options: "Match Prescription" (auto-detect), "Device Language" (translate to device language), or specific languages (English, Bangla, German).
- **Settings & Personalization:** Supports dark/light/system themes, multiple contrast modes for accessibility, app UI language selection, and AI analysis language configuration.

## Code Structure
The project follows the recommended Kotlin Multiplatform structure:

```
- composeApp    # Shared Compose Multiplatform module
  |-- src
    |-- commonMain   # Shared business logic, UI, domain, platform-agnostic code
    |-- androidMain  # Android-specific implementations
    |-- iosMain      # iOS-specific implementations
- iosApp        # iOS entry-point SwiftUI wrapper
```

Key Shared Modules:
- `app/` — Main App Composable, navigation, splash screen, and theming.
- `core/` — Common UI components, enums, error handling, notification contracts, utilities.
- `data/` — Data layer with DAOs, repositories, local database, data mappers, network and AI modules (prescription extraction, storage, etc).
- `domain/` — Domain models (such as `Medication`), repository contracts, business rules.
- `presentation/` — UI screens (Add/Edit, List, Detail, Reminder, Settings) and state management.

Platform-specific (`androidMain`, `iosMain`):
- Android: Implements notification/alarms (`PlatformReminderScheduler.android.kt`), storage interfaces, entrypoint (`MainActivity.kt`),resource linking etc.
- iOS: Binds native iOS features (`PlatformReminderScheduler.ios.kt`), provides native interface hooks (`MainViewController.kt`), etc.

The iOS app code (`iosApp/`) wraps the shared Compose code in a SwiftUI application shell.

## Dependencies and Architecture

MedRemind follows the **MVI (Model View Intent)** architecture for state management, ensuring a predictable and testable flow.

### Core Dependencies Used:
- **JetBrains Compose Multiplatform** — Multiplatform UI toolkit for Android & iOS
- **Koin** — Multiplatform Dependency Injection
- **Kotlin Coroutines** — Asynchronous programming
- **Kotlinx Serialization** — Multiplatform JSON serialization
- **SQLDelight** — Multiplatform database layer
- **Calf** — Permissions and file pickers
- **Coil** — Modern, fast image loading for Compose (Android)
- **Google Material3** — Material Design components for Compose
- **KMP-NativeCoroutines** — Kotlin Flows to Swift
- **BuildKonfig** — Access build-time constants (e.g., API key)
- **Other Standard Kotlin/JetBrains/Compose libraries**

_(You may find more in the `build.gradle.kts` and gradle module files as platform or utility dependencies.)_

## API Key and Custom AI Model Setup
To use AI-powered prescription scanning, you must provide an API key and model name.

1. **Open `local.properties` in the project root.**
2. **Add your AI API key:**
   ```
   AI_API_KEY=your-api-key-here
   ```
3. **Example:**
   ```
   AI_API_KEY=AIza....YOURKEY...
   ```
4. **After adding your API key, you must build the project for the key to be applied.**
5. **Do not share or check this file into version control.**

### Use Any AI Provider
- You can use any AI service for prescription extraction.
- For this, provide the corresponding API key in `local.properties` and specify the AI model name in the code at:
  - `composeApp/src/commonMain/kotlin/com/nomanhassan/medremind/di/Modules.kt`
- Edit the following line to set your custom model:
   ```kotlin
   AIPrescriptionDataExtractor(
       modelName = "your-model-name-here",
       apiKey = get(AI_API_KEY_QUALIFIER)
   )
   ```
- **Example for modelName:**
   - For Google's Gemini Flash model (default in this repo):
     ```kotlin
     modelName = "gemini-2.5-flash"
     ```
   - For GPT-4o (if integrated):
     ```kotlin
     modelName = "gpt-4o"
     ```
   - Replace with your model string as required by your AI provider.
- Make sure both values match your target AI provider and build the project after making changes.

## How to Use
1. **Add Medications:** Use the list screen to add new meds, set details and schedules. You can add images for reference.
2. **Edit/Delete Medications:** Tap any list item to view details, edit, or delete.
3. **Swipe-to-Delete:** Swipe left or right on any medication item to quickly delete it. Use the undo option if you deleted it by mistake.
4. **Multi-Select & Delete:** Long-press on any medication item in the list to enable multi-select mode and select multiple medications for deletion.
5. **Acknowledge Reminders:** When notifications appear (alarms/reminders), tap to view due medications and acknowledge after taking your doses.
6. **Settings:** Access via the app bar/settings icon. Change theme, contrast modes, app UI language (English, Bangla, or German), and AI prescription analysis language (Match Prescription, Device Language, or specific languages).

## Build and Run on Each Platform
### Android
- **From IDE:**
  1. Open the project in Android Studio.
  2. Make sure your API key is present in `local.properties` ([see above](#api-key-and-custom-ai-model-setup)).
  3. Select the 'composeApp' run configuration and launch on an Android device or emulator.
- **From Terminal:**
  1. Ensure you have set your API key in `local.properties`.
  2. After adding the key, build the project by running:
        - on macOS/Linux
          ```shell
          ./gradlew :composeApp:assembleDebug
          ```
        - on Windows
          ```shell
          .\gradlew.bat :composeApp:assembleDebug
          ```
- **Install APK:** The APK can be found in `composeApp/build/outputs/apk/debug/` after building.

### iOS
- **From IDE:**
  1. Ensure your API key is in `local.properties` before building shared code.
  2. Open `iosApp/iosApp` in Xcode and build/run on a simulator or real device.
- **Or:** Use the run configuration in Android Studio with the KMM plugin and pick an iOS target. Make sure the local.properties file contains your AI API key before syncing/building.

## Additional Information
- **Shared State & Dependency Injection:** Uses Koin for multiplatform DI.
- **UI/UX:** Adopts Material Design 3 with support for responsive layouts, accessibility, and themed assets/icons.
- **Persistence:** Data is stored with platform-native databases via Kotlin Multiplatform libraries.
- **Alarm & Notification Handling:** Each platform has its own scheduler and receiver—alarms are scheduled natively per OS, but presented in a unified way to users.
