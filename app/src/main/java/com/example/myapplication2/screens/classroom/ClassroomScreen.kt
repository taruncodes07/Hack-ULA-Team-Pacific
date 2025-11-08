package com.example.myapplication2.screens.classroom

import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication2.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

// Data classes
data class AttendanceDay(
    val date: LocalDate,
    val isPresent: Boolean
)

data class StudyMaterial(
    val id: String,
    val name: String,
    val category: MaterialCategory,
    val size: String,
    val icon: ImageVector
)

enum class MaterialCategory {
    CHEMISTRY, MATH, PREVIOUS_PAPERS
}

data class ClassGroup(
    val id: String,
    val name: String,
    val teacher: String,
    val lastMessage: String,
    val unreadCount: Int
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: String,
    val isTeacher: Boolean
)

@Composable
fun ClassroomScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedFeature by remember { mutableStateOf<ClassroomFeature?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    // Load campus background
    val campusBackground = remember {
        val possibleNames = listOf("collegeimage.png", "collegeimage.jpg", "collegeimage.jpeg")
        var bitmap: android.graphics.Bitmap? = null
        for (name in possibleNames) {
            try {
                val inputStream = context.assets.open(name)
                bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) break
            } catch (e: Exception) {
                // Continue to next file
            }
        }
        bitmap
    }

    // Handle back button - if feature is selected, go back to main view; otherwise go back
    BackHandler {
        if (selectedFeature != null) {
            selectedFeature = null
        } else {
            onBack()
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
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
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black))
        }

        // Dark overlay
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)))

        // Content
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(300))
        ) {
            if (selectedFeature == null) {
                ClassroomMainView(
                    onFeatureSelected = { selectedFeature = it },
                    onBack = onBack
                )
            } else {
                when (selectedFeature) {
                    ClassroomFeature.ATTENDANCE -> AttendanceView(onBack = {
                        selectedFeature = null
                    })

                    ClassroomFeature.MATERIALS -> MaterialsView(onBack = { selectedFeature = null })
                    ClassroomFeature.COMMUNICATION -> CommunicationView(onBack = {
                        selectedFeature = null
                    })

                    ClassroomFeature.TIMETABLE -> TimetableView(onBack = { selectedFeature = null })
                    null -> {}
                }
            }
        }
    }
}

enum class ClassroomFeature(val title: String, val icon: ImageVector) {
    ATTENDANCE("Attendance", Icons.Default.CalendarToday),
    MATERIALS("Materials", Icons.Default.Folder),
    COMMUNICATION("Communication", Icons.Default.Chat),
    TIMETABLE("Timetable", Icons.Default.Schedule)
}

@Composable
fun ClassroomMainView(
    onFeatureSelected: (ClassroomFeature) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppPurple,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Classroom",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppPurple
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Feature Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(ClassroomFeature.values().toList()) { feature ->
                ClassroomFeatureCard(
                    feature = feature,
                    onClick = { onFeatureSelected(feature) }
                )
            }
        }
    }
}

@Composable
fun ClassroomFeatureCard(
    feature: ClassroomFeature,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.5.dp, AppPurple.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                tint = AppPurple,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = feature.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppPurple,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ATTENDANCE VIEW
@Composable
fun AttendanceView(onBack: () -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val attendanceData = remember { generateAttendanceData() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppPurple,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Attendance",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppPurple
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats
        AttendanceStats(attendanceData)

        Spacer(modifier = Modifier.height(24.dp))

        // Month Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppPurple
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    tint = AppPurple,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar
        AttendanceCalendar(currentMonth, attendanceData)
    }
}

@Composable
fun AttendanceStats(attendanceData: List<AttendanceDay>) {
    val presentDays = attendanceData.count { it.isPresent }
    val totalDays = attendanceData.size
    val percentage = if (totalDays > 0) (presentDays * 100) / totalDays else 0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard("Present", presentDays.toString(), CategoryGreen)
        StatCard("Absent", (totalDays - presentDays).toString(), CategoryRed)
        StatCard("Percentage", "$percentage%", AppPurple)
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = AppLightGrey
            )
        }
    }
}

