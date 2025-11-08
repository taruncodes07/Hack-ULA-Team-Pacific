package com.example.myapplication2

import android.app.Application
import android.util.Log
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.addModelFromURL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        Log.d("AIAgent", "Starting MyApplication onCreate")

        // Initialize SDK in background
        applicationScope.launch {
            try {
                initializeSDK()
            } catch (e: Exception) {
                Log.e("AIAgent", "Failed to initialize AI Agent: ${e.message}", e)
            }
        }
    }

    private suspend fun initializeSDK() {
        try {
            Log.d("AIAgent", "Step 1: Initializing RunAnywhere SDK...")

            // Initialize SDK
            RunAnywhere.initialize(
                context = this@MyApplication,
                apiKey = "dev",
                environment = SDKEnvironment.DEVELOPMENT
            )

            Log.d("AIAgent", "Step 2: SDK initialized successfully")

            // Register LlamaCpp provider
            LlamaCppServiceProvider.register()
            Log.d("AIAgent", "Step 3: LlamaCpp provider registered")

            // Register the smaller, faster 360M model for quick responses
            addModelFromURL(
                url = "https://huggingface.co/HuggingFaceTB/SmolLM2-360M-Instruct-GGUF/resolve/main/smollm2-360m-instruct-q8_0.gguf",
                name = "SmolLM2-360M-Instruct",
                type = "LLM"
            )

            Log.d("AIAgent", "Step 4: Fast 360M model registered successfully (119MB)")
            Log.d("AIAgent", "=== SDK initialization complete ===")

        } catch (e: Exception) {
            Log.e("AIAgent", "SDK initialization failed: ${e.message}", e)
        }
    }
}
