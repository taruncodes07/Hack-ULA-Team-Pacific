# Build APK - Complete Guide

## 🎯 How to Convert Your App to APK

There are 3 ways to build an APK:

---

## Method 1: Using Android Studio GUI (EASIEST)

### Step 1: Open Your Project

```
Open Android Studio
File → Open → Select your project folder
Wait for Gradle sync to complete
```

### Step 2: Build APK

```
1. Click "Build" in the top menu
2. Select "Build Bundle(s) / APK(s)"
3. Click "Build APK(s)"
4. Wait for build to complete (you'll see a notification)
```

### Step 3: Locate Your APK

```
After build completes, click "locate" in the notification
OR
Navigate to: app/build/outputs/apk/debug/app-debug.apk
```

**APK Location:**

```
C:\Users\ROHAN MATHAD\StudioProjects\Hack-ULA-Team-Pacific\app\build\outputs\apk\debug\app-debug.apk
```

---

## Method 2: Using Gradle Command Line (FAST)

### For Debug APK (Testing):

```powershell
cd "C:\Users\ROHAN MATHAD\StudioProjects\Hack-ULA-Team-Pacific"
.\gradlew assembleDebug
```

**Output:**

```
app/build/outputs/apk/debug/app-debug.apk
```

### For Release APK (Production):

```powershell
cd "C:\Users\ROHAN MATHAD\StudioProjects\Hack-ULA-Team-Pacific"
.\gradlew assembleRelease
```

**Output:**

```
app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## Method 3: Generate Signed Release APK (For Play Store)

### Step 1: Generate Signing Key

In Android Studio:

```
1. Build → Generate Signed Bundle / APK
2. Select "APK"
3. Click "Create new..." (under Key store path)
4. Fill in the details:
   - Key store path: Choose where to save (e.g., C:\keystore\my-release-key.jks)
   - Password: Create a strong password
   - Key alias: myapp-key
   - Key password: Same or different password
   - Validity: 25 years (default)
   - Certificate info: Fill your details
5. Click "OK"
6. Save your keystore file securely!
```

### Step 2: Build Signed APK

```
1. Select your keystore file
2. Enter passwords
3. Select "release" build variant
4. Check "V1 (Jar Signature)" and "V2 (Full APK Signature)"
5. Click "Finish"
```

**Output:**

```
app/release/app-release.apk
```

---

## 🎯 Quick Command Reference

### Debug APK (For Testing):

```powershell
cd "C:\Users\ROHAN MATHAD\StudioProjects\Hack-ULA-Team-Pacific"
.\gradlew assembleDebug
```

**File:** `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (Unsigned):

```powershell
.\gradlew assembleRelease
```

**File:** `app/build/outputs/apk/release/app-release-unsigned.apk`

### Clean Build (Start Fresh):

```powershell
.\gradlew clean assembleDebug
```

---

## 📱 APK Types Explained

| Type | Purpose | Can Install? | Play Store? |
|------|---------|--------------|-------------|
| **Debug APK** | Testing, development | ✅ Yes | ❌ No |
| **Release Unsigned** | Testing | ⚠️ Yes (with settings) | ❌ No |
| **Release Signed** | Production | ✅ Yes | ✅ Yes |

---

## 🔧 Build Configuration

### Check Your Build Settings

**File:** `app/build.gradle.kts` (or `app/build.gradle`)

```kotlin
android {
    defaultConfig {
        applicationId = "com.example.myapplication2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(...)
        }
    }
}
```

---

## 📦 Build Sizes

**Expected Sizes:**

| Type | Approximate Size |
|------|------------------|
| Debug APK | 15-25 MB |
| Release APK (unsigned) | 10-20 MB |
| Release APK (signed) | 10-20 MB |

**Note:** AI model NOT included (downloads at runtime) ✓

---

## 🧪 Testing Your APK

### Install on Your Phone:

**Method 1: USB Cable**

