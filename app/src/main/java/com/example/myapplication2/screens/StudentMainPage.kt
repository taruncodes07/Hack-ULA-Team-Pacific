package com.example.myapplication2.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import android.graphics.BitmapFactory
import com.example.myapplication2.R
import com.example.myapplication2.data.EmailUser
import com.example.myapplication2.data.UserRepository
import com.example.myapplication2.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

// Particle data class for animated particles
private data class StudentParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float
)

@Composable
fun StudentMainPage(
    onAnnouncementsClick: () -> Unit = {},
    onCampusClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    var showContent by remember { mutableStateOf(false) }
    var showName by remember { mutableStateOf(false) }
    var showRollNumber by remember { mutableStateOf(false) }
    var showAI by remember { mutableStateOf(false) }
    var showInput by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showCampusScreen by remember { mutableStateOf(false) }

    // Get current email profile
    val emailProfile = remember { UserRepository.getCurrentEmailProfile(context) }

    // Load campus background from assets (collegeimage.png/jpg)
    val campusBackground = remember {
        val possibleNames = listOf(
            "collegeimage.png",
            "collegeimage.jpg",
            "collegeimage.jpeg"
        )
        var bitmap: android.graphics.Bitmap? = null

        for (name in possibleNames) {
            try {
                val inputStream = context.assets.open(name)
                bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) break
            } catch (e: Exception) {
                // Try next filename
            }
        }
        bitmap
    }

    // Page load sequence
    LaunchedEffect(Unit) {
        delay(50)
        showContent = true
        delay(50)
        showName = true
        delay(50)
        showRollNumber = true
        delay(50)
        showAI = true
        delay(50)
        showInput = true
        delay(50)
        showButtons = true
    }

    // Show Campus Screen if requested
    if (showCampusScreen) {
        CampusScreen(onBack = { showCampusScreen = false })
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Blurred campus background from assets
            if (campusBackground != null) {
                Image(
                    bitmap = campusBackground.asImageBitmap(),
                    contentDescription = "Campus Background",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(25.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback to black background if image not found
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }

            // Black overlay (70% opacity)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
            )

            // Animated gradient waves
            AnimatedGradientWaves()

            // Floating particles
            FloatingParticles()

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Student Info Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Name with avatar - Clickable to open profile
                        AnimatedVisibility(
                            visible = showName,
                            enter = fadeIn(tween(200))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                            ) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .border(
                                            width = 2.dp,
                                            brush = Brush.sweepGradient(
                                                colors = listOf(
                                                    AppPurple,
                                                    AppPurpleSecondary,
                                                    AppPurple
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .background(AppPurple.copy(alpha = 0.3f), CircleShape)
                                        .clickable { showProfileDialog = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emailProfile?.name?.firstOrNull()?.toString() ?: "S",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppWhite
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Name
                                Text(
                                    text = emailProfile?.name ?: "Student",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppPurple,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Roll number + branch with slide-in
                        AnimatedVisibility(
                            visible = showRollNumber,
                            enter = fadeIn(tween(200)) + slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(200)
                            )
                        ) {
                            Text(
                                text = "${emailProfile?.rollNumber ?: "1RV23AI001"} | ${emailProfile?.department ?: "AI & ML Department"}",
                                fontSize = 14.sp,
                                color = AppLightGrey,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // AI Assistant Section
                    AnimatedVisibility(
                        visible = showAI,
                        enter = fadeIn(tween(200)) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(200)
                        )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AIAssistantCircle()

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // AI Input Field
                    AnimatedVisibility(
                        visible = showInput,
                        enter = fadeIn(tween(200)) + expandVertically()
                    ) {
                        AIInputField()
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Feature Grid (4 buttons)
                    AnimatedVisibility(
                        visible = showButtons,
                        enter = fadeIn(tween(200))
                    ) {
                        FeatureGrid(
                            onAnnouncementsClick = onAnnouncementsClick,
                            onCampusClick = { showCampusScreen = true }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Profile Dialog
            if (showProfileDialog) {
                StudentProfileDialog(
                    emailProfile = emailProfile,
                    onDismiss = { showProfileDialog = false },
                    onLogout = {
                        // Sign out and navigate back
                        UserRepository.signOut(context)
                        onLogout()
                    }
                )
            }
        }
    }
}

@Composable
fun AnimatedGradientWaves() {
    val infiniteTransition = rememberInfiniteTransition(label = "waves")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Purple wave
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    AppPurple.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(
                    width * 0.3f + sin(Math.toRadians(offset1.toDouble())).toFloat() * 100,
                    height * 0.4f
                ),
                radius = 400f
            ),
            center = Offset(
                width * 0.3f + sin(Math.toRadians(offset1.toDouble())).toFloat() * 100,
                height * 0.4f
            ),
            radius = 400f
        )

        // Blue-purple wave
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1E90FF).copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = Offset(
                    width * 0.7f + sin(Math.toRadians(offset2.toDouble())).toFloat() * 120,
                    height * 0.6f
                ),
                radius = 450f
            ),
            center = Offset(
                width * 0.7f + sin(Math.toRadians(offset2.toDouble())).toFloat() * 120,
                height * 0.6f
            ),
            radius = 450f
        )
    }
}

@Composable
fun FloatingParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_progress"
    )

    val particles = remember {
        List(20) {
            StudentParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.3f + 0.2f,
                size = Random.nextFloat() * 2f + 1f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val currentY = (particle.y + animationProgress * particle.speed) % 1f
            val currentX = particle.x + sin(currentY * 8) * 0.03f

            val alpha = if (currentY < 0.1f) currentY / 0.1f
            else if (currentY > 0.9f) (1f - currentY) / 0.1f
            else 1f

            drawCircle(
                color = AppPurple.copy(alpha = alpha * 0.25f),
                radius = particle.size,
                center = Offset(
                    x = currentX * size.width,
                    y = currentY * size.height
                )
            )
        }
    }
}

