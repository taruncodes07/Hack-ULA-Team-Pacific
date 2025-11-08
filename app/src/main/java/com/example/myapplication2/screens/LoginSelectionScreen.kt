package com.example.myapplication2.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication2.R
import com.example.myapplication2.ui.theme.AppBlack
import com.example.myapplication2.ui.theme.AppPurple
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun LoginSelectionScreen(
    onGuestClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }

    // Load Campus Network logo from assets
    val campusNetworkLogo = remember {
        val possibleNames = listOf(
            "campusnetwork.png",
            "campusnetwork.jpg",
            "campus_network.png",
            "campus_network.jpg",
            "campuslogo.png",
            "campuslogo.jpg"
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

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0A),
                        AppBlack,
                        Color(0xFF1A0A2E)
                    )
                )
            )
    ) {
        // Animated particles background
        AnimatedParticles()

        // Main content
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Top section with logo and title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Floating Campus Network Logo
                    FloatingLogo(campusNetworkLogo)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Welcome text with animation
                    AnimatedWelcomeText()
                }

                // Bottom section with buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(bottom = 60.dp)
                ) {
                    Text(
                        text = "Choose your access method",
                        color = AppPurple.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    AnimatedButton(
                        text = "Login",
                        onClick = onLoginClick,
                        isPrimary = true
                    )

                    AnimatedButton(
                        text = "Guest",
                        onClick = onGuestClick,
                        isPrimary = true
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingLogo(logoBitmap: android.graphics.Bitmap?) {
    val infiniteTransition = rememberInfiniteTransition(label = "float")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.offset(y = offsetY.dp)
    ) {
        if (logoBitmap != null) {
            Image(
                bitmap = logoBitmap.asImageBitmap(),
                contentDescription = "Campus Network Logo",
                modifier = Modifier
                    .size(180.dp)
                    .scale(1f),
                contentScale = ContentScale.Fit
            )
        } else {
            // Fallback text logo
            Text(
                text = "CAMPUS\nNETWORK",
                color = AppPurple,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )
        }
    }
}

@Composable
fun AnimatedWelcomeText() {
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400)
        showText = true
    }

    AnimatedVisibility(
        visible = showText,
        enter = fadeIn(animationSpec = tween(1000)) +
                slideInVertically(animationSpec = tween(1000)) { it / 4 }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to your",
                color = AppPurple.copy(alpha = 0.6f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Digital Campus Companion",
                color = AppPurple,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AnimatedParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_progress"
    )

    // Generate random particle positions
    val particles = remember {
        List(30) {
            LoginParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.5f + 0.3f,
                size = Random.nextFloat() * 3f + 1f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val currentY = (particle.y + animationProgress * particle.speed) % 1f
            val currentX = particle.x + sin(currentY * 10) * 0.05f

            val alpha =
                if (currentY < 0.1f) currentY / 0.1f else if (currentY > 0.9f) (1f - currentY) / 0.1f else 1f

            drawCircle(
                color = AppPurple.copy(alpha = alpha * 0.3f),
                radius = particle.size,
                center = Offset(
                    x = currentX * size.width,
                    y = currentY * size.height
                )
            )
        }
    }
}

private data class LoginParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float
)

@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Pulse animation for primary button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isHovered -> 1.05f
            isPrimary -> pulseScale
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .width(240.dp)
            .height(56.dp)
    ) {
        // Glow effect for primary button
        if (isPrimary) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = AppPurple.copy(alpha = glowAlpha * 0.3f),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isPrimary) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                AppPurple,
                                AppPurple.copy(alpha = 0.8f)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                AppPurple.copy(alpha = 0.3f),
                                AppPurple.copy(alpha = 0.2f)
                            )
                        )
                    },
                    shape = RoundedCornerShape(28.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = if (isPrimary) AppBlack else AppPurple,
                textAlign = TextAlign.Center,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
