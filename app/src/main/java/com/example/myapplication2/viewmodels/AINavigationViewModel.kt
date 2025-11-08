package com.example.myapplication2.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.public.extensions.listAvailableModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.File

class AINavigationViewModel(private val context: Context) : ViewModel() {

    // State management
    sealed class AgentState {
        object Idle : AgentState()
        object CheckingModel : AgentState()
        object NeedDownload : AgentState()
        object Downloading : AgentState()
        object LoadingModel : AgentState()
        object Ready : AgentState()
        object Thinking : AgentState()
        data class Error(val message: String) : AgentState()
    }

    private val _agentState = MutableStateFlow<AgentState>(AgentState.Idle)
    val agentState: StateFlow<AgentState> = _agentState

    private val _response = MutableStateFlow("")
    val response: StateFlow<String> = _response

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress

    private val _loadingProgress = MutableStateFlow<Int?>(null)
    val loadingProgress: StateFlow<Int?> = _loadingProgress

    private var modelId: String = ""
    private var isModelLoaded = false
    private var featuresGuide: String = ""

    init {
        // Load features guide from assets
        loadFeaturesGuide()

        // Delete any cached models on init to force fresh download
        deleteCachedModels()

        // Wait a bit for SDK to initialize, then check model
        viewModelScope.launch {
            delay(3000) // Give SDK time to initialize
            checkModel()
        }
    }

    /**
     * Delete cached models to force re-download every time
     */
    private fun deleteCachedModels() {
        try {
            // Try to delete models from common cache directories
            val cacheDir = context.cacheDir
            val filesDir = context.filesDir

            // Delete RunAnywhere model cache
            val modelCacheDirs = listOf(
                File(cacheDir, "models"),
                File(cacheDir, "runanywhere"),
                File(filesDir, "models"),
                File(filesDir, "runanywhere")
            )

            modelCacheDirs.forEach { dir ->
                if (dir.exists()) {
                    Log.d("AIAgent", "ViewModel: Deleting cache directory: ${dir.absolutePath}")
                    dir.deleteRecursively()
                    Log.d("AIAgent", "ViewModel: Cache deleted")
                }
            }
        } catch (e: Exception) {
            Log.e("AIAgent", "ViewModel: Error deleting cache: ${e.message}", e)
        }
    }