@Composable
fun AIAssistantCircle() {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_glow")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    var isPressed by remember { mutableStateOf(false) }
    var showListening by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            showListening = true
            delay(200)
            showListening = false
            isPressed = false
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            isPressed = true
        }
    ) {
        // Outer glow ring
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AppPurple.copy(alpha = glowAlpha * 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Main circle
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .border(
                    width = 3.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            AppPurple,
                            AppPurpleSecondary,
                            AppPurple
                        )
                    ),
                    shape = CircleShape
                )
                .background(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (showListening) {
                ListeningAnimation()
            } else {
                Text(
                    text = "AI",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppWhite
                )
            }
        }
    }
}

@Composable
fun ListeningAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "listening")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = LinearEasing, delayMillis = index * 100),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .background(AppPurple, CircleShape)
            )
        }
    }
}

@Composable
fun AIInputField() {
    var text by remember { mutableStateOf("") }
    var placeholderText by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    var aiResponse by remember { mutableStateOf("") }
    val fullPlaceholder = "Ask for app navigation..."

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "ai_input_float")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        for (i in fullPlaceholder.indices) {
            delay(20)
            placeholderText = fullPlaceholder.substring(0, i + 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Glowing shadow layer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AppPurple.copy(alpha = glowAlpha * 0.4f),
                                Color.Transparent
                            ),
                            radius = 500f
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
            )

            // Main input field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                AppPurple.copy(alpha = glowAlpha),
                                AppPurpleSecondary.copy(alpha = glowAlpha * 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .background(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .clickable { showSuggestions = !showSuggestions }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Input text
                    BasicTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            if (it.isNotEmpty()) {
                                aiResponse = processAIQuery(it)
                            } else {
                                aiResponse = ""
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = AppWhite,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (text.isEmpty()) {
                                    Text(
                                        text = placeholderText,
                                        color = AppLightGrey.copy(alpha = 0.6f),
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        },
                        singleLine = true,
                        cursorBrush = SolidColor(AppPurple)
                    )

                    // Mic icon
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Command",
                        tint = AppPurple,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send icon (shows when text is entered)
                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                aiResponse = processAIQuery(text)
                                showSuggestions = true
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = AppPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick suggestions
        if (showSuggestions && text.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    text = "How to check announcements?",
                    icon = Icons.Default.Notifications,
                    onClick = {
                        text = "How to check announcements?"
                        aiResponse = processAIQuery(text)
                    }
                )
                SuggestionChip(
                    text = "Where is campus hub?",
                    icon = Icons.Default.AccountBalance,
                    onClick = {
                        text = "Where is campus hub?"
                        aiResponse = processAIQuery(text)
                    }
                )
                SuggestionChip(
                    text = "How to view timetable?",
                    icon = Icons.Default.Schedule,
                    onClick = {
                        text = "How to view timetable?"
                        aiResponse = processAIQuery(text)
                    }
                )
            }
        }

        // AI Response
        if (aiResponse.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AppPurple.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AppPurple.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "AI Response",
                        tint = AppPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = aiResponse,
                        color = AppWhite,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppPurple.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppPurple,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = AppLightGrey,
                fontSize = 14.sp
            )
        }
    }
}

