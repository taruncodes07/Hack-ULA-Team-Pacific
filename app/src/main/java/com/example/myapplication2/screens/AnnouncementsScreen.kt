package com.example.myapplication2.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication2.R
import com.example.myapplication2.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// Data class matching Excel structure
data class Announcement(
    val heading: String,
    val dateUploaded: String,
    val sentBy: String,
    val category: CampusAnnouncementCategory,
    val imageUrl: String? = null,
    val description: String
)
enum class CampusAnnouncementCategory(val displayName: String, val color: Color) {
    CLUB("Club", Color(0xFF00FF7F)),
    HACKATHON("Hackathon", Color(0xFFA020F0)),
    ACADEMIC("Academic", Color(0xFFFF4C4C)),
    FEST("Fest/Event", Color(0xFF1E90FF)),
    GENERAL("General", Color(0xFFFFA500))
}

@Composable
fun AnnouncementsScreen(onBack: () -> Unit) {
    var showContent by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CampusAnnouncementCategory?>(null) }
    var selectedAnnouncement by remember { mutableStateOf<Announcement?>(null) }
    var showIconTransition by remember { mutableStateOf(false) }

    // Sample data (replace with Excel/Sheet data)
    val announcements = remember { getSampleAnnouncements() }

    // Filtered announcements
    val filteredAnnouncements = announcements.filter { announcement ->
        val matchesSearch = announcement.heading.contains(searchQuery, ignoreCase = true) ||
                announcement.sentBy.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || announcement.category == selectedCategory
        matchesSearch && matchesCategory
    }

    // Entry animation sequence
    LaunchedEffect(Unit) {
        showIconTransition = true
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

        // Dark gradient overlay (70% opacity to match other pages)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        )

        // Animated purple particles
        FloatingPurpleParticles()

        // Icon transition animation
        AnimatedVisibility(
            visible = showIconTransition,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(300))
        ) {
            // Main content
            if (showContent) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Top Bar
                    AnnouncementsTopBar(
                        onBack = onBack,
                        onFilterClick = { /* Show filter menu */ }
                    )

                    // Search and Filter Bar
                    SearchAndFilterBar(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )

                    // Announcements Feed
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        itemsIndexed(filteredAnnouncements) { index, announcement ->
                            AnnouncementCard(
                                announcement = announcement,
                                index = index,
                                onClick = { selectedAnnouncement = announcement }
                            )
                        }

                        // Footer
                        item {
                            Text(
                                text = "Powered by Campus AI",
                                color = AppLightGrey.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Detail Modal
        selectedAnnouncement?.let { announcement ->
            AnnouncementDetailModal(
                announcement = announcement,
                onDismiss = { selectedAnnouncement = null }
            )
        }
    }
}

@Composable
fun AnnouncementsTopBar(
    onBack: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back button
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = AppPurple,
                modifier = Modifier.size(28.dp)
            )
        }

        // Title
        Text(
            text = "Campus Announcements",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppPurple,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Filter button
        IconButton(onClick = onFilterClick) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = AppPurple,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SearchAndFilterBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: CampusAnnouncementCategory?,
    onCategorySelected: (CampusAnnouncementCategory?) -> Unit
) {
    var showCategoryMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = {
                Text(
                    "Search announcements...",
                    color = AppLightGrey.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AppPurple
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = AppPurple
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AppWhite,
                unfocusedTextColor = AppWhite,
                focusedBorderColor = AppPurple,
                unfocusedBorderColor = AppPurple.copy(alpha = 0.5f),
                cursorColor = AppPurple,
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.03f)
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category filter chips - Horizontally scrollable
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // All filter chip
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AppPurple,
                        selectedLabelColor = AppWhite
                    )
                )
            }

            // All category chips (including General)
            items(CampusAnnouncementCategory.values().size) { index ->
                val category = CampusAnnouncementCategory.values()[index]
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(if (selectedCategory == category) null else category) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(category.color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                category.displayName,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = category.color.copy(alpha = 0.3f),
                        selectedLabelColor = AppWhite,
                        containerColor = Color.White.copy(alpha = 0.05f)
                    )
                )
            }
        }
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    index: Int,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay((index * 50).toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (isHovered) -3f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_offset"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 4 }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .offset(y = offsetY.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isHovered = !isHovered
                    onClick()
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.05f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        announcement.category.color.copy(alpha = 0.6f),
                        Color.Transparent
                    )
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Category indicator bar
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(60.dp)
                        .background(
                            announcement.category.color,
                            RoundedCornerShape(2.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Category dot + Heading
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(announcement.category.color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = announcement.heading,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppPurple,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sent by
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AppLightGrey,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sent By: ${announcement.sentBy}",
                            fontSize = 13.sp,
                            color = AppLightGrey,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = AppLightGrey,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Uploaded: ${announcement.dateUploaded}",
                            fontSize = 12.sp,
                            color = AppLightGrey.copy(alpha = 0.8f)
                        )
                    }
                }

                // Arrow icon
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Details",
                    tint = AppPurple.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AnnouncementDetailModal(
    announcement: Announcement,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    announcement.category.color,
                                    announcement.category.color.copy(alpha = 0.7f)
                                )
                            )
                        )
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = announcement.category.displayName,
                        fontSize = 16.sp,
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
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Heading
                        Text(
                            text = announcement.heading,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppPurple,
                            lineHeight = 28.sp
                        )
                    }

                    item {
                        // Meta info
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text(
                                    "Posted By:",
                                    fontSize = 12.sp,
                                    color = AppLightGrey.copy(alpha = 0.7f)
                                )
                                Text(
                                    announcement.sentBy,
                                    fontSize = 14.sp,
                                    color = AppWhite,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Column {
                                Text(
                                    "Date:",
                                    fontSize = 12.sp,
                                    color = AppLightGrey.copy(alpha = 0.7f)
                                )
                                Text(
                                    announcement.dateUploaded,
                                    fontSize = 14.sp,
                                    color = AppWhite,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    item {
                        Divider(color = AppPurple.copy(alpha = 0.3f))
                    }

                    item {
                        // Description
                        Text(
                            text = announcement.description,
                            fontSize = 15.sp,
                            color = AppWhite,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingPurpleParticles() {
    val particles = remember {
        List(15) {
            Triple(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat() * 0.3f + 0.2f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_progress"
    )

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { (x, y, speed) ->
            val currentY = (y + progress * speed) % 1f
            val alpha = if (currentY < 0.1f) currentY / 0.1f
            else if (currentY > 0.9f) (1f - currentY) / 0.1f
            else 1f

            drawCircle(
                color = AppPurple.copy(alpha = alpha * 0.2f),
                radius = 40f,
                center = androidx.compose.ui.geometry.Offset(
                    x = x * size.width,
                    y = currentY * size.height
                )
            )
        }
    }
}

// Sample data function (replace with Excel/Sheet loader)
fun getSampleAnnouncements(): List<Announcement> {
    return listOf(
        Announcement(
            heading = "AI Innovate 2025 - Registration Open!",
            dateUploaded = "08 Nov 2025",
            sentBy = "Tech Club",
            category = CampusAnnouncementCategory.HACKATHON,
            description = "Join us for the biggest AI hackathon of the year! Register now to compete with teams from across the country. Prizes worth ₹5 lakhs to be won. Limited slots available."
        ),
        Announcement(
            heading = "Exam Form Submission Deadline",
            dateUploaded = "07 Nov 2025",
            sentBy = "Examination Department",
            category = CampusAnnouncementCategory.ACADEMIC,
            description = "All students are required to submit their exam forms before 15th November. Late submissions will not be accepted. Visit the examination office during office hours (9 AM - 5 PM)."
        ),
        Announcement(
            heading = "Cultural Club Auditions - Dance & Drama",
            dateUploaded = "06 Nov 2025",
            sentBy = "Cultural Club",
            category = CampusAnnouncementCategory.CLUB,
            description = "Auditions for the annual cultural fest are now open! Show your talent in dance, drama, or music. Auditions will be held on 12th November at the main auditorium from 10 AM onwards."
        ),
        Announcement(
            heading = "TechFest 2025 - Save the Date",
            dateUploaded = "05 Nov 2025",
            sentBy = "Event Committee",
            category = CampusAnnouncementCategory.FEST,
            description = "Mark your calendars! TechFest 2025 will be held from 20-22 December. Exciting competitions, workshops, and celebrity guests. Registration opens next week. Stay tuned for more details!"
        ),
        Announcement(
            heading = "Library Maintenance Schedule",
            dateUploaded = "04 Nov 2025",
            sentBy = "Library Department",
            category = CampusAnnouncementCategory.GENERAL,
            description = "The library will be closed for annual maintenance from 10-12 November. All borrowed books should be returned by 9th November. E-library services will remain available during this period."
        ),
        Announcement(
            heading = "Coding Bootcamp by Microsoft",
            dateUploaded = "03 Nov 2025",
            sentBy = "Placement Cell",
            category = CampusAnnouncementCategory.HACKATHON,
            description = "Microsoft is conducting a free 3-day coding bootcamp for students. Topics include Azure, Cloud Computing, and AI. Register through the placement portal. Limited seats - first come, first served!"
        )
    )
}