package com.example.myapplication2.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.myapplication2.data.UserRepository
import com.example.myapplication2.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class CampusSection {
    NONE,
    CANTEEN,
    ANNOUNCEMENTS,
    FEEDBACK,
    CROWD_CHECK
}

// Data class for campus notices with timestamp
data class Quadruple(
    val title: String,
    val description: String,
    val colorTag: String,
    val uploadTimestamp: Long // Unix timestamp in milliseconds
)

// Helper function to calculate hours since upload
fun calculateCampusNoticeHoursAgo(uploadTimestamp: Long): Int {
    val currentTime = System.currentTimeMillis()
    val diffInMillis = currentTime - uploadTimestamp
    return (diffInMillis / (1000 * 60 * 60)).toInt()
}

// Helper function to format relative time for campus notices
fun formatCampusNoticeTime(uploadTimestamp: Long): String {
    val hoursAgo = calculateCampusNoticeHoursAgo(uploadTimestamp)
    return when {
        hoursAgo < 1 -> "Just now"
        hoursAgo < 24 -> "$hoursAgo ${if (hoursAgo == 1) "hour" else "hours"} ago"
        else -> {
            val days = hoursAgo / 24
            "$days ${if (days == 1) "day" else "days"} ago"
        }
    }
}

@Composable
fun CampusScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var currentSection by remember { mutableStateOf(CampusSection.NONE) }
    var showContent by remember { mutableStateOf(false) }

    // Get profile
    val emailProfile = remember { UserRepository.getCurrentEmailProfile(context) }
    val guestProfile = remember { UserRepository.getCurrentProfile(context) }

    val userName = emailProfile?.name ?: guestProfile?.name ?: "Student"

    // Handle back button - if section is open, close it; otherwise go back
    BackHandler {
        if (currentSection != CampusSection.NONE) {
            currentSection = CampusSection.NONE
        } else {
            onBack()
        }
    }

    // Entry animation
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Pure black background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        // Animated purple particles
        FloatingPurpleParticles()

        // Main content
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header with back button and title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppPurple,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Campus Hub",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppPurple
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // 2x2 Grid of campus features
                if (currentSection == CampusSection.NONE) {
                    CampusGridButtons(
                        onCanteenClick = { currentSection = CampusSection.CANTEEN },
                        onAnnouncementsClick = { currentSection = CampusSection.ANNOUNCEMENTS },
                        onFeedbackClick = { currentSection = CampusSection.FEEDBACK },
                        onCrowdCheckClick = { currentSection = CampusSection.CROWD_CHECK }
                    )
                }
            }
        }

        // Show sections as overlays (not dialogs)
        when (currentSection) {
            CampusSection.CANTEEN -> {
                CanteenOverlay(onDismiss = { currentSection = CampusSection.NONE })
            }

            CampusSection.ANNOUNCEMENTS -> {
                CampusAnnouncementsOverlay(onDismiss = { currentSection = CampusSection.NONE })
            }

            CampusSection.FEEDBACK -> {
                FeedbackOverlay(onDismiss = { currentSection = CampusSection.NONE })
            }

            CampusSection.CROWD_CHECK -> {
                CrowdCheckOverlay(onDismiss = { currentSection = CampusSection.NONE })
            }

            else -> {}
        }
    }
}

@Composable
fun CampusGridButtons(
    onCanteenClick: () -> Unit,
    onAnnouncementsClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onCrowdCheckClick: () -> Unit
) {
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(50)
        showButtons = true
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val buttonSize = minOf(maxWidth / 2.5f, maxHeight / 3f)

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CampusButton(
                    icon = Icons.Default.Restaurant,
                    label = "Order Food",
                    onClick = onCanteenClick,
                    buttonSize = buttonSize,
                    delay = 0,
                    visible = showButtons
                )

                CampusButton(
                    icon = Icons.Default.Notifications,
                    label = "Notices",
                    onClick = onAnnouncementsClick,
                    buttonSize = buttonSize,
                    delay = 50,
                    visible = showButtons
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                CampusButton(
                    icon = Icons.Default.Feedback,
                    label = "Feedback",
                    onClick = onFeedbackClick,
                    buttonSize = buttonSize,
                    delay = 100,
                    visible = showButtons
                )

                CampusButton(
                    icon = Icons.Default.Groups,
                    label = "Live Crowd",
                    onClick = onCrowdCheckClick,
                    buttonSize = buttonSize,
                    delay = 150,
                    visible = showButtons
                )
            }
        }
    }
}

