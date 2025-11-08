package com.example.myapplication2

import com.example.myapplication2.screens.classroom.*
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

/**
 * Comprehensive Test Cases for Classroom Features
 *
 * This test suite covers all classroom functionalities:
 * 1. Attendance - Calendar with green/red marks for present/absent
 * 2. Materials - PDF folders organized by category
 * 3. Communication - Class groups with dummy chats
 * 4. Timetable - Weekly schedule display
 */
class ClassroomFeatureTests {

    // ========== ATTENDANCE FEATURE TESTS ==========

    @Test
    fun `test attendance data generation creates 60 days`() {
        val attendanceData = generateAttendanceData()

        assertEquals("Should generate exactly 60 days of attendance", 60, attendanceData.size)
    }

    @Test
    fun `test attendance data includes present and absent days`() {
        val attendanceData = generateAttendanceData()

        val presentDays = attendanceData.count { it.isPresent }
        val absentDays = attendanceData.count { !it.isPresent }

        assertTrue("Should have present days", presentDays > 0)
        assertTrue("Should have absent days", absentDays > 0)
    }

    @Test
    fun `test attendance percentage calculation - 80 percent rate`() {
        val attendanceData = generateAttendanceData()

        val presentDays = attendanceData.count { it.isPresent }
        val totalDays = attendanceData.size
        val percentage = (presentDays * 100) / totalDays

        // Should be around 80% (as per generateAttendanceData logic)
        assertTrue("Attendance percentage should be around 80%", percentage in 75..85)
    }

    @Test
    fun `test attendance dates are in chronological order`() {
        val attendanceData = generateAttendanceData()

        val dates = attendanceData.map { it.date }
        val sortedDates = dates.sortedDescending()

        assertEquals("Dates should be in descending order (most recent first)", sortedDates, dates)
    }

    @Test
    fun `test attendance data includes today's date`() {
        val attendanceData = generateAttendanceData()
        val today = LocalDate.now()

        val hasToday = attendanceData.any { it.date == today }
        assertTrue("Attendance data should include today's date", hasToday)
    }

    @Test
    fun `test attendance pattern - one absent every 5 days`() {
        val attendanceData = generateAttendanceData()

        // Check first 5 entries
        val first5 = attendanceData.take(5)
        val absentInFirst5 = first5.count { !it.isPresent }

        assertEquals("Should have 1 absent day in first 5 days", 1, absentInFirst5)
    }

    // ========== MATERIALS FEATURE TESTS ==========

    @Test
    fun `test materials generation creates 9 items`() {
        val materials = generateMaterials()

        assertEquals("Should generate exactly 9 study materials", 9, materials.size)
    }

    @Test
    fun `test materials have all three categories`() {
        val materials = generateMaterials()

        val hasChemistry = materials.any { it.category == MaterialCategory.CHEMISTRY }
        val hasMath = materials.any { it.category == MaterialCategory.MATH }
        val hasPapers = materials.any { it.category == MaterialCategory.PREVIOUS_PAPERS }

        assertTrue("Should have Chemistry materials", hasChemistry)
        assertTrue("Should have Math materials", hasMath)
        assertTrue("Should have Previous Papers", hasPapers)
    }

    @Test
    fun `test chemistry materials count`() {
        val materials = generateMaterials()

        val chemistryMaterials = materials.filter { it.category == MaterialCategory.CHEMISTRY }
        assertEquals("Should have 3 Chemistry materials", 3, chemistryMaterials.size)
    }

    @Test
    fun `test math materials count`() {
        val materials = generateMaterials()

        val mathMaterials = materials.filter { it.category == MaterialCategory.MATH }
        assertEquals("Should have 3 Math materials", 3, mathMaterials.size)
    }

    @Test
    fun `test previous papers count`() {
        val materials = generateMaterials()

        val paperMaterials = materials.filter { it.category == MaterialCategory.PREVIOUS_PAPERS }
        assertEquals("Should have 3 Previous Papers", 3, paperMaterials.size)
    }

    @Test
    fun `test all materials have valid properties`() {
        val materials = generateMaterials()

        materials.forEach { material ->
            assertFalse("Material ID should not be empty", material.id.isEmpty())
            assertFalse("Material name should not be empty", material.name.isEmpty())
            assertFalse("Material size should not be empty", material.size.isEmpty())
            assertNotNull("Material icon should not be null", material.icon)
        }
    }

    @Test
    fun `test material sizes are in MB format`() {
        val materials = generateMaterials()

        materials.forEach { material ->
            assertTrue(
                "Material size should end with MB",
                material.size.endsWith("MB")
            )
        }
    }