// AI Query Processing Function
fun processAIQuery(query: String): String {
    val lowercaseQuery = query.lowercase()

    return when {
        // App Navigation queries
        lowercaseQuery.contains("announcement") || lowercaseQuery.contains("notice") ->
            "🔔 To check announcements: Tap the 'Announcements' button in the feature grid below. View campus notices with color-coded categories!"

        lowercaseQuery.contains("campus") || lowercaseQuery.contains("hub") ->
            "🏛️ To access Campus Hub: Tap the 'Campus' button in the bottom right. Explore Order Food, Notices, Feedback, and Live Crowd features!"

        lowercaseQuery.contains("timetable") || lowercaseQuery.contains("schedule") ->
            "📅 To view timetable: Go to Guest Main Page (back button) → Tap 'Time Table' icon → View and zoom your class schedule!"

        lowercaseQuery.contains("calendar") || lowercaseQuery.contains("event") ->
            "📆 To check calendar: Go to Guest Main Page → Tap 'Academic Calendar' → Browse events by month with color codes!"

        // Profile & Settings
        lowercaseQuery.contains("profile") || lowercaseQuery.contains("account") ->
            "👤 To view profile: Tap your name at the top of the screen. See all your details and use the logout button!"

        lowercaseQuery.contains("logout") || lowercaseQuery.contains("sign out") ->
            "🚪 To logout: Tap your name → Scroll to bottom → Tap the red 'Logout' button. You'll return to the login screen."

        // Campus Features
        lowercaseQuery.contains("food") || lowercaseQuery.contains("canteen") || lowercaseQuery.contains(
            "order"
        ) ->
            "🍔 To order food: Tap 'Campus' button → Select 'Order Food'. This feature helps reduce canteen rush!"

        lowercaseQuery.contains("feedback") || lowercaseQuery.contains("complaint") ->
            "📝 To send feedback: Tap 'Campus' → Select 'Send Feedback' → Choose category → Write your feedback → Submit!"

        lowercaseQuery.contains("crowd") || lowercaseQuery.contains("busy") ->
            "🏋️ To check crowd: Tap 'Campus' → Select 'Live Crowd' → See real-time crowd status at canteen, library, gym, and labs!"

        // App Features
        lowercaseQuery.contains("ai") || lowercaseQuery.contains("assistant") ->
            "🤖 You're using the AI Assistant now! Ask me about app features, navigation, or how to use any functionality!"

        lowercaseQuery.contains("button") || lowercaseQuery.contains("click") || lowercaseQuery.contains(
            "tap"
        ) ->
            "👆 The app has 4 main buttons: Classroom (coming soon), Announcements, Social (coming soon), and Campus. Tap any active button to use it!"

        // Navigation help
        lowercaseQuery.contains("back") || lowercaseQuery.contains("return") ->
            "◀️ To go back: Use your device's back button or tap profile photo in Campus Hub to return to main screen."

        lowercaseQuery.contains("home") || lowercaseQuery.contains("main") ->
            "🏠 You're on the Student Main Page! From here you can access Announcements, Campus Hub, view your profile, and use the AI Assistant."

        // General help
        lowercaseQuery.contains("help") || lowercaseQuery.contains("how") ->
            "ℹ️ I can help you navigate the app! Try asking:\n• 'How to check announcements?'\n• 'Where is campus hub?'\n• 'How to view timetable?'\n• 'How to send feedback?'"

        // Feature availability
        lowercaseQuery.contains("classroom") || lowercaseQuery.contains("social") ->
            "⏳ The Classroom and Social features are coming soon! Currently available: Announcements and Campus Hub."

        // Default response
        else ->
            "🤖 I help you navigate the app! Ask me:\n• How to use features (announcements, campus, timetable)\n• Where to find buttons\n• How to view your profile\n• How to send feedback or check crowd\n• General app navigation"
    }
}

