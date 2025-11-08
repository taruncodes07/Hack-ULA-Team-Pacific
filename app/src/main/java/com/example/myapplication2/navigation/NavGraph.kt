package com.example.myapplication2.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication2.screens.*
import com.example.myapplication2.screens.classroom.ClassroomScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = NavRoutes.Splash.route,
    onExitApp: () -> Unit = {}
) {
    // Wrap NavHost in a Box with black background to prevent white flashes
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(
                route = NavRoutes.Splash.route,
                exitTransition = {
                    fadeOut(animationSpec = tween(400))
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
                    fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(400))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(400))
                }
            ) {
                LoginSelectionScreen(
                    onGuestClick = {
                        navController.navigate(NavRoutes.GuestAuth.route)
                    },
                    onLoginClick = {
                        navController.navigate(NavRoutes.EmailAuth.route)
                    },
                    onAboutUsClick = {
                        navController.navigate(NavRoutes.AboutUs.route)
                    }
                )
            }

            // Email Authentication Route
            composable(
                route = NavRoutes.EmailAuth.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(400))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(400))
                }
            ) {
                EmailAuthScreen(
                    onAuthSuccess = {
                        navController.navigate(NavRoutes.StudentMainPage.route) {
                            popUpTo(NavRoutes.LoginSelection.route) {
                                inclusive = true
                            }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavRoutes.GuestAuth.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(400))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(400))
                }
            ) {
                GuestAuthScreen(
                    onAuthSuccess = {
                        navController.navigate(NavRoutes.GuestMainPage.route) {
                            popUpTo(NavRoutes.LoginSelection.route) {
                                inclusive = true
                            }
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
                    fadeIn(animationSpec = tween(400))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(400))
                }
            ) {
                // Handle back button press to exit app
                BackHandler {
                    onExitApp()
                }

                GuestMainPage(
                    onLogout = {
                        navController.navigate(NavRoutes.LoginSelection.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Student Main Page Route
            composable(
                route = NavRoutes.StudentMainPage.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(400))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(400))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(400))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(400))
                }
            ) {
                // Handle back button press to exit app
                BackHandler {
                    onExitApp()
                }

                // Student Main Page with AI Assistant
                StudentMainPage(
                    onAnnouncementsClick = {
                        navController.navigate(NavRoutes.Announcements.route)
                    },
                    onCampusClick = {
                        navController.navigate(NavRoutes.CampusSection.route)
                    },
                    onCalendarClick = {
                        navController.navigate(NavRoutes.AcademicCalendar.route)
                    },
                    onPersonalInfoClick = {
                        navController.navigate(NavRoutes.PersonalInfo.route)
                    },
                    onLogout = {
                        navController.navigate(NavRoutes.LoginSelection.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onClassroomClick = {
                        navController.navigate(NavRoutes.Classroom.route)
                    }
                )
            }

            // Announcements Screen Route
            composable(
                route = NavRoutes.Announcements.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                         animationSpec = tween(400)
                    ) + fadeIn(tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(tween(400))
                }
            ) {
                AnnouncementsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Campus Hub Screen Route
            composable(
                route = NavRoutes.CampusSection.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(tween(400))
                }
            ) {
                CampusScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Academic Calendar Screen Route
            composable(
                route = NavRoutes.AcademicCalendar.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(tween(400))
                }
            ) {
                AcademicCalendarScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Personal Info Screen Route
            composable(
                route = NavRoutes.PersonalInfo.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(tween(400))
                }
            ) {
                PersonalInfoScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // Classroom Screen Route
            composable(
                route = NavRoutes.Classroom.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(tween(400))
                }
            ) {
                ClassroomScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // About Us Route
            composable(
                route = NavRoutes.AboutUs.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeIn(tween(400))
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeOut(tween(400))
                }
            ) {
                AboutUsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
