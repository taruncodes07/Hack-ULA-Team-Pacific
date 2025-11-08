package com.example.myapplication2.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication2.R
import com.example.myapplication2.data.UserRepository
import com.example.myapplication2.ui.theme.*
import kotlinx.coroutines.delay

enum class EmailAuthState {
    EMAIL_INPUT,
    OTP_INPUT,
    SUCCESS
}

@Composable
fun EmailAuthScreen(
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var authState by remember { mutableStateOf(EmailAuthState.EMAIL_INPUT) }
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState == EmailAuthState.SUCCESS) {
            delay(2000)
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.9f),
                            Color.Black.copy(alpha = 0.95f),
                            Color(0xFF1A0A2E)
                        )
                    )
                )
        )

        when (authState) {
            EmailAuthState.SUCCESS -> {
                SuccessAnimationView()
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Title
                    Text(
                        text = if (authState == EmailAuthState.EMAIL_INPUT) "Student Login" else "Verify OTP",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppPurple,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (authState == EmailAuthState.EMAIL_INPUT)
                            "Enter your college email ID"
                        else
                            "Enter the OTP sent to\n$email",
                        fontSize = 14.sp,
                        color = AppLightGrey,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Email Input (Glassmorphic)
                    if (authState == EmailAuthState.EMAIL_INPUT) {
                        GlassmorphicTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                showError = false
                            },
                            label = "College Email",
                            placeholder = "yourname@college.edu",
                            keyboardType = KeyboardType.Email,
                            leadingIcon = Icons.Default.Email
                        )
                    }

                    // OTP Input
                    if (authState == EmailAuthState.OTP_INPUT) {
                        GlassmorphicTextField(
                            value = otp,
                            onValueChange = {
                                if (it.length <= 6) {
                                    otp = it
                                    showError = false
                                }
                            },
                            label = "OTP",
                            placeholder = "Enter 6-digit OTP",
                            keyboardType = KeyboardType.Number
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error message
                    if (showError) {
                        Text(
                            text = errorMessage,
                            color = AppRed,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Button
                    GlowingButton(
                        text = when (authState) {
                            EmailAuthState.EMAIL_INPUT -> "Send OTP"
                            EmailAuthState.OTP_INPUT -> "Verify"
                            else -> "Done"
                        },
                        onClick = {
                            when (authState) {
                                EmailAuthState.EMAIL_INPUT -> {
                                    if (isValidEmail(email)) {
                                        // Check if email exists in database
                                        val user =
                                            UserRepository.getEmailUserByEmail(context, email)
                                        if (user != null) {
                                            // Email exists, proceed to OTP
                                            authState = EmailAuthState.OTP_INPUT
                                            showError = false
                                        } else {
                                            // Email not registered
                                            showError = true
                                            errorMessage =
                                                "Email not registered. Please use a registered college email."
                                        }
                                    } else {
                                        showError = true
                                        errorMessage = "Please enter a valid college email"
                                    }
                                }

                                EmailAuthState.OTP_INPUT -> {
                                    if (otp.length == 6) {
                                        // Verify OTP against user's specific OTP
                                        if (UserRepository.verifyEmailOTP(context, email, otp)) {
                                            // OTP is correct
                                            val user =
                                                UserRepository.getEmailUserByEmail(context, email)
                                            if (user != null) {
                                                UserRepository.saveCurrentEmailProfile(
                                                    context,
                                                    user
                                                )
                                                authState = EmailAuthState.SUCCESS
                                            }
                                        } else {
                                            showError = true
                                            errorMessage =
                                                "Invalid OTP. Please check and try again."
                                        }
                                    } else {
                                        showError = true
                                        errorMessage = "Please enter 6-digit OTP"
                                    }
                                }

                                else -> {}
                            }
                        }
                    )

                    // Resend OTP button
                    if (authState == EmailAuthState.OTP_INPUT) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = {
                            otp = ""
                            showError = true
                            errorMessage = "OTP resent! Check your email."
                        }) {
                            Text(
                                "Resend OTP",
                                color = AppPurpleSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GlassmorphicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    val interactionSource =
        remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = AppPurpleSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = AppLightGrey.copy(alpha = 0.5f)
                )
            },
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isFocused) AppPurple else AppLightGrey
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    brush = if (isFocused) {
                        Brush.horizontalGradient(
                            colors = listOf(AppPurple, AppPurpleSecondary)
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                AppPurple.copy(alpha = 0.3f),
                                AppPurple.copy(alpha = 0.3f)
                            )
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AppWhite,
                unfocusedTextColor = AppWhite,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = AppPurple,
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.03f)
            ),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            interactionSource = interactionSource
        )
    }
}

@Composable
fun GlowingButton(
    text: String,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            AppPurple.copy(alpha = glowAlpha * 0.5f),
                            AppPurpleSecondary.copy(alpha = glowAlpha * 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        )

        // Button
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(AppPurple, AppPurpleSecondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppWhite
                )
            }
        }
    }
}

@Composable
fun SuccessAnimationView() {
    var scale by remember { mutableStateOf(0f) }
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scale = 1f
        delay(500)
        showText = true
    }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "success_scale"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Green checkmark with glow - Properly centered
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Glow effect behind
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AppGreen.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(140.dp)
                        .scale(animatedScale * 1.1f)
                )
                // Main icon on top
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = AppGreen,
                    modifier = Modifier
                        .size(120.dp)
                        .scale(animatedScale)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = showText,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 2 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Verified!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppGreen
                    )
                    Text(
                        text = "Welcome to Campus Network",
                        fontSize = 16.sp,
                        color = AppLightGrey,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return email.contains("@") &&
            email.contains(".") &&
            email.length > 5 &&
            email.substringAfter("@").contains(".")
}