@Composable
fun FeatureGrid(
    onAnnouncementsClick: () -> Unit,
    onCampusClick: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val buttonWidth = maxWidth / 2.2f // Divide by 2.2 to account for spacing
        val buttonHeight = maxHeight / 2.2f

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureButton(
                    icon = Icons.Default.Computer,
                    label = "Classroom",
                    isActive = false,
                    onClick = {},
                    buttonWidth = buttonWidth,
                    buttonHeight = buttonHeight,
                    delayMs = 0
                )

                FeatureButton(
                    icon = Icons.Default.Notifications,
                    label = "Announcements",
                    isActive = true,
                    onClick = onAnnouncementsClick,
                    buttonWidth = buttonWidth,
                    buttonHeight = buttonHeight,
                    delayMs = 100
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureButton(
                    icon = Icons.Default.Chat,
                    label = "Social",
                    isActive = false,
                    onClick = {},
                    buttonWidth = buttonWidth,
                    buttonHeight = buttonHeight,
                    delayMs = 200
                )

                FeatureButton(
                    icon = Icons.Default.AccountBalance,
                    label = "Campus",
                    isActive = true,
                    onClick = onCampusClick,
                    buttonWidth = buttonWidth,
                    buttonHeight = buttonHeight,
                    delayMs = 300
                )
            }
        }
    }
}

@Composable
fun FeatureButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    buttonWidth: androidx.compose.ui.unit.Dp,
    buttonHeight: androidx.compose.ui.unit.Dp,
    delayMs: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        isVisible = true
    }

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "float_$label")

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + (delayMs), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    // Glowing animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed && isActive) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(200)) + scaleIn(initialScale = 0.8f, animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .width(buttonWidth)
                .height(buttonHeight)
                .offset(y = floatOffset.dp)
        ) {
            // Glowing shadow layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale * 1.05f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (isActive) AppPurple.copy(alpha = glowAlpha * 0.5f) else Color.Transparent,
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
            )

            // Main button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .border(
                        width = 1.5.dp,
                        brush = if (isActive) {
                            Brush.linearGradient(
                                colors = listOf(
                                    AppPurple.copy(alpha = glowAlpha),
                                    AppPurpleSecondary.copy(alpha = glowAlpha * 0.8f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    AppLightGrey.copy(alpha = 0.3f),
                                    AppLightGrey.copy(alpha = 0.3f)
                                )
                            )
                        },
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(
                        color = Color.White.copy(alpha = if (isActive) 0.08f else 0.03f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = isActive,
                        onClick = onClick
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Icon with glow effect
                    Box(contentAlignment = Alignment.Center) {
                        if (isActive) {
                            // Icon glow layer
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = AppPurple.copy(alpha = glowAlpha * 0.4f),
                                modifier = Modifier.size(52.dp)
                            )
                        }
                        // Main icon
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isActive) AppPurple else AppLightGrey.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Label
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActive) AppPurple else AppLightGrey.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )

                    // Coming soon badge
                    if (!isActive) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Coming soon",
                            fontSize = 11.sp,
                            color = AppLightGrey.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileDialog(
    emailProfile: com.example.myapplication2.data.EmailUser?,
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.8f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Prevent closing when clicking card */ },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(AppPurple, AppPurpleSecondary)
                                )
                            )
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Student Profile",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppWhite
                        )

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = AppWhite
                            )
                        }
                    }

                    // Scrollable content
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        emailProfile?.let { profile ->
                            // Personal Information
                            item {
                                ProfileSection(title = "Personal Information")
                            }

                            item { ProfileItem(label = "Name", value = profile.name) }
                            item { ProfileItem(label = "Email", value = profile.email) }
                            item { ProfileItem(label = "Roll Number", value = profile.rollNumber) }
                            item { ProfileItem(label = "Department", value = profile.department) }
                            item { ProfileItem(label = "Address", value = profile.address) }

                            // Academic Information
                            item {
                                ProfileSection(title = "Academic Information")
                            }

                            item { ProfileItem(label = "Branch", value = profile.branch) }
                            item { ProfileItem(label = "College", value = profile.college) }
                            item { ProfileItem(label = "Class", value = profile.classNumber) }
                            item {
                                ProfileItem(
                                    label = "Class Teacher",
                                    value = profile.classTeacherName
                                )
                            }
                            item {
                                ProfileItem(
                                    label = "Teacher Contact",
                                    value = profile.teacherContact
                                )
                            }

                            // Family Information
                            item {
                                ProfileSection(title = "Family Information")
                            }

                            item {
                                ProfileItem(
                                    label = "Father's Name",
                                    value = profile.fatherName
                                )
                            }
                            item {
                                ProfileItem(
                                    label = "Father's Contact",
                                    value = profile.fatherContact
                                )
                            }
                        }
                    }

                    // Logout Button at bottom
                    Button(
                        onClick = onLogout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppRed
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = AppWhite,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Logout",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = AppPurple,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = AppPurple.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = AppLightGrey.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            color = AppWhite,
            fontWeight = FontWeight.Normal
        )
    }
}