@Composable
fun CampusButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    buttonSize: androidx.compose.ui.unit.Dp,
    delay: Int,
    visible: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "float_$label")

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + (delay * 2), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset"
    )

    // Glowing animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "campus_button_scale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250, delayMillis = delay)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(250, delayMillis = delay)
        )
    ) {
        Box(
            modifier = Modifier
                .size(buttonSize)
                .offset(y = floatOffset.dp)
        ) {
            // Glowing shadow layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale * 1.05f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AppPurple.copy(alpha = glowAlpha * 0.6f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            // Main button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .background(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AppPurple.copy(alpha = glowAlpha),
                                AppPurpleSecondary.copy(alpha = glowAlpha * 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Icon with glow
                    Box(contentAlignment = Alignment.Center) {
                        // Icon glow
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = AppPurple.copy(alpha = glowAlpha * 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        // Main icon
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = AppPurple,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppPurple,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CanteenOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = false
                ) { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = AppPurple.copy(alpha = 0.3f)
            )
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    AppPurple.copy(alpha = 0.8f),
                                    AppPurpleSecondary.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Order Food",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppWhite
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppWhite
                        )
                    }
                }

                // Content
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Canteen ordering feature coming soon...",
                        color = AppLightGrey,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CampusAnnouncementsOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = false
                ) { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = AppPurple.copy(alpha = 0.3f)
            )
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    AppPurple.copy(alpha = 0.8f),
                                    AppPurpleSecondary.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Campus Notices",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppWhite
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppWhite
                        )
                    }
                }

                // Content
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            listOf(
                                Quadruple(
                                    "Library Closed",
                                    "Library closed for cleaning today. Will reopen tomorrow at 9 AM.",
                                    "🔴",
                                    System.currentTimeMillis() - (3 * 60 * 60 * 1000)
                                ),
                                Quadruple(
                                    "Pool Maintenance",
                                    "Swimming pool under maintenance until Friday. Please avoid the area.",
                                    "🔵",
                                    System.currentTimeMillis() - (8 * 60 * 60 * 1000)
                                ),
                                Quadruple(
                                    "Photography Club Meet",
                                    "Photography club meeting today at 4 PM in Room 201. All welcome!",
                                    "🟢",
                                    System.currentTimeMillis() - (12 * 60 * 60 * 1000)
                                ),
                                Quadruple(
                                    "Exam Form Deadline",
                                    "Last date to submit exam forms is tomorrow. Visit office before 5 PM.",
                                    "🔴",
                                    System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                                ),
                                Quadruple(
                                    "Tech Fest Registration",
                                    "Register for TechFest 2024 before this weekend. Limited slots!",
                                    "🟣",
                                    System.currentTimeMillis() - (48 * 60 * 60 * 1000)
                                ),
                                Quadruple(
                                    "Canteen Timing Change",
                                    "Canteen will close at 7 PM this week due to staff shortage.",
                                    "🔵",
                                    System.currentTimeMillis() - (72 * 60 * 60 * 1000)
                                )
                            )
                        ) { index, (title, desc, color, uploadTimestamp) ->
                            CampusAnnouncementCard(title, desc, color, uploadTimestamp, index)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackOverlay(onDismiss: () -> Unit) {
    var selectedCategory by remember { mutableStateOf("Library") }
    var feedbackText by remember { mutableStateOf("") }
    var customSubject by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var feedbackMode by remember { mutableStateOf<FeedbackMode?>(null) } // null = show options, NEW = submit form, PREVIOUS = view history

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = false
                ) { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = AppPurple.copy(alpha = 0.3f)
            )
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    AppPurple.copy(alpha = 0.8f),
                                    AppPurpleSecondary.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (feedbackMode != null) {
                                feedbackMode = null // Go back to options
                            } else {
                                onDismiss()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppWhite
                        )
                    }

                    Text(
                        text = when (feedbackMode) {
                            FeedbackMode.NEW -> "Submit Feedback"
                            FeedbackMode.PREVIOUS -> "Previous Feedback"
                            null -> "Feedback"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppWhite,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppWhite
                        )
                    }
                }

                // Content
                Box(modifier = Modifier.weight(1f)) {
                    when (feedbackMode) {
                        null -> FeedbackOptionsScreen(
                            onNewFeedback = { feedbackMode = FeedbackMode.NEW },
                            onViewPrevious = { feedbackMode = FeedbackMode.PREVIOUS }
                        )

                        FeedbackMode.NEW -> NewFeedbackForm(
                            selectedCategory = selectedCategory,
                            onCategoryChange = { selectedCategory = it },
                            feedbackText = feedbackText,
                            onFeedbackChange = { feedbackText = it },
                            customSubject = customSubject,
                            onSubjectChange = { customSubject = it },
                            showSuccess = showSuccess,
                            onSubmit = {
                                showSuccess = true
                                feedbackText = ""
                                customSubject = ""
                            }
                        )

                        FeedbackMode.PREVIOUS -> PreviousFeedbackList()
                    }
                }
            }
        }
    }
}

