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
    onCalendarClick: () -> Unit = {},
    onPersonalInfoClick: () -> Unit = {},
    onClassroomClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    var showContent by remember { mutableStateOf(false) }
    var showName by remember { mutableStateOf(false) }
    var showRollNumber by remember { mutableStateOf(false) }
    var showAI by remember { mutableStateOf(false) }
    var showInput by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var showCampusScreen by remember { mutableStateOf(false) }
    var showCalendarScreen by remember { mutableStateOf(false) }
    var showClassroomScreen by remember { mutableStateOf(false) }

    // Get current email profile
    val emailProfile = remember { UserRepository.getCurrentEmailProfile(context) }

    // Check if SDK is available and get AI state
    val sdkAvailable = remember {
        try {
            Class.forName("com.runanywhere.sdk.public.RunAnywhere")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    // Initialize ViewModel only if SDK is available
    val viewModel = if (sdkAvailable) {
        remember {
            try {
                com.example.myapplication2.viewmodels.AINavigationViewModel(context)
            } catch (e: Exception) {
                null
            }
        }
    } else {
        null
    }

    val agentState = viewModel?.agentState?.collectAsState()?.value
        ?: com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Idle

    // Determine if buttons should be enabled
    val buttonsEnabled = when (agentState) {
        is com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Downloading,
        is com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.LoadingModel -> false

        else -> true
    }

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

    // Reset animations when returning from Campus or Calendar screens
    LaunchedEffect(showCampusScreen, showCalendarScreen, showClassroomScreen) {
        if (!showCampusScreen && !showCalendarScreen && !showClassroomScreen) {
            // Small delay then show buttons again
            showButtons = false
            delay(50)
            showButtons = true
        }
    }

    // Show screens
    if (showCampusScreen) {
        CampusScreen(onBack = { showCampusScreen = false })
    } else if (showCalendarScreen) {
        AcademicCalendarScreen(onBack = { showCalendarScreen = false })
    } else if (showClassroomScreen) {
        // Call the navigation callback instead of showing inline
        LaunchedEffect(Unit) {
            showClassroomScreen = false
            onClassroomClick()
        }
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
                    .background(Color.Black.copy(alpha = 0.8f))
            )

            // Animated gradient waves
            AnimatedGradientWaves()

            // Floating particles
            FloatingParticles()

            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(200))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Main scrollable content
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                        }

                        // Student Info Header
                        item {
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
                                                .background(
                                                    AppPurple.copy(alpha = 0.3f),
                                                    CircleShape
                                                )
                                                .clickable { onPersonalInfoClick() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = emailProfile?.name?.firstOrNull()?.toString()
                                                    ?: "S",
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
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // AI Assistant Section
                        item {
                            AnimatedVisibility(
                                visible = showAI && showInput,
                                enter = fadeIn(tween(200)) + expandVertically()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AIAssistantCircle()
                                    Spacer(modifier = Modifier.height(16.dp))
                                    AIInputField()
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        // Feature Grid (6 buttons) - Pass buttonsEnabled state
                        item {
                            AnimatedVisibility(
                                visible = showButtons,
                                enter = fadeIn(tween(200))
                            ) {
                                FeatureGrid(
                                    onAnnouncementsClick = onAnnouncementsClick,
                                    onCampusClick = { showCampusScreen = true },
                                    onCalendarClick = { showCalendarScreen = true },
                                    onPersonalInfoClick = onPersonalInfoClick,
                                    buttonsEnabled = buttonsEnabled,
                                    onClassroomClick = { showClassroomScreen = true }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    // Logout button in top-right corner
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(tween(300)) + scaleIn(
                            initialScale = 0.5f,
                            animationSpec = tween(300)
                        ),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        IconButton(
                            onClick = {
                                UserRepository.signOut(context)
                                onLogout()
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            AppPurple.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .border(
                                    width = 2.dp,
                                    color = AppPurple.copy(alpha = 0.6f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = AppPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
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

        // Main circle with Navi logo
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
                // Navi logo image
                Image(
                    painter = painterResource(id = R.mipmap.navilogo_foreground),
                    contentDescription = "Navi - AI Assistant",
                    modifier = Modifier
                        .size(90.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
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
fun AIInputField(responseState: MutableState<String>? = null) {
    val context = LocalContext.current

    // Check if SDK is available
    val sdkAvailable = remember {
        try {
            Class.forName("com.runanywhere.sdk.public.RunAnywhere")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    // Initialize ViewModel only if SDK is available
    val viewModel = if (sdkAvailable) {
        remember {
            try {
                com.example.myapplication2.viewmodels.AINavigationViewModel(context)
            } catch (e: Exception) {
                null
            }
        }
    } else {
        null
    }

    val agentState = viewModel?.agentState?.collectAsState()?.value
        ?: com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Error("SDK not synced")
    val response = viewModel?.response?.collectAsState()?.value ?: ""
    if (responseState != null) responseState.value = response
    val downloadProgress = viewModel?.downloadProgress?.collectAsState()?.value
    val loadingProgress = viewModel?.loadingProgress?.collectAsState()?.value

    var text by remember { mutableStateOf("") }
    var placeholderText by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
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
        // Show SDK Not Available Message
        if (!sdkAvailable) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AppPurple.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AppPurple.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = AppPurple,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "AI Agent Not Available",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppPurple,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please sync Gradle in Android Studio:\nClick 'Sync Now' banner at top",
                        fontSize = 12.sp,
                        color = AppLightGrey,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
            return@Column // Exit early if SDK not available
        }

        // Show AI Agent Status (only if SDK available)
        when (val state = agentState) {
            is com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.CheckingModel -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = AppPurple,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Checking model...",
                        color = AppPurple,
                        fontSize = 12.sp
                    )
                }
            }

            is com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.NeedDownload -> {
                // Show download button
                Button(
                    onClick = { viewModel?.downloadModel() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppPurple
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.Download, "Download", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download AI Model (119MB)")
                }
            }
            is com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.LoadingModel -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = AppPurple,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Loading AI Agent...",
                        color = AppPurple,
                        fontSize = 12.sp
                    )
                }
            }
            is com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Error -> {
                if (!state.message.contains("not available") && !state.message.contains("SDK not synced")) {
                    Text(
                        text = state.message,
                        color = AppRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            is com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Thinking -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = AppPurple,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI is thinking...",
                        color = AppPurple,
                        fontSize = 12.sp
                    )
                }
            }
            else -> {
                // Ready, Idle, or Downloading - show nothing here (progress bars show below)
            }
        }

        // Download progress
        downloadProgress?.let { progress ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = AppPurple,
                    trackColor = AppPurple.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Downloading: ${(progress * 100).toInt()}%",
                    color = AppPurple,
                    fontSize = 12.sp
                )
            }
        }

        // Loading progress
        loadingProgress?.let { progress ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = AppPurple,
                    trackColor = AppPurple.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Loading AI Model: $progress%",
                    color = AppPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                    .clickable(
                        enabled = sdkAvailable && agentState == com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Ready
                    ) {
                        if (sdkAvailable && agentState == com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Ready) {
                            showSuggestions = !showSuggestions
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Input text
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
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
                        cursorBrush = SolidColor(AppPurple),
                        enabled = sdkAvailable && agentState is com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Ready
                    )

                    // Mic icon
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Command",
                        tint = AppPurple.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Send icon (shows when text is entered and model is ready)
                    if (text.isNotEmpty() && sdkAvailable && agentState is com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Ready) {
                        IconButton(
                            onClick = {
                                viewModel?.askQuestion(text)
                                text = ""
                                showSuggestions = false
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
        if (sdkAvailable && showSuggestions && text.isEmpty() && agentState == com.example.myapplication2.viewmodels.AINavigationViewModel.AgentState.Ready) {
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    text = "How to check crowd?",
                    icon = Icons.Default.People,
                    onClick = {
                        viewModel?.askQuestion("How to check crowd?")
                        showSuggestions = false
                    }
                )
                SuggestionChip(
                    text = "How to order food?",
                    icon = Icons.Default.Restaurant,
                    onClick = {
                        viewModel?.askQuestion("How to order food?")
                        showSuggestions = false
                    }
                )
                SuggestionChip(
                    text = "Where are my books?",
                    icon = Icons.Default.MenuBook,
                    onClick = {
                        viewModel?.askQuestion("Where are my borrowed books?")
                        showSuggestions = false
                    }
                )
            }
        }

        // AI Response
        if (response.isNotEmpty()) {
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
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI Response",
                        tint = AppPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = response,
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
    onCampusClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onPersonalInfoClick: () -> Unit,
    buttonsEnabled: Boolean = true,
    onClassroomClick: () -> Unit = {}
) {
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
                isActive = true,
                isEnabled = buttonsEnabled,
                onClick = onClassroomClick,
                modifier = Modifier.weight(1f),
                delayMs = 0
            )

            FeatureButton(
                icon = Icons.Default.Notifications,
                label = "Announcements",
                isActive = true,
                isEnabled = buttonsEnabled,
                onClick = onAnnouncementsClick,
                modifier = Modifier.weight(1f),
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
                isEnabled = buttonsEnabled,
                onClick = {},
                modifier = Modifier.weight(1f),
                delayMs = 200
            )

            FeatureButton(
                icon = Icons.Default.AccountBalance,
                label = "Campus",
                isActive = true,
                isEnabled = buttonsEnabled,
                onClick = onCampusClick,
                modifier = Modifier.weight(1f),
                delayMs = 300
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FeatureButton(
                icon = Icons.Default.Event,
                label = "Calendar",
                isActive = true,
                isEnabled = buttonsEnabled,
                onClick = onCalendarClick,
                modifier = Modifier.weight(1f),
                delayMs = 400
            )

            FeatureButton(
                icon = Icons.Default.Person,
                label = "Personal Info",
                isActive = true,
                isEnabled = buttonsEnabled,
                onClick = onPersonalInfoClick,
                modifier = Modifier.weight(1f),
                delayMs = 500
            )
        }
    }
}

@Composable
fun FeatureButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    delayMs: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Determine if button should be clickable
    val canClick = isActive && isEnabled

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
        targetValue = if (isPressed && canClick) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    Box(
        modifier = modifier
            .height(140.dp)
            .offset(y = floatOffset.dp)
    ) {
        // Main button 
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .border(
                    width = 1.5.dp,
                    brush = if (canClick) {
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
                    color = if (canClick) {
                        Color.White.copy(alpha = 0.08f)
                    } else {
                        Color.White.copy(alpha = 0.03f)
                    },
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = canClick,
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
                // Icon without glow effect - single layer for better visibility
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (canClick) AppPurple else AppLightGrey.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Label with better visibility
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canClick) AppPurple else AppLightGrey.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
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
