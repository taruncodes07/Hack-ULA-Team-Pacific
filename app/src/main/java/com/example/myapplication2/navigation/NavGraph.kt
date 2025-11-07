package com.example.myapplication2.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication2.screens.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route
    ) {
        composable(
            route = NavRoutes.Splash.route,
            exitTransition = {
                fadeOut(animationSpec = tween(500))
            }
        ) {
            SplashScreen(
                onNavigateToLoginSelection = {
                    navController.navigate(NavRoutes.LoginSelection.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavRoutes.LoginSelection.route,
            enterTransition = {
                fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500))
            }
        ) {
            LoginSelectionScreen(
                onGuestClick = {
                    navController.navigate(NavRoutes.GuestAuth.route)
                }
            )
        }

        composable(
            route = NavRoutes.GuestAuth.route,
            enterTransition = {
                fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500))
            }
        ) {
            GuestAuthScreen(
                onAuthSuccess = {
                    navController.navigate(NavRoutes.GuestMainPage.route) {
                        popUpTo(NavRoutes.LoginSelection.route) { inclusive = true }
                    }
                },
                onBackToSelection = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = NavRoutes.GuestMainPage.route,
            enterTransition = {
                fadeIn(animationSpec = tween(1000))
            }
        ) {
            GuestMainPage()
        }
    }
}
