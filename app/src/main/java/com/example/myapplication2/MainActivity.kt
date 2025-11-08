package com.example.myapplication2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.example.myapplication2.data.UserRepository
import com.example.myapplication2.navigation.NavGraph
import com.example.myapplication2.navigation.NavRoutes
import com.example.myapplication2.ui.theme.AppBlack
import com.example.myapplication2.ui.theme.MyApplication2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(android.graphics.Color.BLACK)
        enableEdgeToEdge()

        // Hide the status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            MyApplication2Theme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBlack)
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }
                    
                    // Check if user is already logged in
                    LaunchedEffect(Unit) {
                        val guestProfile = UserRepository.getCurrentProfile(this@MainActivity)
                        val emailProfile = UserRepository.getCurrentEmailProfile(this@MainActivity)
                        val isLoggedIn = UserRepository.isUserLoggedIn(this@MainActivity)

                        startDestination = when {
                            isLoggedIn && emailProfile != null -> {
                                // Email user is logged in, go to Student Main Page
                                NavRoutes.StudentMainPage.route
                            }

                            isLoggedIn && guestProfile != null -> {
                                // Guest user is logged in, go to Guest Main Page
                                NavRoutes.GuestMainPage.route
                            }

                            else -> {
                                // Not logged in, start from splash
                                NavRoutes.Splash.route
                            }
                        }
                    }
                    
                    // Only show NavGraph when start destination is determined
                    startDestination?.let { destination ->
                        NavGraph(
                            navController = navController,
                            startDestination = destination,
                            onExitApp = { finish() }
                        )
                    }
                }
            }
        }
    }
}