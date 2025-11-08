package com.example.myapplication2.screens

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

@Composable
fun CampusScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var currentSection by remember { mutableStateOf(CampusSection.NONE) }
    var showContent by remember { mutableStateOf(false) }

    // Get profile
    val emailProfile = remember { UserRepository.getCurrentEmailProfile(context) }
    val guestProfile = remember { UserRepository.getCurrentProfile(context) }

    val userName = emailProfile?.name ?: guestProfile?.name ?: "Student"

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
                    .padding(20.dp),
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
                            modifier = Modifier.size(60.dp)
                        )
                        // Main icon
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = AppPurple,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppWhite,
                        textAlign = TextAlign.Center
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
                                Triple(
                                    "Library Closed",
                                    "Library closed for cleaning today. Will reopen tomorrow at 9 AM.",
                                    "🔴"
                                ),
                                Triple(
                                    "Pool Maintenance",
                                    "Swimming pool under maintenance until Friday. Please avoid the area.",
                                    "🔵"
                                ),
                                Triple(
                                    "Photography Club Meet",
                                    "Photography club meeting today at 4 PM in Room 201. All welcome!",
                                    "🟢"
                                ),
                                Triple(
                                    "Exam Form Deadline",
                                    "Last date to submit exam forms is tomorrow. Visit office before 5 PM.",
                                    "🔴"
                                ),
                                Triple(
                                    "Tech Fest Registration",
                                    "Register for TechFest 2024 before this weekend. Limited slots!",
                                    "🟣"
                                ),
                                Triple(
                                    "Canteen Timing Change",
                                    "Canteen will close at 7 PM this week due to staff shortage.",
                                    "🔵"
                                )
                            )
                        ) { index, (title, desc, color) ->
                            AnnouncementCard(title, desc, color, index)
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
                        text = "Send Feedback",
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Category selector
                            Text(
                                "Category:",
                                color = AppPurple,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        item {
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
                                            onClick = { selectedCategory = category },
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
                                            onClick = { selectedCategory = category },
                                            label = { Text(category, fontSize = 13.sp) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // Show custom subject input only when "Other" is selected
                        if (selectedCategory == "Other") {
                            item {
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
                                        onValueChange = { customSubject = it },
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
                        }

                        item {
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
                                    onValueChange = { feedbackText = it },
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
                        }

                        item {
                            // Submit button
                            Button(
                                onClick = {
                                    if (feedbackText.isNotEmpty()) {
                                        showSuccess = true
                                        feedbackText = ""
                                        customSubject = ""
                                    }
                                },
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
                        }

                        if (showSuccess) {
                            item {
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
fun AnnouncementCard(title: String, description: String, colorTag: String, index: Int) {
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
                }
            }
        }
    }
}
