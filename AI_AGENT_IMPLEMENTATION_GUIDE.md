# AI Navigation Agent Implementation Guide

## Overview

This guide explains how to implement an AI-powered navigation agent in the Campus Network app using
the RunAnywhere SDK. The agent helps users navigate the app by providing concise, step-by-step
instructions based on natural language queries.

---

## What Was Created

### 1. Features Documentation (`app/src/main/assets/app_features.txt`)

✅ **Complete features guide** containing:

- All app features and their locations
- Step-by-step navigation paths
- Common Q&A
- Button locations
- Access methods for each feature

This file serves as the **knowledge base** for the AI agent.

---

## Implementation Steps

### Step 1: Copy RunAnywhere SDK Files

The SDK AAR files are already available at:

```
app/AI Navigation Agent/app/libs/
├── RunAnywhereKotlinSDK-release.aar (4.0MB)
└── runanywhere-llm-llamacpp-release.aar (2.1MB)
```

**Action Required:**

```bash
# Copy SDK files to your main project
cp "app/AI Navigation Agent/app/libs/RunAnywhereKotlinSDK-release.aar" app/libs/
cp "app/AI Navigation Agent/app/libs/runanywhere-llm-llamacpp-release.aar" app/libs/
```

---

### Step 2: Update build.gradle.kts

Add dependencies to `app/build.gradle.kts`:

```kotlin
dependencies {
    // RunAnywhere SDK - Local AARs
    implementation(files("libs/RunAnywhereKotlinSDK-release.aar"))
    implementation(files("libs/runanywhere-llm-llamacpp-release.aar"))

    // Required SDK dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Ktor for networking
    implementation("io.ktor:ktor-client-core:3.0.3")
    implementation("io.ktor:ktor-client-okhttp:3.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
    implementation("io.ktor:ktor-client-logging:3.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Gson
    implementation("com.google.code.gson:gson:2.11.0")

    // Okio
    implementation("com.squareup.okio:okio:3.9.1")

    // AndroidX WorkManager
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // AndroidX Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // AndroidX Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
```

---

### Step 3: Update AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Required Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission 
        android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="28" />

    <application
        android:name=".MyApplication"
        android:largeHeap="true"
        ...>
    </application>
</manifest>
```

**Key Changes:**

- Set `android:name=".MyApplication"` (custom Application class)
- Add `android:largeHeap="true"` (required for AI models)
- Add `INTERNET` permission (for model downloads)

---

### Step 4: Create Application Class

Create `app/src/main/java/com/example/myapplication2/MyApplication.kt`:

```kotlin
package com.example.myapplication2

