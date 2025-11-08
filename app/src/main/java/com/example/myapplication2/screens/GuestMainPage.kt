package com.example.myapplication2.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication2.R
import com.example.myapplication2.data.UserProfile
import com.example.myapplication2.data.UserRepository
import com.example.myapplication2.ui.theme.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// Data classes for announcements and calendar events
private data class GuestAnnouncement(
    val title: String,
    val category: AnnouncementCategory,
    val hoursAgo: Int = 0
)

private data class AnnouncementJson(
    val title: String,
    val category: String,
    val hoursAgo: Int
)

private enum class AnnouncementCategory(val color: Color, val label: String) {
    HACKATHON(CategoryPurple, "Hackathon Alert"),
    CLUB_EVENT(CategoryGreen, "Club Event"),
    ACADEMIC(CategoryRed, "Important Academic")
}

data class CalendarEvent(
    val date: LocalDate,
    val title: String,
    val category: EventCategory
)

data class CalendarEventJson(
    val daysOffset: Int,
    val title: String,
    val category: String
)

enum class EventCategory(val color: Color, val label: String) {
    FEST(CategoryGreen, "Festival/Club Event"),
    HOLIDAY(CategoryRed, "Holiday"),
    EXAM(CategoryBlue, "Exam Week"),
    ACADEMIC(CategoryRed, "Academic Notice")
}

// Helper function to format time ago
fun formatTimeAgo(hoursAgo: Int): String {
    return when {
        hoursAgo < 1 -> "Just now"
        hoursAgo < 24 -> "$hoursAgo hour${if (hoursAgo > 1) "s" else ""} ago"
        hoursAgo < 168 -> {
            val days = hoursAgo / 24
            "$days day${if (days > 1) "s" else ""} ago"
        }

        else -> {
            val weeks = hoursAgo / 168
            "$weeks week${if (weeks > 1) "s" else ""} ago"
        }
    }
}

