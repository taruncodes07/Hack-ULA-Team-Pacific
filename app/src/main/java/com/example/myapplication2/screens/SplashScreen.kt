package com.example.myapplication2.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication2.R
import com.example.myapplication2.ui.theme.AppBlack
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToLoginSelection: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "splash_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        startAnimation = false
        delay(1000)
        onNavigateToLoginSelection()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBlack),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "Splash Logo",
            modifier = Modifier
                .size(200.dp)
                .alpha(alpha)
        )
    }
}