import android.app.Application
import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.public.extensions.addModelFromURL
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize SDK asynchronously
        GlobalScope.launch(Dispatchers.IO) {
            initializeAIAgent()
        }
    }

    private suspend fun initializeAIAgent() {
        try {
            Log.d("AIAgent", "Initializing RunAnywhere SDK...")

            // Step 1: Initialize SDK
            RunAnywhere.initialize(
                context = this@MyApplication,
                apiKey = "dev",
                environment = SDKEnvironment.DEVELOPMENT
            )

            // Step 2: Register LLM Service Provider
            LlamaCppServiceProvider.register()

            // Step 3: Register lightweight model for navigation
            registerNavigationModel()

            // Step 4: Scan for downloaded models
            RunAnywhere.scanForDownloadedModels()

            Log.d("AIAgent", "SDK initialized successfully")

        } catch (e: Exception) {
            Log.e("AIAgent", "SDK initialization failed: ${e.message}", e)
        }
    }

    private suspend fun registerNavigationModel() {
        // Use SmolLM2 360M - Fast and lightweight (119MB)
        // Perfect for quick navigation queries
        addModelFromURL(
            url = "https://huggingface.co/prithivMLmods/SmolLM2-360M-GGUF/resolve/main/SmolLM2-360M.Q8_0.gguf",
            name = "SmolLM2 360M Q8_0",
            type = "LLM"
        )
        Log.d("AIAgent", "Navigation model registered")
    }
}
```

---

### Step 5: Create AI Navigation Agent ViewModel

Create `app/src/main/java/com/example/myapplication2/viewmodels/AINavigationViewModel.kt`:

```kotlin
package com.example.myapplication2.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class AINavigationViewModel(private val context: Context) : ViewModel() {

    // State management
    sealed class AgentState {
        object Idle : AgentState()
        object LoadingModel : AgentState()
        object ModelReady : AgentState()
        object Thinking : AgentState()
        data class Responding(val partialResponse: String) : AgentState()
        data class Error(val message: String) : AgentState()
    }

    private val _agentState = MutableStateFlow<AgentState>(AgentState.Idle)
    val agentState: StateFlow<AgentState> = _agentState

    private val _response = MutableStateFlow("")
    val response: StateFlow<String> = _response

    private var isModelLoaded = false
    private val featuresGuide: String by lazy { loadFeaturesGuide() }

    init {
        checkAndLoadModel()
    }

    /**
     * Load features guide from assets
     */
    private fun loadFeaturesGuide(): String {
        return try {
            val inputStream = context.assets.open("app_features.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.use { it.readText() }
        } catch (e: Exception) {
            "Features guide not found"
        }
    }

    /**
     * Check if model is available and load it
     */
    private fun checkAndLoadModel() {
        viewModelScope.launch {
            try {
                _agentState.value = AgentState.LoadingModel

                // Check available models
                val models = listAvailableModels()
                val navigationModel = models.firstOrNull { it.name.contains("SmolLM2") }

                if (navigationModel == null) {
                    _agentState.value = AgentState.Error("Model not found. Please download it first.")
                    return@launch
                }

                // Load model if downloaded
                if (navigationModel.isDownloaded) {
                    val success = RunAnywhere.loadModel(navigationModel.id)
                    if (success) {
                        isModelLoaded = true
                        _agentState.value = AgentState.ModelReady
                    } else {
                        _agentState.value = AgentState.Error("Failed to load model")
                    }
                } else {
                    _agentState.value = AgentState.Error("Model not downloaded. Download size: 119MB")
                }

            } catch (e: Exception) {
                _agentState.value = AgentState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Download the navigation model
     */
    fun downloadModel() {
        viewModelScope.launch {
            try {
                val models = listAvailableModels()
                val navigationModel = models.firstOrNull { it.name.contains("SmolLM2") }
                
                if (navigationModel != null) {
                    RunAnywhere.downloadModel(navigationModel.id).collect { progress ->
                        // You can emit progress here if needed
                    }
                    checkAndLoadModel()
                }
            } catch (e: Exception) {
                _agentState.value = AgentState.Error("Download failed: ${e.message}")
            }
        }
    }

    /**
     * Ask the AI agent a navigation question
     */
    fun askQuestion(query: String) {
        if (!isModelLoaded) {
            _agentState.value = AgentState.Error("Model not loaded")
            return
        }

        viewModelScope.launch {
            try {
                _agentState.value = AgentState.Thinking
                _response.value = ""

                // Build prompt with features guide as context
                val prompt = buildNavigationPrompt(query)

                // Stream response
                var fullResponse = ""
                RunAnywhere.generateStream(prompt).collect { token ->
                    fullResponse += token
                    _response.value = fullResponse
                    _agentState.value = AgentState.Responding(fullResponse)
                }

                _agentState.value = AgentState.ModelReady

            } catch (e: Exception) {
                _agentState.value = AgentState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Build navigation prompt with context
     */
    private fun buildNavigationPrompt(userQuery: String): String {
        return """
You are a helpful navigation assistant for the Campus Network mobile app.

Your task: Answer the user's question about how to access app features with CONCISE, step-by-step instructions.

Rules:
1. Be extremely brief and direct
2. Use numbered steps (Step 1, Step 2, etc.)
3. Only include necessary steps, no extra information
4. Use exact button/feature names from the guide
5. Maximum 4-5 steps
6. End with "Done!" or similar confirmation

Features Guide:
$featuresGuide

User Question: $userQuery

Navigation Steps:
""".trimIndent()
    }

    /**
     * Clear response
     */
    fun clearResponse() {
        _response.value = ""
        _agentState.value = AgentState.ModelReady
    }
}
```

---

### Step 6: Update StudentMainPage

Replace the `processAIQuery()` function with actual AI agent integration:

```kotlin
// In StudentMainPage.kt

// Replace the old AIInputField with this enhanced version:

@Composable
fun AIInputField() {
    val context = LocalContext.current
    val viewModel = remember { AINavigationViewModel(context) }
    
    var text by remember { mutableStateOf("") }
    val agentState by viewModel.agentState.collectAsState()
    val response by viewModel.response.collectAsState()

    // ... existing UI code ...

    // When send button is clicked:
    IconButton(
        onClick = {
            if (text.isNotEmpty()) {
                viewModel.askQuestion(text)
            }
        }
    ) {
        Icon(Icons.Default.Send, "Send")
    }

    // Display AI response
    if (response.isNotEmpty()) {
        Card {
            Row {
                Icon(Icons.Default.Info, "AI Response", tint = AppPurple)
                Spacer(Modifier.width(12.dp))
                Text(response, color = AppWhite)
            }
        }
    }
}
```

---

## Usage Examples

### Example 1: Check Facility Crowd

**User Query:**

```
"How do I check the crowd at the canteen?"
```

**AI Response:**

```
Step 1: Tap "Campus" button on main page
Step 2: Tap "Live Crowd" card
Step 3: See Canteen status
Done!
```

### Example 2: Order Food

**User Query:**

```
"How to order food?"
```

**AI Response:**

```
Step 1: Tap "Campus" button
Step 2: Tap "Order Food" card
Step 3: Select items and add to cart
Step 4: Tap "Place Order"
Done!
```

### Example 3: View Borrowed Books

**User Query:**

```
"Where can I see my borrowed books?"
```

**AI Response:**

```
Step 1: Tap "Personal Info" button
Step 2: Expand "Library Details"
Step 3: Tap "Show More" for all 10 books
Done!
```

### Example 4: Send Feedback

**User Query:**

```
"How do I report a problem?"
```

**AI Response:**

```
Step 1: Tap "Campus" button
Step 2: Tap "Send Feedback"
Step 3: Tap "Submit New Feedback"
Step 4: Select category and write feedback
Step 5: Submit
Done!
```

---

## Model Information

### SmolLM2 360M Q8_0

**Specs:**

- **Size:** 119 MB
- **Speed:** Very Fast (⚡⚡⚡)
- **Quality:** Good for navigation
- **RAM Required:** 1GB
- **Load Time:** 2-5 seconds

**Why This Model:**

- Smallest footprint
- Fast responses
- Perfect for concise navigation instructions
- Works on most devices
- Quick download

### Alternative Models (Optional)

If you want better quality responses:

```kotlin
// Qwen 2.5 0.5B - Better quality (374 MB)
addModelFromURL(
    url = "https://huggingface.co/Triangle104/Qwen2.5-0.5B-Instruct-Q6_K-GGUF/resolve/main/qwen2.5-0.5b-instruct-q6_k.gguf",
    name = "Qwen 2.5 0.5B Instruct Q6_K",
    type = "LLM"
)
```

---

## Features

### ✅ What Works

1. **On-Device AI**: All processing happens locally, no internet needed after model download
2. **Fast Responses**: Streaming responses for immediate feedback
3. **Contextual**: Uses features guide as knowledge base
4. **Concise**: Provides only necessary steps
5. **Privacy**: No data sent to servers
6. **Offline**: Works without network connection

### 📋 Limitations

1. **First-Time Setup**: Requires 119MB model download
2. **Device RAM**: Needs at least 1GB available RAM
3. **Load Time**: 2-5 seconds to load model on startup
4. **Accuracy**: May occasionally provide imperfect instructions
5. **Model Size**: Trade-off between quality and size

---

## Testing Checklist

### Initial Setup

- [ ] Copy AAR files to `app/libs/`
- [ ] Add dependencies to `build.gradle.kts`
- [ ] Update `AndroidManifest.xml`
- [ ] Create `MyApplication.kt`
- [ ] Sync Gradle
- [ ] Build succeeds

### Runtime

- [ ] App starts without crashes
- [ ] SDK initializes (check logs)
- [ ] Model registration successful
- [ ] Model download UI appears
- [ ] Model downloads successfully
- [ ] Model loads without errors

### AI Agent

- [ ] Input field appears
- [ ] Can type queries
- [ ] Send button works
- [ ] AI response streams in real-time
- [ ] Responses are relevant
- [ ] Multiple queries work
- [ ] Clear/reset works

### Sample Queries to Test

- [ ] "How do I check crowd?"
- [ ] "How to order food?"
- [ ] "Where are my borrowed books?"
- [ ] "How do I send feedback?"
- [ ] "How to view calendar?"
- [ ] "How do I logout?"

---

## Troubleshooting

### Model Won't Download

```kotlin
// Check internet permission
<uses-permission android:name="android.permission.INTERNET" />

// Check available storage
val availableBytes = ...
// Need at least 150MB free
```

### Model Won't Load

```kotlin
// Ensure largeHeap is enabled
android:largeHeap="true"

// Check model is downloaded
val models = listAvailableModels()
val isDownloaded = models.first().isDownloaded
```

### App Crashes on Generation

```kotlin
// OutOfMemoryError - Use smaller model or increase heap
android:largeHeap="true"

// Check available RAM before generation
if (memoryInfo.availMem < 500 * 1024 * 1024) {
    // Less than 500MB available - warn user
}
```

### Slow Responses

```kotlin
// Normal for first generation (model warmup)
// Subsequent generations should be faster

// If consistently slow:
// 1. Check device specs
// 2. Close other apps
// 3. Consider even smaller model
```

---

## Next Steps

1. **Implement ViewModel Integration**: Connect the ViewModel to StudentMainPage
2. **Add Download UI**: Show progress bar for model download
3. **Enhance Prompts**: Refine system prompts for better responses
4. **Add Conversation History**: Maintain context across queries
5. **Implement Model Selection**: Let users choose different models
6. **Add Voice Input**: Integrate speech-to-text for queries
7. **Caching**: Cache common queries/responses
8. **Analytics**: Track query patterns to improve guide

---

## File Structure

```
app/
├── libs/
│   ├── RunAnywhereKotlinSDK-release.aar       ← SDK Core
│   └── runanywhere-llm-llamacpp-release.aar   ← LLM Engine
├── src/main/
│   ├── assets/
│   │   └── app_features.txt                    ← Features Guide
│   ├── java/com/example/myapplication2/
│   │   ├── MyApplication.kt                    ← SDK Init
│   │   ├── viewmodels/
│   │   │   └── AINavigationViewModel.kt        ← AI Agent Logic
│   │   └── screens/
│   │       └── StudentMainPage.kt              ← UI Integration
│   └── AndroidManifest.xml                     ← Config
└── build.gradle.kts                            ← Dependencies
```

---

## Benefits

### For Users

- ✅ **Instant Help**: Get navigation instructions without searching
- ✅ **Natural Language**: Ask in plain English
- ✅ **Fast**: Responses in seconds
- ✅ **Always Available**: Works offline
- ✅ **Private**: No data leaves device

### For Developers

- ✅ **Easy Updates**: Just update `app_features.txt`
- ✅ **No Backend**: No server costs
- ✅ **Scalable**: Add more features without code changes
- ✅ **Customizable**: Adjust prompts for your needs
- ✅ **Open Source SDK**: Full control

---

## Summary

You now have:

1. ✅ Complete features documentation
2. ✅ RunAnywhere SDK files ready
3. ✅ Implementation guide
4. ✅ Sample code
5. ✅ Testing checklist

**Total Implementation Time:** 1-2 hours

**Result:** A fully functional AI navigation agent that helps users navigate your app using natural
language queries, all running completely on-device! 🎉

---

**Made with ❤️ for Campus Network**
