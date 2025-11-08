package com.example.myapplication2.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication2.data.UserRepository
import com.example.myapplication2.ui.theme.AppBlack
import com.example.myapplication2.ui.theme.AppGreen
import com.example.myapplication2.ui.theme.AppPurple
import com.example.myapplication2.ui.theme.AppWhite
import kotlinx.coroutines.delay

enum class AuthState {
    PHONE_INPUT,
    OTP_INPUT,
    VERIFYING,
    SUCCESS
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GuestAuthScreen(
    onAuthSuccess: () -> Unit,
    onBackToSelection: () -> Unit
) {
    val context = LocalContext.current
    var authState by remember { mutableStateOf(AuthState.PHONE_INPUT) }
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState == AuthState.SUCCESS) {
            delay(1500)
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBlack)
    ) {
        when (authState) {
            AuthState.SUCCESS -> {
                // Green tick animation
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SuccessAnimation()
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(60.dp))

                    // Guest text at top
                    Text(
                        text = "Guest",
                        style = MaterialTheme.typography.headlineLarge,
                        color = AppPurple
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    // Phone number input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            if (it.length <= 10) {
                                phoneNumber = it
                                showError = false
                            }
                        },
                        label = {
                            Text(
                                "Phone Number",
                                color = AppPurple
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AppPurple,
                            unfocusedTextColor = AppPurple,
                            focusedBorderColor = AppPurple,
                            unfocusedBorderColor = AppPurple,
                            cursorColor = AppPurple
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = authState == AuthState.PHONE_INPUT
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // OTP input - fades in after phone verification
                    AnimatedVisibility(
                        visible = authState == AuthState.OTP_INPUT,
                        enter = fadeIn(animationSpec = tween(500)) + expandVertically(),
                        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = otp,
                                onValueChange = {
                                    if (it.length <= 6) {
                                        otp = it
                                        showError = false
                                    }
                                },
                                label = {
                                    Text(
                                        "Enter OTP",
                                        color = AppPurple
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = AppPurple,
                                    unfocusedTextColor = AppPurple,
                                    focusedBorderColor = AppPurple,
                                    unfocusedBorderColor = AppPurple,
                                    cursorColor = AppPurple
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Error message
                    if (showError) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Verify Phone / Resend OTP button
                    if (authState == AuthState.PHONE_INPUT) {
                        GreenButton(
                            text = "Verify Phone",
                            onClick = {
                                if (isValidPhoneNumber(phoneNumber)) {
                                    // Check if user exists in database
                                    val user =
                                        UserRepository.getUserByPhoneNumber(context, phoneNumber)
                                    if (user != null) {
                                        // User exists, proceed to OTP
                                        authState = AuthState.OTP_INPUT
                                        showError = false
                                    } else {
                                        // User doesn't exist
                                        showError = true
                                        errorMessage =
                                            "Phone number not registered. Please use a registered number."
                                    }
                                } else {
                                    showError = true
                                    errorMessage = "Please enter a valid 10-digit phone number"
                                }
                            }
                        )
                    }

                    // Verify OTP and Resend buttons (only shown when in OTP state)
                    if (authState == AuthState.OTP_INPUT) {
                        GreenButton(
                            text = "Verify OTP",
                            onClick = {
                                if (otp.length == 6) {
                                    if (otp == "123456") {
                                        val user = UserRepository.getUserByPhoneNumber(
                                            context,
                                            phoneNumber
                                        )
                                        if (user != null) {
                                            UserRepository.saveCurrentProfile(context, user)
                                            authState = AuthState.SUCCESS
                                        }
                                    } else {
                                        showError = true
                                        errorMessage = "Invalid OTP. Try 123456 for testing."
                                    }
                                } else {
                                    showError = true
                                    errorMessage = "Please enter the 6-digit OTP"
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        GreenButton(
                            text = "Resend OTP",
                            onClick = {
                                // Reset OTP and show feedback
                                otp = ""
                                showError = true
                                errorMessage = "OTP resent! Use 123456"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GreenButton(
    text: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "green_button_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = AppGreen,
                shape = RoundedCornerShape(12.dp)
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
            color = AppWhite,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SuccessAnimation() {
    var scale by remember { mutableStateOf(0f) }
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scale = 1f
        delay(600)  // Wait for checkmark animation
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = AppGreen,
            modifier = Modifier
                .size(120.dp)
                .scale(animatedScale)
        )

        AnimatedVisibility(
            visible = showText,
            enter = fadeIn(tween(600)) + slideInVertically { it / 4 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Verified!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppGreen,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "Welcome to Campus Network",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppPurple,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun isValidPhoneNumber(phone: String): Boolean {
    return phone.length == 10 && phone.all { it.isDigit() }
}
