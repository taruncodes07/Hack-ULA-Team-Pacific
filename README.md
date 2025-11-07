# MyApplication2 - Guest Authentication App

A modern Android application built with Jetpack Compose featuring a complete authentication flow
with splash screen, phone verification, and OTP validation.

## Features

### Design & Theme
- **Color Scheme:**
    - Black background (#000000)
    - Purple text and icons (#A020F0)
    - Green action buttons (#00C853)

- **Typography:**
    - Heading font: Brooklyn (with Serif fallback)
    - Content font: Receptive (with SansSerif fallback)

- **Animations:**
    - Button hover and click animations with spring physics
    - Smooth fade in/out transitions
    - Scale animations for interactive elements
    - Success animation with green checkmark

### App Flow

1. **Splash Screen (2 seconds)**
    - Displays app logo with fade in/out animation
    - Automatically transitions to login selection

2. **Login Selection Screen**
    - Two buttons: "Login" (placeholder) and "Guest"
    - Animated button interactions (press and hover effects)
    - Clicking "Guest" navigates to authentication

3. **Guest Authentication Screen**
    - Phone number input (10 digits)
    - Validation for correct phone format
    - OTP input (6 digits) - fades in after phone verification
    - "Verify" button converts to "Resend OTP" button
    - Test OTP: `123456`
    - Green success animation on verification
    - Retrieves user profile from database

4. **Guest Main Page**
    - Displays welcome message
    - Shows user profile information (Name, Phone, Email, Address)
    - Profile data loaded from saved file

## Test Data

The app includes two test user profiles:

| Phone Number | Name | Email | Address |
|-------------|------|-------|---------|
| 1234567890 | John Doe | john.doe@example.com | 123 Main Street, City, Country |
| 9876543210 | Jane Smith | jane.smith@example.com | 456 Oak Avenue, Town, Country |

**Test OTP:** `123456`

## Technical Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Navigation:** Navigation Compose
- **Data Storage:** Local file storage with Gson
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36

## Project Structure

```
app/src/main/java/com/example/myapplication2/
├── data/
│   ├── UserProfile.kt          # User data model
│   └── UserRepository.kt       # User data management
├── navigation/
│   ├── NavRoutes.kt           # Navigation routes
│   └── NavGraph.kt            # Navigation graph setup
├── screens/
│   ├── SplashScreen.kt        # Splash screen with animation
│   ├── LoginSelectionScreen.kt # Login/Guest selection
│   ├── GuestAuthScreen.kt     # Phone & OTP authentication
│   └── GuestMainPage.kt       # Main app page
├── ui/theme/
│   ├── Color.kt               # App color definitions
│   ├── Theme.kt               # Material theme configuration
│   └── Type.kt                # Typography definitions
└── MainActivity.kt            # Main activity entry point
```

## Setup & Running

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or higher
- Android SDK 36

### Steps

1. **Open the project in Android Studio**
   ```
   File > Open > Select MyApplication2 folder
   ```

2. **Sync Gradle dependencies**
    - Android Studio will automatically prompt to sync
    - Or click: File > Sync Project with Gradle Files

3. **Add Custom Fonts (Optional)**
   To use Brooklyn and Receptive fonts:
    1. Place font files (.ttf or .otf) in `app/src/main/res/font/`
    2. Update `Type.kt` to reference the actual font files
    3. Currently using system fonts as fallback

4. **Run the app**
    - Connect an Android device or start an emulator
    - Click Run button or press Shift+F10
    - Select your target device

### Building APK
```bash
./gradlew assembleDebug
```
The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

## Key Dependencies

```kotlin
implementation("androidx.navigation:navigation-compose:2.7.5")
implementation("androidx.compose.material:material-icons-extended:1.5.4")
implementation("com.google.code.gson:gson:2.10.1")
```

## User Profile Storage

User profiles are saved to local storage as JSON:
- File name: `current_profile`
- Location: App's internal files directory
- Format: JSON serialization using Gson

## Animation Details

### Button Animations
- **Press:** Scales to 95% with spring physics
- **Hover:** Scales to 105% with spring physics
- **Spring Settings:** Medium bouncy damping, low stiffness

### Screen Transitions
- **Fade In/Out:** 500-1000ms duration
- **Success Animation:** Spring-based scale from 0 to 1

### Splash Screen
- **Fade In:** 1000ms
- **Display:** 2000ms total
- **Fade Out:** 1000ms before transition

## Phone Number Validation

- Must be exactly 10 digits
- Only numeric characters allowed
- Real-time validation feedback

## OTP Verification

- Test OTP: `123456`
- Validates against hardcoded test value
- In production, integrate with SMS OTP service

## Future Enhancements

- Actual Login functionality
- Real SMS OTP integration
- Backend API integration
- Additional user profile fields
- Profile editing capabilities
- Biometric authentication

## Notes

- The Login button is currently a placeholder
- User database is simulated with in-memory data
- Phone numbers must match test data to proceed past OTP
- Custom fonts (Brooklyn, Receptive) require manual installation

## License

This project is for educational/demonstration purposes.

## Contact

For questions or issues, please open an issue in the repository.