enum class FeedbackMode {
    NEW,
    PREVIOUS
}

@Composable
fun FeedbackOptionsScreen(
    onNewFeedback: () -> Unit,
    onViewPrevious: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What would you like to do?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppPurple,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Submit New Feedback Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable(onClick = onNewFeedback),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppPurple.copy(alpha = 0.2f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                color = AppPurple
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "New Feedback",
                    tint = AppPurple,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Submit New Feedback",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // View Previous Feedback Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clickable(onClick = onViewPrevious),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.05f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                color = AppPurple.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "View Previous",
                    tint = AppPurple.copy(alpha = 0.8f),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "View Previous Feedback",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppWhite.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun NewFeedbackForm(
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    feedbackText: String,
    onFeedbackChange: (String) -> Unit,
    customSubject: String,
    onSubjectChange: (String) -> Unit,
    showSuccess: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category selector
        Text(
            "Category:",
            color = AppPurple,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Library", "Canteen").forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Hygiene", "Other").forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Show custom subject input only when "Other" is selected
        if (selectedCategory == "Other") {
            Column {
                Text(
                    "Subject:",
                    color = AppPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customSubject,
                    onValueChange = { onSubjectChange(it) },
                    placeholder = {
                        Text(
                            "Enter complaint subject...",
                            color = AppLightGrey.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AppWhite,
                        unfocusedTextColor = AppWhite,
                        focusedBorderColor = AppPurple,
                        unfocusedBorderColor = AppPurple.copy(alpha = 0.5f),
                        cursorColor = AppPurple
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // Feedback text
        Column {
            Text(
                "Feedback:",
                color = AppPurple,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = feedbackText,
                onValueChange = { onFeedbackChange(it) },
                placeholder = {
                    Text(
                        "Write your feedback...",
                        color = AppLightGrey.copy(alpha = 0.6f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = AppWhite,
                    unfocusedTextColor = AppWhite,
                    focusedBorderColor = AppPurple,
                    unfocusedBorderColor = AppPurple.copy(alpha = 0.5f),
                    cursorColor = AppPurple
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6
            )
        }

        // Submit button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Submit Feedback",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (showSuccess) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AppGreen.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Feedback submitted successfully!",
                        color = AppGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PreviousFeedbackList() {
    val previousFeedback = remember {
        listOf(
            Triple(
                "Library - Noisy Environment",
                "The library has been too noisy lately. Please enforce silence rules.",
                System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000)
            ),
            Triple(
                "Canteen - Food Quality",
                "The food quality needs improvement. More variety would be appreciated.",
                System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000)
            ),
            Triple(
                "Hygiene - Washroom Cleanliness",
                "Washrooms need more frequent cleaning.",
                System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (previousFeedback.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AppLightGrey.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No previous feedback found",
                        color = AppLightGrey,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            itemsIndexed(previousFeedback) { index, (title, description, timestamp) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = AppPurple.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = title,
                            color = AppPurple,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = description,
                            color = AppLightGrey,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = AppLightGrey.copy(alpha = 0.6f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatCampusNoticeTime(timestamp),
                                color = AppLightGrey.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrowdCheckOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = false
                ) { },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A).copy(alpha = 0.85f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = AppPurple.copy(alpha = 0.3f)
            )
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    AppPurple.copy(alpha = 0.8f),
                                    AppPurpleSecondary.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Live Crowd Status",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppWhite
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = AppWhite
                        )
                    }
                }

                // Content
                Box(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CrowdIndicator("Canteen", 75)
                        CrowdIndicator("Library", 30)
                        CrowdIndicator("Gym", 50)
                        CrowdIndicator("Lab", 20)
                    }
                }
            }
        }
    }
}

@Composable
fun CrowdIndicator(facilityName: String, crowdPercent: Int) {
    val color = when {
        crowdPercent < 30 -> Color(0xFF00FF7F)
        crowdPercent < 70 -> Color(0xFFFFD700)
        else -> Color(0xFFFF4C4C)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(facilityName, color = AppWhite)
            Text("$crowdPercent%", color = color)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = crowdPercent / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun CampusAnnouncementCard(
    title: String,
    description: String,
    colorTag: String,
    uploadTimestamp: Long,
    index: Int
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay((index * 50).toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { 20 }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.05f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = colorTag,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(title, color = AppPurple, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(description, color = AppLightGrey, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = AppLightGrey.copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            formatCampusNoticeTime(uploadTimestamp),
                            color = AppLightGrey.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}