@Composable
fun AttendanceCalendar(month: YearMonth, attendanceData: List<AttendanceDay>) {
    val daysInMonth = month.lengthOfMonth()
    val firstDayOfWeek = month.atDay(1).dayOfWeek.value % 7

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Day headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppPurple,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val weeks = (firstDayOfWeek + daysInMonth + 6) / 7
            items(weeks) { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (day in 0..6) {
                        val dayNumber = week * 7 + day - firstDayOfWeek + 1
                        if (dayNumber in 1..daysInMonth) {
                            val date = month.atDay(dayNumber)
                            val attendance = attendanceData.find { it.date == date }
                            AttendanceDayCell(dayNumber, attendance)
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.AttendanceDayCell(day: Int, attendance: AttendanceDay?) {
    val backgroundColor = when {
        attendance == null -> Color.Transparent
        attendance.isPresent -> CategoryGreen.copy(alpha = 0.3f)
        else -> CategoryRed.copy(alpha = 0.3f)
    }

    val borderColor = when {
        attendance == null -> AppLightGrey.copy(alpha = 0.3f)
        attendance.isPresent -> CategoryGreen
        else -> CategoryRed
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (attendance != null) AppWhite else AppLightGrey
        )
    }
}

// MATERIALS VIEW
@Composable
fun MaterialsView(onBack: () -> Unit) {
    val materials = remember { generateMaterials() }
    var selectedCategory by remember { mutableStateOf<MaterialCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppPurple,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Study Materials",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppPurple
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryChip(
                label = "All",
                isSelected = selectedCategory == null,
                onClick = { selectedCategory = null }
            )
            MaterialCategory.values().forEach { category ->
                CategoryChip(
                    label = category.name.replace("_", " "),
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Materials List
        val filteredMaterials = if (selectedCategory == null) {
            materials
        } else {
            materials.filter { it.category == selectedCategory }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredMaterials) { material ->
                MaterialCard(material)
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppPurple else Color.White.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, AppPurple.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) AppBlack else AppPurple,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun MaterialCard(material: StudyMaterial) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, AppPurple.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = material.icon,
                contentDescription = material.name,
                tint = AppPurple,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppWhite
                )
                Text(
                    text = "${material.category.name.replace("_", " ")} • ${material.size}",
                    fontSize = 12.sp,
                    color = AppLightGrey
                )
            }
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download",
                tint = AppPurple,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// COMMUNICATION VIEW
@Composable
fun CommunicationView(onBack: () -> Unit) {
    val classGroups = remember { generateClassGroups() }
    var selectedGroup by remember { mutableStateOf<ClassGroup?>(null) }

    // Handle back button
    BackHandler {
        if (selectedGroup != null) {
            selectedGroup = null
        } else {
            onBack()
        }
    }

    if (selectedGroup == null) {
        ClassGroupsList(
            groups = classGroups,
            onGroupSelected = { selectedGroup = it },
            onBack = onBack
        )
    } else {
        ChatView(
            group = selectedGroup!!,
            onBack = { selectedGroup = null }
        )
    }
}

@Composable
fun ClassGroupsList(
    groups: List<ClassGroup>,
    onGroupSelected: (ClassGroup) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppPurple,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Class Groups",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppPurple
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(groups) { group ->
                ClassGroupCard(
                    group = group,
                    onClick = { onGroupSelected(group) }
                )
            }
        }
    }
}

@Composable
fun ClassGroupCard(
    group: ClassGroup,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, AppPurple.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(AppPurple.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = group.name,
                    tint = AppPurple,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppWhite
                )
                Text(
                    text = "Teacher: ${group.teacher}",
                    fontSize = 12.sp,
                    color = AppLightGrey
                )
                Text(
                    text = group.lastMessage,
                    fontSize = 12.sp,
                    color = AppLightGrey.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
            if (group.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(AppPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = group.unreadCount.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppWhite
                    )
                }
            }
        }
    }
}

@Composable
fun ChatView(
    group: ClassGroup,
    onBack: () -> Unit
) {
    var messages by remember { mutableStateOf(generateChatMessages(group.id)) }
    var messageInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppPurple,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = group.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppPurple
                )
                Text(
                    text = "Teacher: ${group.teacher}",
                    fontSize = 12.sp,
                    color = AppLightGrey
                )
            }
        }

        // Chat Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatMessageBubble(message)
            }
        }

        // Input Area (Now Interactive)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = {
                    Text(
                        text = "Type a message...",
                        fontSize = 14.sp,
                        color = AppLightGrey.copy(alpha = 0.5f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = AppWhite,
                    unfocusedTextColor = AppWhite,
                    focusedBorderColor = AppPurple,
                    unfocusedBorderColor = AppPurple.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    cursorColor = AppPurple
                ),
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        // Add new message to the list
                        val newMessage = ChatMessage(
                            id = (messages.size + 1).toString(),
                            sender = "You",
                            message = messageInput,
                            timestamp = java.text.SimpleDateFormat(
                                "h:mm a",
                                java.util.Locale.getDefault()
                            ).format(java.util.Date()),
                            isTeacher = false
                        )
                        messages = messages + newMessage
                        messageInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (messageInput.isNotBlank()) AppPurple else AppPurple.copy(alpha = 0.3f),
                        CircleShape
                    ),
                enabled = messageInput.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (messageInput.isNotBlank()) AppWhite else AppWhite.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isTeacher) Arrangement.Start else Arrangement.End
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isTeacher) 4.dp else 16.dp,
                bottomEnd = if (message.isTeacher) 16.dp else 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isTeacher) {
                    AppPurple.copy(alpha = 0.3f)
                } else {
                    Color.White.copy(alpha = 0.1f)
                }
            ),
            border = BorderStroke(
                1.dp,
                if (message.isTeacher) AppPurple else AppLightGrey.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.sender,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (message.isTeacher) AppPurple else AppLightGrey
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.message,
                    fontSize = 14.sp,
                    color = AppWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.timestamp,
                    fontSize = 10.sp,
                    color = AppLightGrey.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

// TIMETABLE VIEW
@Composable
fun TimetableView(onBack: () -> Unit) {
    val context = LocalContext.current

    // Try to load timetable from assets
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppPurple,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Timetable",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppPurple
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (timetableBitmap != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, AppPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = timetableBitmap.asImageBitmap(),
                    contentDescription = "Timetable",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            // Dummy timetable grid
            DummyTimetableGrid()
        }
    }
}