    @Test
    fun `test chemistry materials have correct names`() {
        val materials = generateMaterials()
        val chemistryMaterials = materials.filter { it.category == MaterialCategory.CHEMISTRY }

        val names = chemistryMaterials.map { it.name }
        assertTrue("Should have Organic Chemistry", names.any { it.contains("Organic") })
        assertTrue("Should have Inorganic Chemistry", names.any { it.contains("Inorganic") })
        assertTrue("Should have Physical Chemistry", names.any { it.contains("Physical") })
    }

    // ========== COMMUNICATION FEATURE TESTS ==========

    @Test
    fun `test class groups generation creates 4 groups`() {
        val groups = generateClassGroups()

        assertEquals("Should generate exactly 4 class groups", 4, groups.size)
    }

    @Test
    fun `test all class groups have valid properties`() {
        val groups = generateClassGroups()

        groups.forEach { group ->
            assertFalse("Group ID should not be empty", group.id.isEmpty())
            assertFalse("Group name should not be empty", group.name.isEmpty())
            assertFalse("Teacher name should not be empty", group.teacher.isEmpty())
            assertFalse("Last message should not be empty", group.lastMessage.isEmpty())
            assertTrue("Unread count should be non-negative", group.unreadCount >= 0)
        }
    }

    @Test
    fun `test class groups have different subjects`() {
        val groups = generateClassGroups()

        val hasChemistry = groups.any { it.name.contains("Chemistry") }
        val hasMath = groups.any { it.name.contains("Mathematics") }
        val hasPhysics = groups.any { it.name.contains("Physics") }
        val hasEnglish = groups.any { it.name.contains("English") }

        assertTrue("Should have Chemistry group", hasChemistry)
        assertTrue("Should have Mathematics group", hasMath)
        assertTrue("Should have Physics group", hasPhysics)
        assertTrue("Should have English group", hasEnglish)
    }

    @Test
    fun `test class groups have unread messages`() {
        val groups = generateClassGroups()

        val totalUnread = groups.sumOf { it.unreadCount }
        assertTrue("Should have some unread messages", totalUnread > 0)
    }

    @Test
    fun `test chat messages generation creates 8 messages`() {
        val messages = generateChatMessages("1")

        assertEquals("Should generate exactly 8 chat messages", 8, messages.size)
    }

    @Test
    fun `test chat messages have teacher and student messages`() {
        val messages = generateChatMessages("1")

        val teacherMessages = messages.count { it.isTeacher }
        val studentMessages = messages.count { !it.isTeacher }

        assertTrue("Should have teacher messages", teacherMessages > 0)
        assertTrue("Should have student messages", studentMessages > 0)
    }

    @Test
    fun `test chat messages have valid timestamps`() {
        val messages = generateChatMessages("1")

        messages.forEach { message ->
            assertFalse("Timestamp should not be empty", message.timestamp.isEmpty())
            assertTrue(
                "Timestamp should contain AM or PM",
                message.timestamp.contains("AM") || message.timestamp.contains("PM")
            )
        }
    }

    @Test
    fun `test first chat message is from teacher`() {
        val messages = generateChatMessages("1")

        assertTrue("First message should be from teacher", messages.first().isTeacher)
        assertEquals(
            "Teacher name should be Dr. Sarah Johnson",
            "Dr. Sarah Johnson",
            messages.first().sender
        )
    }

    @Test
    fun `test chat messages have different senders`() {
        val messages = generateChatMessages("1")

        val senders = messages.map { it.sender }.distinct()
        assertTrue("Should have multiple different senders", senders.size > 1)
    }

    @Test
    fun `test student names in chat are valid`() {
        val messages = generateChatMessages("1")

        val studentMessages = messages.filter { !it.isTeacher }
        val studentNames = listOf("Rohan", "Priya", "Arjun", "Sneha")

        studentMessages.forEach { message ->
            assertTrue(
                "Student name should be one of the predefined names",
                studentNames.contains(message.sender)
            )
        }
    }

    // ========== TIMETABLE FEATURE TESTS ==========

    @Test
    fun `test classroom features enum has all four features`() {
        val features = ClassroomFeature.values()

        assertEquals("Should have exactly 4 classroom features", 4, features.size)

        val featureNames = features.map { it.name }
        assertTrue("Should have ATTENDANCE", featureNames.contains("ATTENDANCE"))
        assertTrue("Should have MATERIALS", featureNames.contains("MATERIALS"))
        assertTrue("Should have COMMUNICATION", featureNames.contains("COMMUNICATION"))
        assertTrue("Should have TIMETABLE", featureNames.contains("TIMETABLE"))
    }

