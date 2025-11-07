package com.example.myapplication2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppColorScheme = darkColorScheme(
    primary = AppPurple,
    onPrimary = AppWhite,
    secondary = AppPurple,
    onSecondary = AppWhite,
    tertiary = AppPurple,
    onTertiary = AppWhite,
    background = AppBlack,
    onBackground = AppPurple,
    surface = AppBlack,
    onSurface = AppPurple,
    error = Color(0xFFCF6679),
    onError = AppWhite
)

@Composable
fun MyApplication2Theme(
    darkTheme: Boolean = true, // Always use dark theme
    dynamicColor: Boolean = false, // Disable dynamic color to use our custom theme
    content: @Composable () -> Unit
) {
    val colorScheme = AppColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppBlack.toArgb()
            window.navigationBarColor = AppBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}