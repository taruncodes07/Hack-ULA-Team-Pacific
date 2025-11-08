package com.example.myapplication2.navigation

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object LoginSelection : NavRoutes("login_selection")
    object AboutUs : NavRoutes("about_us")
    object GuestAuth : NavRoutes("guest_auth")
    object GuestMainPage : NavRoutes("guest_main_page")

    // New routes for email login flow
    object EmailAuth : NavRoutes("email_auth")
    object StudentMainPage : NavRoutes("student_main_page")
    object CampusSection : NavRoutes("campus_section")
    object Announcements : NavRoutes("announcements")
    object AcademicCalendar : NavRoutes("academic_calendar")
    object PersonalInfo : NavRoutes("personal_info")
    object Classroom : NavRoutes("classroom")
}