    /**
     * Load the app features guide from assets
     */
    private fun loadFeaturesGuide() {
        try {
            val inputStream = context.assets.open("app_features.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            featuresGuide = reader.readText()
            reader.close()
            Log.d("AIAgent", "ViewModel: Features guide loaded (${featuresGuide.length} chars)")
        } catch (e: Exception) {
            Log.e("AIAgent", "ViewModel: Failed to load features guide: ${e.message}", e)
            featuresGuide = "Features guide not available."
        }
    }

    /**
     * Check if model is registered - Always show download button (cache was deleted)
     */
    private fun checkModel() {
        viewModelScope.launch {
            try {
                _agentState.value = AgentState.CheckingModel
                Log.d("AIAgent", "ViewModel: Checking for registered models...")
                val models = listAvailableModels()
                Log.d("AIAgent", "ViewModel: Found ${models.size} registered models")

                // Find our SmolLM2 model
                val navModel = models.firstOrNull {
                    it.name.contains("SmolLM2", ignoreCase = true)
                }

                if (navModel == null) {
                    Log.e("AIAgent", "ViewModel: SmolLM2 model not registered")
                    _agentState.value =
                        AgentState.Error("Model not registered. Please restart app.")
                    return@launch
                }

                modelId = navModel.id
                Log.d(
                    "AIAgent",
                    "ViewModel: Model found - ID: ${navModel.id}, Name: ${navModel.name}"
                )

                // Always show download button (cache was deleted in init)
                _agentState.value = AgentState.NeedDownload
                Log.d("AIAgent", "ViewModel: Ready for fresh download (cache cleared)")

            } catch (e: Exception) {
                Log.e("AIAgent", "ViewModel: Error checking model: ${e.message}", e)
                _agentState.value = AgentState.Error("Error checking model: ${e.message}")
            }
        }
    }

    /**
     * Download the model
     */
    fun downloadModel() {
        viewModelScope.launch {
            try {
                if (modelId == "") {
                    Log.e("AIAgent", "ViewModel: Cannot download - model ID not set")
                    return@launch
                }

                _agentState.value = AgentState.Downloading
                _downloadProgress.value = 0f
                Log.d("AIAgent", "ViewModel: Starting download for model ID: $modelId")

                // Collect download progress
                RunAnywhere.downloadModel(modelId).collect { progress ->
                    _downloadProgress.value = progress
                    Log.d("AIAgent", "ViewModel: Download progress: ${(progress * 100).toInt()}%")
                }

                Log.d("AIAgent", "ViewModel: Download completed!")
                _downloadProgress.value = null

                // Wait a moment then load the model
                delay(1000)
                loadModel()

            } catch (e: Exception) {
                Log.e("AIAgent", "ViewModel: Download failed: ${e.message}", e)
                _downloadProgress.value = null
                _agentState.value = AgentState.Error("Download failed: ${e.message}")
            }
        }
    }

    /**
     * Load the model into memory
     */
    private fun loadModel() {
        viewModelScope.launch {
            try {
                if (modelId == "") {
                    Log.e("AIAgent", "ViewModel: Cannot load - model ID not set")
                    return@launch
                }

                _agentState.value = AgentState.LoadingModel
                _loadingProgress.value = 10
                Log.d("AIAgent", "ViewModel: Loading model ID: $modelId")

                // Start progress animation
                val progressJob = launch {
                    var progress = 10
                    while (progress < 90) {
                        delay(500)
                        progress += 2
                        _loadingProgress.value = progress
                    }
                }

                // Load model on IO thread (this can take 30-60 seconds)
                val success = withContext(Dispatchers.IO) {
                    Log.d("AIAgent", "ViewModel: Calling RunAnywhere.loadModel()...")
                    val result = RunAnywhere.loadModel(modelId)
                    Log.d("AIAgent", "ViewModel: loadModel() returned: $result")
                    result
                }

                progressJob.cancel()

                if (success) {
                    isModelLoaded = true
                    _loadingProgress.value = 100
                    Log.d("AIAgent", "ViewModel: Model loaded successfully!")
                    delay(500)
                    _loadingProgress.value = null
                    _agentState.value = AgentState.Ready
                } else {
                    Log.e("AIAgent", "ViewModel: loadModel returned false")
                    _loadingProgress.value = null
                    _agentState.value = AgentState.Error("Failed to load model")
                }

            } catch (e: Exception) {
                Log.e("AIAgent", "ViewModel: Load error: ${e.message}", e)
                _loadingProgress.value = null
                _agentState.value = AgentState.Error("Load error: ${e.message}")
            }
        }
    }

    /**
     * Ask a question to the AI - Now with smart keyword matching first
     */
    fun askQuestion(query: String) {
        viewModelScope.launch {
            try {
                _agentState.value = AgentState.Thinking
                _response.value = ""
                Log.d("AIAgent", "ViewModel: Asking question: $query")

                // Try keyword matching first (instant response)
                val keywordResponse = tryKeywordMatch(query)
                if (keywordResponse != null) {
                    Log.d("AIAgent", "ViewModel: Using keyword match (instant response)")
                    _response.value = keywordResponse
                    _agentState.value = AgentState.Ready
                    return@launch
                }

                // If keyword matching fails and model is not loaded, use fallback
                if (!isModelLoaded) {
                    Log.w("AIAgent", "ViewModel: Model not loaded, using fallback")
                    _response.value = getFallbackResponse(query)
                    _agentState.value = AgentState.Ready
                    return@launch
                }

                // Try AI model for complex queries
                Log.d("AIAgent", "ViewModel: Trying AI model for complex query")
                val prompt = buildSimplePrompt(query)
                Log.d("AIAgent", "ViewModel: Prompt built (${prompt.length} chars)")

                var fullResponse = ""
                var tokenCount = 0
                val startTime = System.currentTimeMillis()

                // Use 8 second timeout
                try {
                    withTimeout(8000) {
                        try {
                            Log.d("AIAgent", "ViewModel: Starting generateStream...")

                            RunAnywhere.generateStream(prompt).collect { token ->
                                tokenCount++
                                fullResponse += token
                                _response.value = fullResponse

                                if (tokenCount == 1) {
                                    Log.d("AIAgent", "ViewModel: First token received!")
                                }

                                // Stop at reasonable length (avoid long responses)
                                if (fullResponse.length > 300) {
                                    Log.d("AIAgent", "ViewModel: Stopping at 300 chars")
                                    return@collect
                                }
                            }

                            val duration = System.currentTimeMillis() - startTime
                            Log.d("AIAgent", "ViewModel: AI response complete in ${duration}ms")

                        } catch (streamError: Exception) {
                            Log.e("AIAgent", "ViewModel: Stream error: ${streamError.message}")
                            if (fullResponse.isEmpty()) {
                                fullResponse = getFallbackResponse(query)
                                _response.value = fullResponse
                            }
                        }
                    }
                } catch (timeoutError: Exception) {
                    Log.e("AIAgent", "ViewModel: Timeout after ${tokenCount} tokens")
                    if (fullResponse.isEmpty()) {
                        fullResponse = getFallbackResponse(query)
                        _response.value = fullResponse
                    }
                }

                if (fullResponse.isEmpty()) {
                    fullResponse = getFallbackResponse(query)
                    _response.value = fullResponse
                }

                _agentState.value = AgentState.Ready

            } catch (e: Exception) {
                Log.e("AIAgent", "ViewModel: Question error: ${e.message}", e)
                _response.value = getFallbackResponse(query)
                _agentState.value = AgentState.Ready
            }
        }
    }

    /**
     * Try to match query using keywords - returns null if no match
     */
    private fun tryKeywordMatch(query: String): String? {
        val lowerQuery = query.lowercase().trim()

        // Handle exact or very specific queries
        return when {
            // Greetings - redirect
            lowerQuery.matches(Regex("^(hi|hello|hey|hola|greetings?|sup|wassup|yo)$")) ||
                    lowerQuery.matches(Regex("^(how are you|what's up|whats up)\\??$")) -> {
                "I can only help with Campus Network app navigation. Please ask about app features like checking crowd, ordering food, viewing materials, etc."
            }

            // Class notes / materials (the specific query mentioned)
            lowerQuery.contains("class note") || lowerQuery.contains("my notes") ||
                    lowerQuery.contains("where") && (lowerQuery.contains("note") || lowerQuery.contains(
                "material"
            )) -> {
                "To access your class notes:\n\n1. Tap 'Classroom' button on main page\n2. Select 'Materials'\n3. Filter by Chemistry, Math, or Previous Papers\n4. Tap any PDF to view"
            }

            // Crowd checking
            (lowerQuery.contains("crowd") || lowerQuery.contains("busy") || lowerQuery.contains("crowded")) &&
                    !lowerQuery.contains("not") -> {
                when {
                    lowerQuery.contains("library") -> "To check library crowd:\n\n1. Tap 'Campus' button\n2. Tap 'Live Crowd'\n3. View Library status"
                    lowerQuery.contains("canteen") -> "To check canteen crowd:\n\n1. Tap 'Campus' button\n2. Tap 'Live Crowd'\n3. View Canteen status"
                    lowerQuery.contains("gym") -> "To check gym crowd:\n\n1. Tap 'Campus' button\n2. Tap 'Live Crowd'\n3. View Gym status"
                    lowerQuery.contains("lab") -> "To check lab crowd:\n\n1. Tap 'Campus' button\n2. Tap 'Live Crowd'\n3. View Lab status"
                    else -> "To check crowd status:\n\n1. Tap 'Campus' button\n2. Tap 'Live Crowd'\n3. View real-time status for Canteen, Library, Gym, and Labs"
                }
            }

            // Food/order
            lowerQuery.contains("food") || lowerQuery.contains("order") ||
                    lowerQuery.contains("canteen") && !lowerQuery.contains("crowd") ||
                    lowerQuery.contains("eat") || lowerQuery.contains("hungry") -> {
                "To order food:\n\n1. Tap 'Campus' button\n2. Tap 'Order Food'\n3. Browse menu and add items\n4. Place your order"
            }

            // Books/library
            (lowerQuery.contains("book") || lowerQuery.contains("borrowed")) &&
                    !lowerQuery.contains("note") && !lowerQuery.contains("material") -> {
                "To view borrowed books:\n\n1. Tap 'Personal Info' button\n2. Expand 'Library Details'\n3. Tap 'Show More' to see all books"
            }

            // Materials/PDFs/notes
            lowerQuery.contains("material") || lowerQuery.contains("pdf") ||
                    lowerQuery.contains("note") || lowerQuery.contains("study") && !lowerQuery.contains(
                "book"
            ) -> {
                "To access study materials:\n\n1. Tap 'Classroom' button\n2. Select 'Materials'\n3. Filter by Chemistry, Math, or Previous Papers\n4. Tap to view PDFs"
            }

            // Attendance
            lowerQuery.contains("attendance") || lowerQuery.contains("present") ||
                    lowerQuery.contains("absent") && !lowerQuery.contains("classroom") -> {
                "To check attendance:\n\n1. Tap 'Classroom' button\n2. Select 'Attendance'\n3. View calendar (green = present, red = absent)"
            }

            // Timetable
            lowerQuery.contains("timetable") || lowerQuery.contains("time table") ||
                    lowerQuery.contains("schedule") -> {
                "To view timetable:\n\n1. Tap 'Classroom' button\n2. Select 'Timetable'\n3. View weekly schedule"
            }

            // Communication/chat
            lowerQuery.contains("chat") || lowerQuery.contains("message") ||
                    lowerQuery.contains("talk") && lowerQuery.contains("teacher") -> {
                "To access class chats:\n\n1. Tap 'Classroom' button\n2. Select 'Communication'\n3. Choose a class group\n4. View chats with teachers and students"
            }

            // Feedback
            lowerQuery.contains("feedback") || lowerQuery.contains("complaint") ||
                    lowerQuery.contains("report") && !lowerQuery.contains("paper") -> {
                "To send feedback:\n\n1. Tap 'Campus' button\n2. Tap 'Send Feedback'\n3. Choose category\n4. Write and submit"
            }

            // Announcements
            lowerQuery.contains("announcement") || lowerQuery.contains("notice") -> {
                "To view announcements:\n\n1. Tap 'Announcements' button\n2. Browse all campus notices"
            }

            // Calendar
            lowerQuery.contains("calendar") || lowerQuery.contains("event") ||
                    lowerQuery.contains("exam") && !lowerQuery.contains("paper") -> {
                "To check calendar:\n\n1. Tap 'Calendar' button\n2. Browse events by month\n3. Tap dates for details"
            }

            // Payment
            lowerQuery.contains("payment") || lowerQuery.contains("fee") ||
                    lowerQuery.contains("dues") -> {
                "To check payments:\n\n1. Tap 'Personal Info' button\n2. Expand 'Payment Details'\n3. View pending and past payments"
            }

            // Personal info
            lowerQuery.contains("personal") || lowerQuery.contains("profile") ||
                    lowerQuery.contains("my info") -> {
                "To view personal info:\n\n1. Tap 'Personal Info' button\n2. View your details, library, and payment info"
            }

            // Classroom general
            lowerQuery.contains("classroom") -> {
                "To access Classroom:\n\n1. Tap 'Classroom' button\n2. Choose:\n   • Attendance\n   • Materials\n   • Communication\n   • Timetable"
            }

            else -> null // No keyword match, will try AI or fallback
        }
    }

    /**
     * Build a very simple, short prompt for AI
     */
    private fun buildSimplePrompt(userQuery: String): String {
        return """You help users navigate the Campus Network app. Answer in 3-4 short steps.

Available features:
- Classroom (attendance, materials, chats, timetable)
- Campus (food, notices, feedback, crowd check)
- Announcements, Calendar, Personal Info

Question: $userQuery

Steps:"""
    }

    /**
     * Get a fallback response based on simple keyword matching
     */
    private fun getFallbackResponse(query: String): String {
        // This is only called if keyword matching and AI both fail
        return """I can help you navigate the Campus Network app!

Popular features:
• Check crowd → Campus → Live Crowd
• Order food → Campus → Order Food  
• View materials → Classroom → Materials
• Check attendance → Classroom → Attendance
• Send feedback → Campus → Send Feedback
• View calendar → Calendar button
• Check books → Personal Info → Library Details

Ask me things like:
"How to check library crowd?"
"Where are my class notes?"
"How to order food?"
"""
    }

    /**
     * Clear response
     */
    fun clearResponse() {
        _response.value = ""
        if (isModelLoaded) {
            _agentState.value = AgentState.Ready
        }
    }
}