@Composable
fun DummyTimetableGrid() {
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    val periods = listOf("9:00-10:00", "10:00-11:00", "11:00-12:00", "12:00-1:00", "2:00-3:00")
    val subjects = listOf("Math", "Chemistry", "Physics", "English", "CS", "Break")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.width(80.dp)) {
                    Text(
                        text = "Time",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppPurple,
                        textAlign = TextAlign.Center
                    )
                }
                days.forEach { day ->
                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = day.take(3),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppPurple,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        items(periods.size) { periodIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp)
                        .border(1.dp, AppPurple.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = periods[periodIndex],
                        fontSize = 10.sp,
                        color = AppLightGrey,
                        textAlign = TextAlign.Center
                    )
                }

                days.forEach { _ ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .border(1.dp, AppPurple.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .background(AppPurple.copy(alpha = 0.1f), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = subjects.random(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppWhite,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// DUMMY DATA GENERATORS
fun generateAttendanceData(): List<AttendanceDay> {
    val today = LocalDate.now()
    val attendance = mutableListOf<AttendanceDay>()

    // Generate 60 days of attendance (present/absent pattern)
    for (i in 0 until 60) {
        val date = today.minusDays(i.toLong())
        // 80% attendance rate
        val isPresent = (i % 5) != 0
        attendance.add(AttendanceDay(date, isPresent))
    }

    return attendance
}

fun generateMaterials(): List<StudyMaterial> {
    return listOf(
        // Chemistry
        StudyMaterial(
            "1",
            "Organic Chemistry - Chapter 1",
            MaterialCategory.CHEMISTRY,
            "2.4 MB",
            Icons.Default.Description
        ),
        StudyMaterial(
            "2",
            "Inorganic Chemistry Notes",
            MaterialCategory.CHEMISTRY,
            "1.8 MB",
            Icons.Default.Description
        ),
        StudyMaterial(
            "3",
            "Physical Chemistry Lab Manual",
            MaterialCategory.CHEMISTRY,
            "3.2 MB",
            Icons.Default.Description
        ),

        // Math
        StudyMaterial(
            "4",
            "Calculus - Differentiation",
            MaterialCategory.MATH,
            "1.5 MB",
            Icons.Default.Functions
        ),
        StudyMaterial(
            "5",
            "Linear Algebra Notes",
            MaterialCategory.MATH,
            "2.1 MB",
            Icons.Default.Functions
        ),
        StudyMaterial(
            "6",
            "Probability and Statistics",
            MaterialCategory.MATH,
            "2.8 MB",
            Icons.Default.Functions
        ),

        // Previous Papers
        StudyMaterial(
            "7",
            "Mid-Term 2023 Question Paper",
            MaterialCategory.PREVIOUS_PAPERS,
            "0.8 MB",
            Icons.Default.Quiz
        ),
        StudyMaterial(
            "8",
            "Final Exam 2023 Solutions",
            MaterialCategory.PREVIOUS_PAPERS,
            "1.2 MB",
            Icons.Default.Quiz
        ),
        StudyMaterial(
            "9",
            "Mock Test Series",
            MaterialCategory.PREVIOUS_PAPERS,
            "2.5 MB",
            Icons.Default.Quiz
        )
    )
}

fun generateClassGroups(): List<ClassGroup> {
    return listOf(
        ClassGroup(
            "1",
            "Chemistry Class - Section A",
            "Dr. Sarah Johnson",
            "Assignment due tomorrow",
            3
        ),
        ClassGroup(
            "2",
            "Mathematics Class - Section A",
            "Prof. Michael Chen",
            "Check the new formula sheet",
            0
        ),
        ClassGroup(
            "3",
            "Physics Class - Section B",
            "Dr. Robert Smith",
            "Lab session postponed",
            5
        ),
        ClassGroup(
            "4",
            "English Literature",
            "Ms. Emily Brown",
            "Read Chapter 5 for next class",
            1
        )
    )
}

fun generateChatMessages(groupId: String): List<ChatMessage> {
    return listOf(
        ChatMessage("1", "Dr. Sarah Johnson", "Good morning everyone!", "9:00 AM", true),
        ChatMessage("2", "Rohan", "Good morning ma'am", "9:02 AM", false),
        ChatMessage("3", "Priya", "Good morning!", "9:02 AM", false),
        ChatMessage(
            "4",
            "Dr. Sarah Johnson",
            "Please complete Assignment 3 by tomorrow. It covers organic reactions.",
            "9:05 AM",
            true
        ),
        ChatMessage("5", "Arjun", "Ma'am, which chapter is it from?", "9:10 AM", false),
        ChatMessage(
            "6",
            "Dr. Sarah Johnson",
            "Chapter 5 - Alkenes and Alkynes. Refer to the notes I shared last week.",
            "9:12 AM",
            true
        ),
        ChatMessage("7", "Sneha", "Thank you ma'am", "9:15 AM", false),
        ChatMessage(
            "8",
            "Dr. Sarah Johnson",
            "Also, lab session is scheduled for Friday at 2 PM. Don't be late!",
            "9:20 AM",
            true
        )
    )
}
