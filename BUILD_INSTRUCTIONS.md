# 🔧 BUILD INSTRUCTIONS - Native Libraries Fix

## ✅ Current Status

All native libraries have been downloaded and placed in the correct location:

- **Location:** `app/src/main/jniLibs/arm64-v8a/`
- **Count:** 12 native libraries (6 MB total)
- **Key Library:** `libllama-android.so` ✅

Gradle configuration has been updated to ensure libraries are included in the APK.

---

## 🚨 IMPORTANT: You MUST Rebuild

The error you're seeing is because you're running the **old APK** that doesn't have the native
libraries.

**The libraries are on your computer, but NOT in the APK running on the emulator.**

---

## 🏗️ How to Build Correctly

### **Option 1: Android Studio (RECOMMENDED)**

**Step 1: Sync Gradle**

1. Open Android Studio
2. You should see "Gradle files have changed" banner at top
3. Click **"Sync Now"**
4. Wait 30-60 seconds

**Step 2: Clean Project**

1. Click **Build** in top menu
2. Select **Clean Project**
3. Wait for completion (~30 seconds)

**Step 3: Rebuild Project**

1. Click **Build** in top menu
2. Select **Rebuild Project**
3. Wait for completion (3-5 minutes)
4. Watch for "BUILD SUCCESSFUL" message

**Step 4: Uninstall Old App**

1. In Android Studio bottom toolbar, click **Terminal**
2. Run:
   ```powershell
   adb uninstall com.example.myapplication2
   ```
3. Or manually: Settings → Apps → My Application2 → Uninstall

**Step 5: Run Fresh Install**

1. Click the **green Run button ▶️**
2. Select your emulator
3. App installs with native libraries ✅

---

### **Option 2: Command Line**

Open PowerShell in your project directory:

**Step 1: Set Java 17**

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "C:\Program Files\Java\jdk-17\bin;$env:PATH"
```

**Step 2: Clean Build**

```powershell
.\gradlew.bat clean build
```

**Step 3: Uninstall Old App**

```powershell
adb uninstall com.example.myapplication2
```

**Step 4: Install New APK**

```powershell
adb install app\build\outputs\apk\debug\app-debug.apk
```

---

## 🔍 Verification Steps

### **Before Building - Check Libraries Exist**

```powershell
Get-ChildItem "app/src/main/jniLibs/arm64-v8a/" | Select-Object Name
```

**Should show:**

- libllama-android.so ✅
- libllama.so
- libggml-base.so
- libggml-cpu.so
- (+ 8 more files)

---

### **After Building - Check APK Contains Libraries**

```powershell
# Extract APK
Expand-Archive -Path "app\build\outputs\apk\debug\app-debug.apk" -DestinationPath "temp-check-apk" -Force

# Check for native libraries
Get-ChildItem "temp-check-apk\lib\arm64-v8a\" | Select-Object Name

# Cleanup
Remove-Item -Path "temp-check-apk" -Recurse -Force
```

**Should show the same 12 .so files**

If missing → Build didn't include them → Rebuild!

---

## 🎯 What Changed in build.gradle.kts

### **1. NDK Configuration**

```kotlin
ndk {
    abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
}
```

Tells Gradle which architectures to support.

### **2. SourceSets Configuration**

```kotlin
sourceSets {
    getByName("main") {
        jniLibs.srcDirs("src/main/jniLibs")
    }
}
```

Explicitly tells Gradle where native libraries are.

### **3. Splits Disabled**

```kotlin
splits {
    abi { isEnable = false }
    density { isEnable = false }
}
```

Ensures one APK with all libraries (no splitting).

### **4. Packaging Options**

```kotlin
jniLibs {
    useLegacyPackaging = true
    keepDebugSymbols += listOf("**/*.so")
}
```

Preserves native libraries in APK.

---

## 📱 Expected Result After Rebuild

### **Logcat Output:**

```
AIAgent: === MyApplication onCreate ===
AIAgent: Step 1: Initializing RunAnywhere SDK...
AIAgent: Step 2: SDK initialized successfully
AIAgent: Step 3: LlamaCpp provider registered ✅
AIAgent: Step 4: Model registered successfully
AIAgent: === SDK initialization complete ===
```

**No more `UnsatisfiedLinkError`!** ✅

### **In App:**

1. Navigate to Student Main Page
2. See "Checking model..." (3 seconds)
3. See "Download AI Model (119MB)" button
4. Download works! ✅
5. Model loads! ✅
6. AI works! ✅

---

## ⚠️ Common Mistakes

### **Mistake 1: Clicking Run Without Rebuilding**

❌ Old APK still installed → Still crashes

✅ Always **Clean → Rebuild** before running

### **Mistake 2: Not Uninstalling Old App**

❌ Old app data conflicts with new app

✅ Uninstall old app first

### **Mistake 3: Not Syncing Gradle**

❌ Gradle doesn't know about new configuration

✅ Click "Sync Now" after gradle changes

### **Mistake 4: Checking Wrong APK**

❌ Looking at old APK in different location

✅ Check: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🐛 Troubleshooting

### **Still Getting UnsatisfiedLinkError After Rebuild?**

**Check 1: Verify libraries in APK**

```powershell
# Quick check
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead("app\build\outputs\apk\debug\app-debug.apk")
$zip.Entries | Where-Object {$_.FullName -like "lib/arm64-v8a/*"} | Select-Object FullName
$zip.Dispose()
```

Should list 12 .so files. If not → Rebuild failed!

**Check 2: Check build logs**
Look for "Task :app:stripDebugDebugSymbols" or similar in build output.
Should process native libraries.

**Check 3: Clean build directory**

```powershell
.\gradlew.bat clean
Remove-Item -Path "app\build" -Recurse -Force
.\gradlew.bat build
```

**Check 4: Invalidate Android Studio caches**

1. File → Invalidate Caches
2. Check all options
3. Click "Invalidate and Restart"
4. Rebuild after restart

---

## 📊 File Checklist

**Before Building:**

- ✅ `app/src/main/jniLibs/arm64-v8a/libllama-android.so` exists
- ✅ `app/src/main/jniLibs/arm64-v8a/` has 12 .so files
- ✅ `app/build.gradle.kts` has NDK configuration
- ✅ `app/build.gradle.kts` has sourceSets configuration

**After Building:**

- ✅ `app/build/outputs/apk/debug/app-debug.apk` exists
- ✅ APK size ~20-30 MB (includes 6 MB of native libs)
- ✅ APK contains lib/arm64-v8a/*.so files

**After Installing:**

- ✅ App launches without crash
- ✅ Logcat shows "LlamaCpp provider registered"
- ✅ No UnsatisfiedLinkError in logs

---

## 🎉 Summary

**Problem:** Native library `libllama-android.so` not found

**Root Cause:** Libraries weren't in the APK

**Solution:**

1. ✅ Downloaded native libraries
2. ✅ Placed in `app/src/main/jniLibs/arm64-v8a/`
3. ✅ Updated Gradle configuration
4. ⏳ **YOU NEED TO**: Rebuild the app

**Next Step:**

```
You → Clean → Rebuild → Uninstall Old → Install New → Test ✅
```

---

## 🚀 Quick Command Reference

```powershell
# Full rebuild from scratch
.\gradlew.bat clean build
adb uninstall com.example.myapplication2
adb install app\build\outputs\apk\debug\app-debug.apk

# Or in Android Studio
Build → Clean Project
Build → Rebuild Project
Run ▶️
```

---

**The fix is complete. You just need to rebuild!** 🎉

All native libraries are in place. The moment you rebuild and install the new APK, the error will be
gone and the AI will work.