// Helper function to load announcements from assets
private fun loadAnnouncementsFromAssets(context: android.content.Context): List<GuestAnnouncement> {
    return try {
        val json = context.assets.open("announcements.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val listType = object : TypeToken<List<AnnouncementJson>>() {}.type
        val announcementJsonList: List<AnnouncementJson> = gson.fromJson(json, listType)
        announcementJsonList.map { json ->
            GuestAnnouncement(
                title = json.title,
                category = AnnouncementCategory.valueOf(json.category),
                hoursAgo = json.hoursAgo
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback data
        listOf(
            GuestAnnouncement(
                "Hackathon XYZ registrations open!",
                AnnouncementCategory.HACKATHON,
                2
            ),
            GuestAnnouncement(
                "Dance Club Auditions this Friday!",
                AnnouncementCategory.CLUB_EVENT,
                5
            ),
            GuestAnnouncement(
                "Exam form submission ends tomorrow.",
                AnnouncementCategory.ACADEMIC,
                12
            )
        )
    }
}

// Helper function to load calendar events from assets
fun loadCalendarEventsFromAssets(context: android.content.Context): List<CalendarEvent> {
    return try {
        val json =
            context.assets.open("calendar_events.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val listType = object : TypeToken<List<CalendarEventJson>>() {}.type
        val eventJsonList: List<CalendarEventJson> = gson.fromJson(json, listType)
        eventJsonList.map { json ->
            val category = try {
                EventCategory.valueOf(json.category)
            } catch (e: Exception) {
                EventCategory.ACADEMIC
            }
            CalendarEvent(
                date = LocalDate.now().plusDays(json.daysOffset.toLong()),
                title = json.title,
                category = category
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback data
        listOf(
            CalendarEvent(LocalDate.now().plusDays(5), "Tech Fest 2024", EventCategory.FEST),
            CalendarEvent(LocalDate.now().plusDays(10), "Republic Day", EventCategory.HOLIDAY)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestMainPage(onLogout: () -> Unit = {}) {
    val context = LocalContext.current
    val userProfile = remember { UserRepository.getCurrentProfile(context) }

    var showProfileDialog by remember { mutableStateOf(false) }
    var showAnnouncementsDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showTimetableDialog by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    // Load campus background from assets
    val campusBackground = remember {
        val possibleNames = listOf(
            "collegeimage.png",
            "collegeimage.jpg",
            "collegeimage.jpeg",
            "campus_background.png",
            "campus_background.jpg"
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
        modifier = Modifier.fillMaxSize()
    ) {
        // Blurred campus background
        if (campusBackground != null) {
            Image(
                bitmap = campusBackground.asImageBitmap(),
                contentDescription = "Campus Background",
                modifier = Modifier
                    .fillMaxSize()
                    .blur(25.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback to drawable placeholder
            Image(
                painter = painterResource(id = R.drawable.campus_background),
                contentDescription = "Campus Background",
                modifier = Modifier
                    .fillMaxSize()
                    .blur(25.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Black overlay (70% opacity)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        )

        // Main content
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with user name
                ProfileHeader(
                    userProfile = userProfile,
                    onProfileClick = { showProfileDialog = true }
                )

                // Main content area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Welcome title section
                    Text(
                        text = "Welcome to ${userProfile?.college ?: "University of Los Angeles"}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AppPurple,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Explore your campus effortlessly.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppLightGrey,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Guest user banner
                    GuestBanner()
                }

                // Bottom Navigation Bar
                BottomNavBar(
                    onNavigationClick = { /* CN - Inactive */ },
                    onAnnouncementsClick = { showAnnouncementsDialog = true },
                    onCalendarClick = { showCalendarDialog = true },
                    onTimetableClick = { showTimetableDialog = true }
                )
            }
        }
    }

    // Dialogs
    if (showProfileDialog) {
        ProfileDialog(
            userProfile = userProfile,
            onDismiss = { showProfileDialog = false },
            onLogout = {
                UserRepository.signOut(context)
                onLogout()
            }
        )
    }

    if (showAnnouncementsDialog) {
        AnnouncementsDialog(
            onDismiss = { showAnnouncementsDialog = false }
        )
    }

    if (showCalendarDialog) {
        CalendarDialog(
            onDismiss = { showCalendarDialog = false }
        )
    }

    if (showTimetableDialog) {
        TimetableDialog(
            onDismiss = { showTimetableDialog = false }
        )
    }
}

@Composable
fun ProfileHeader(
    userProfile: UserProfile?,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AppPurple.copy(alpha = 0.3f))
                .border(2.dp, AppPurple, CircleShape)
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userProfile?.name?.firstOrNull()?.toString() ?: "G",
                color = AppPurple,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Hi, ${userProfile?.name?.split(" ")?.firstOrNull() ?: "Guest"}!",
            color = AppPurple,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GuestBanner() {
    Box(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppPurple.copy(alpha = 0.2f))
            .border(1.dp, AppPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "You're browsing as Guest. Log in to access full features.",
            color = AppLightGrey,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BottomNavBar(
    onNavigationClick: () -> Unit,
    onAnnouncementsClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onTimetableClick: () -> Unit
) {
    // Frosted glass effect
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButton(
                icon = R.drawable.ic_navigation,
                label = "Navigate",
                enabled = false,
                onClick = onNavigationClick
            )

            NavButton(
                icon = R.drawable.ic_announcements,
                label = "Announcements",
                enabled = true,
                onClick = onAnnouncementsClick
            )

            NavButton(
                icon = R.drawable.ic_calendar,
                label = "Calendar",
                enabled = true,
                onClick = onCalendarClick
            )

            NavButton(
                icon = R.drawable.ic_timetable,
                label = "Timetable",
                enabled = true,
                onClick = onTimetableClick
            )
        }
    }
}

@Composable
fun NavButton(
    icon: Int,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "nav_button_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = if (enabled) 8.dp else 0.dp,
                    shape = CircleShape,
                    spotColor = if (enabled) AppPurple else Color.Transparent
                )
                .clip(CircleShape)
                .background(
                    if (enabled) AppPurple.copy(alpha = 0.3f)
                    else Color.Gray.copy(alpha = 0.2f)
                )
                .border(
                    2.dp,
                    if (enabled) AppPurple else Color.Gray.copy(alpha = 0.5f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = if (enabled) AppPurple else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = if (enabled) AppPurple else AppLightGrey.copy(alpha = 0.5f),
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileDialog(
    userProfile: UserProfile?,
    onDismiss: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.95f))
                .border(2.dp, AppPurple, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header - Fixed at top
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile",
                        color = AppPurple,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppPurple
                        )
                    }
                }

                // Scrollable content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    userProfile?.let { profile ->
                        item { ProfileDetailItem("Name", profile.name) }
                        item { ProfileDetailItem("Phone", profile.phoneNumber) }
                        item { ProfileDetailItem("Email", profile.email) }
                        item { ProfileDetailItem("Branch", profile.branch) }
                        item { ProfileDetailItem("College", profile.college) }
                        item { ProfileDetailItem("Class", profile.classNumber) }
                        item { ProfileDetailItem("Class Teacher", profile.classTeacherName) }
                        item { ProfileDetailItem("Teacher Contact", profile.teacherContact) }
                        item { ProfileDetailItem("Father's Name", profile.fatherName) }
                        item { ProfileDetailItem("Father's Contact", profile.fatherContact) }
                        item { ProfileDetailItem("Address", profile.address) }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }

                // Sign Out Button at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppRed
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out",
                                tint = AppWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Sign Out",
                                color = AppWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = AppLightGrey,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = AppPurple,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Divider(
            color = AppPurple.copy(alpha = 0.3f),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun AnnouncementsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val announcements = remember { loadAnnouncementsFromAssets(context) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.95f))
                .border(2.dp, AppPurple, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Announcements",
                        color = AppPurple,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppPurple
                        )
                    }
                }

                // Announcements list
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(announcements) { announcement ->
                        AnnouncementCard(announcement)
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCard(announcement: GuestAnnouncement) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(announcement.category.color.copy(alpha = 0.15f))
                .border(
                    1.dp,
                    announcement.category.color.copy(alpha = 0.5f),
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category bullet
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(announcement.category.color)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = announcement.category.label,
                        color = announcement.category.color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatTimeAgo(announcement.hoursAgo),
                        color = AppLightGrey.copy(alpha = 0.7f),
                        fontSize = 9.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = announcement.title,
                    color = AppLightGrey,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CalendarDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val allEvents = remember { loadCalendarEventsFromAssets(context) }

    // Filter events for current month
    val currentMonthEvents = remember(currentMonth, allEvents) {
        allEvents.filter { event ->
            event.date.month == currentMonth.month && event.date.year == currentMonth.year
        }
    }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.95f))
                .border(2.dp, AppPurple, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Academic Calendar",
                        color = AppPurple,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppPurple
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem("Fest", CategoryGreen)
                    LegendItem("Holiday", CategoryRed)
                    LegendItem("Exam", CategoryBlue)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Month navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        currentMonth = currentMonth.minusMonths(1)
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = AppPurple,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = "${
                            currentMonth.month.getDisplayName(
                                TextStyle.FULL,
                                Locale.getDefault()
                            )
                        } ${currentMonth.year}",
                        color = AppPurple,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = {
                        currentMonth = currentMonth.plusMonths(1)
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = AppPurple,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Events list
                if (currentMonthEvents.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentMonthEvents) { event ->
                            CalendarEventCard(
                                event = event,
                                onClick = { selectedDate = event.date }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No events in this month",
                            color = AppLightGrey.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Event detail popup
    selectedDate?.let { date ->
        val event = allEvents.find { it.date == date }
        event?.let {
            EventDetailPopup(event = it, onDismiss = { selectedDate = null })
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            color = AppLightGrey,
            fontSize = 11.sp
        )
    }
}

@Composable
fun CalendarEventCard(
    event: CalendarEvent,
    onClick: () -> Unit
) {
    val dayOfWeek = event.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(event.category.color.copy(alpha = 0.15f))
            .border(1.dp, event.category.color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(event.category.color)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                color = AppLightGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$dayOfWeek, ${event.date.dayOfMonth} ${
                    event.date.month.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    )
                }",
                color = event.category.color,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun EventDetailPopup(event: CalendarEvent, onDismiss: () -> Unit) {
    val dayOfWeek = event.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.95f))
                .border(2.dp, event.category.color, RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = event.title,
                    color = AppPurple,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Date: $dayOfWeek, ${event.date.dayOfMonth} ${
                        event.date.month.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault()
                        )
                    } ${event.date.year}",
                    color = AppLightGrey,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Category: ${event.category.label}",
                    color = event.category.color,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppPurple
                    ),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = AppWhite)
                }
            }
        }
    }
}

@Composable
fun TimetableDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    // Try to load timetable from assets - try multiple possible filenames
    val timetableBitmap = remember {
        val possibleNames = listOf("tt.png", "tt.jpg", "tt.jpeg", "timetable.png", "timetable.jpg")
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

    // State for zoom and pan
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val state = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)

        // Accelerated panning - multiply by 2.5 for faster movement
        val panSpeedMultiplier = 2.5f
        val maxX = (scale - 1) * 1000f
        val maxY = (scale - 1) * 1000f

        offsetX = (offsetX + panChange.x * panSpeedMultiplier).coerceIn(-maxX, maxX)
        offsetY = (offsetY + panChange.y * panSpeedMultiplier).coerceIn(-maxY, maxY)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header - Compact floating overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Timetable",
                            color = AppPurple,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pinch to zoom • Drag to pan",
                            color = AppLightGrey.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Reset zoom button
                        IconButton(
                            onClick = {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Zoom",
                                tint = AppPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = AppPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Timetable image with zoom support - Full screen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (timetableBitmap != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offsetX,
                                    translationY = offsetY
                                )
                                .transformable(state = state),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = timetableBitmap.asImageBitmap(),
                                contentDescription = "Timetable",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    } else {
                        // Show message if no timetable found
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No Timetable",
                                tint = AppPurple.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No timetable found",
                                color = AppLightGrey,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add tt.png or tt.jpg to\nassets folder",
                                color = AppLightGrey.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
