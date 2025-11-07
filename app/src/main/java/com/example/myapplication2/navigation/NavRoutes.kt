package com.example.myapplication2.navigation

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object LoginSelection : NavRoutes("login_selection")
    object GuestAuth : NavRoutes("guest_auth")
    object GuestMainPage : NavRoutes("guest_main_page")
}