```powershell
# Connect phone via USB, enable USB debugging
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Method 2: File Transfer**

```
1. Copy APK to your phone (USB/Bluetooth/Drive)
2. Open the APK file on your phone
3. Allow "Install from Unknown Sources" if prompted
4. Install and open
```

**Method 3: Email/Drive**

```
1. Send APK to yourself via email/Google Drive
2. Download on phone
3. Install
```

---

## ⚠️ Common Issues & Solutions

### Issue: "Gradle sync failed"

**Solution:**

```powershell
cd "C:\Users\ROHAN MATHAD\StudioProjects\Hack-ULA-Team-Pacific"
.\gradlew clean
.\gradlew --refresh-dependencies
```

### Issue: "Build failed with errors"

**Solution:** Check logcat for specific errors

```
1. Open Android Studio
2. View → Tool Windows → Build
3. Read error messages
4. Fix issues (usually dependency conflicts)
```

### Issue: "APK won't install on phone"

**Solutions:**

- Enable "Install from Unknown Sources" in phone settings
- Make sure you have enough storage space
- Uninstall old version first if updating
- Use debug APK for testing (easier to install)

### Issue: "App crashes on startup"

**Solution:**

```
1. Check phone has Android 7.0+ (API 24+)
2. Check phone has enough RAM (2GB+ recommended)
3. Check logcat for crash logs
```

---

## 🚀 Quick Build Script

Create a file `build_apk.bat`:

```batch
@echo off
echo Building Campus Network APK...
cd "C:\Users\ROHAN MATHAD\StudioProjects\Hack-ULA-Team-Pacific"
call gradlew clean assembleDebug
echo.
echo Build complete!
echo APK Location: app\build\outputs\apk\debug\app-debug.apk
pause
```

Double-click to build!

---

## 📊 Build Variants

Your app has these build variants:

| Variant | Description |
|---------|-------------|
| `debug` | For testing, includes debug info |
| `release` | Optimized for production |

Build specific variant:

```powershell
.\gradlew assembleDebug          # Debug APK
.\gradlew assembleRelease        # Release APK
```

---

## 🎉 After Building

### Debug APK (For Testing):

**Location:**

```
app/build/outputs/apk/debug/app-debug.apk
```

**Use for:**

- Testing on your phone
- Sharing with team for testing
- Internal demo

### Release APK (For Distribution):

**Location:**

```
app/build/outputs/apk/release/app-release.apk
```

**Use for:**

- Play Store upload (must be signed)
- Production release
- Public distribution

---

## 📱 Installing APK on Phone

### Enable Installation:

**Android 8+:**

```
Settings → Apps → Special Access → Install Unknown Apps
→ Enable for your file manager/browser
```

**Android 7 and below:**

```
Settings → Security → Unknown Sources → Enable
```

### Install:

```
1. Transfer APK to phone
2. Open APK file
3. Tap "Install"
4. Open and enjoy! 🎉
```

---

## 🔐 For Play Store Release

### Steps:

1. **Build Signed Release APK** (Method 3 above)
2. **Test thoroughly** on multiple devices
3. **Prepare store listing:**
    - App name
    - Description
    - Screenshots
    - Icon (512x512 PNG)
    - Feature graphic
4. **Upload to Play Console:**
    - https://play.google.com/console
    - Create app
    - Upload signed APK/AAB
    - Fill required info
    - Submit for review

---

## 💡 Pro Tips

### 1. Always test Debug APK first

```powershell
.\gradlew assembleDebug
```

### 2. For smaller APK, use App Bundle (AAB)

```powershell
.\gradlew bundleRelease
```

**Output:** `app/build/outputs/bundle/release/app-release.aab`

### 3. Check APK size

```powershell
.\gradlew assembleDebug
# Check: app/build/outputs/apk/debug/app-debug.apk
```

### 4. View build info

```powershell
.\gradlew assembleDebug --info
```

---

## 📞 Quick Help

**I just want to test on my phone:**
→ Use Method 1 (Android Studio GUI) → Build Debug APK

**I want to share with my team:**
→ Use Method 2 (Gradle) → `.\gradlew assembleDebug`

**I want to publish on Play Store:**
→ Use Method 3 → Generate Signed Release APK

---

## ✅ Summary

**Fastest Way to Get APK:**

```powershell
cd "C:\Users\ROHAN MATHAD\StudioProjects\Hack-ULA-Team-Pacific"
.\gradlew assembleDebug
```

**APK will be at:**

```
app\build\outputs\apk\debug\app-debug.apk
```

**Install on phone and test!** 🚀