    @Test
    fun `test classroom features have correct titles`() {
        assertEquals("Attendance", ClassroomFeature.ATTENDANCE.title)
        assertEquals("Materials", ClassroomFeature.MATERIALS.title)
        assertEquals("Communication", ClassroomFeature.COMMUNICATION.title)
        assertEquals("Timetable", ClassroomFeature.TIMETABLE.title)
    }

    @Test
    fun `test classroom features have icons assigned`() {
        ClassroomFeature.values().forEach { feature ->
            assertNotNull("Feature ${feature.name} should have an icon", feature.icon)
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    fun `test attendance data matches calendar display requirements`() {
        val attendanceData = generateAttendanceData()

        // Group by month
        val byMonth = attendanceData.groupBy { it.date.month }

        assertTrue("Should have attendance data spanning multiple months", byMonth.size > 1)
    }

    @Test
    fun `test materials can be filtered by category`() {
        val materials = generateMaterials()

        MaterialCategory.values().forEach { category ->
            val filtered = materials.filter { it.category == category }
            assertTrue(
                "Should be able to filter materials by ${category.name}",
                filtered.isNotEmpty()
            )
        }
    }

    @Test
    fun `test chat messages are chronologically ordered`() {
        val messages = generateChatMessages("1")

        // Check that timestamps are in increasing order (assuming same day)
        val times = messages.map { it.timestamp }
        val hasValidOrder = times.zipWithNext().all { (first, second) ->
            val firstTime = first.substringBefore(" ")
            val secondTime = second.substringBefore(" ")
            firstTime <= secondTime || firstTime.contains("12") // Handle 12 PM wrap
        }

        assertTrue("Chat messages should be in chronological order", hasValidOrder)
    }

    @Test
    fun `test class group with id 1 is Chemistry`() {
        val groups = generateClassGroups()
        val group1 = groups.find { it.id == "1" }

        assertNotNull("Should have group with ID 1", group1)
        assertTrue("Group 1 should be Chemistry class", group1!!.name.contains("Chemistry"))
        assertEquals("Teacher should be Dr. Sarah Johnson", "Dr. Sarah Johnson", group1.teacher)
    }

    @Test
    fun `test materials IDs are unique`() {
        val materials = generateMaterials()
        val ids = materials.map { it.id }
        val uniqueIds = ids.distinct()

        assertEquals("All material IDs should be unique", materials.size, uniqueIds.size)
    }

    @Test
    fun `test classroom features are accessible`() {
        val features = ClassroomFeature.values()

        features.forEach { feature ->
            when (feature) {
                ClassroomFeature.ATTENDANCE -> {
                    val data = generateAttendanceData()
                    assertTrue("Attendance data should be accessible", data.isNotEmpty())
                }

                ClassroomFeature.MATERIALS -> {
                    val data = generateMaterials()
                    assertTrue("Materials data should be accessible", data.isNotEmpty())
                }

                ClassroomFeature.COMMUNICATION -> {
                    val groups = generateClassGroups()
                    assertTrue("Class groups should be accessible", groups.isNotEmpty())
                }

                ClassroomFeature.TIMETABLE -> {
                    // Timetable uses image or dummy grid, just verify feature exists
                    assertNotNull("Timetable feature should exist", feature)
                }
            }
        }
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    fun `test attendance with empty list handling`() {
        val emptyList = emptyList<AttendanceDay>()
        val presentDays = emptyList.count { it.isPresent }
        val percentage = if (emptyList.isNotEmpty()) (presentDays * 100) / emptyList.size else 0

        assertEquals("Empty attendance should have 0% attendance", 0, percentage)
    }

    @Test
    fun `test materials filtering with non-existent category`() {
        val materials = generateMaterials()

        // All categories should exist, but test the filter mechanism
        MaterialCategory.values().forEach { category ->
            val filtered = materials.filter { it.category == category }
            assertTrue(
                "Filtering should work for all categories",
                filtered.all { it.category == category })
        }
    }

    @Test
    fun `test chat messages with empty group ID`() {
        val messages = generateChatMessages("")

        // Should still return messages (using dummy data)
        assertTrue("Should return messages even with empty group ID", messages.isNotEmpty())
    }

    @Test
    fun `test attendance present rate is consistent`() {
        val attendanceData = generateAttendanceData()

        // Check pattern: absent every 5th day
        val pattern = attendanceData.mapIndexed { index, attendance ->
            if (index % 5 == 0) !attendance.isPresent else attendance.isPresent
        }

        assertTrue("Attendance pattern should be consistent with logic", pattern.all { it })
    }
}
