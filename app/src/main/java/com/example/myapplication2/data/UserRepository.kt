package com.example.myapplication2.data

import android.content.Context
import com.google.gson.Gson
import java.io.File

object UserRepository {
    // Simulated Excel data with test cases
    private val userDatabase = mapOf(
        "1234567890" to UserProfile(
            phoneNumber = "1234567890",
            name = "John Doe",
            email = "john.doe@example.com",
            address = "123 Main Street, City, Country"
        ),
        "9876543210" to UserProfile(
            phoneNumber = "9876543210",
            name = "Jane Smith",
            email = "jane.smith@example.com",
            address = "456 Oak Avenue, Town, Country"
        )
    )

    fun getUserByPhoneNumber(phoneNumber: String): UserProfile? {
        return userDatabase[phoneNumber]
    }

    fun saveCurrentProfile(context: Context, profile: UserProfile) {
        val file = File(context.filesDir, "current_profile")
        val gson = Gson()
        val json = gson.toJson(profile)
        file.writeText(json)
    }

    fun getCurrentProfile(context: Context): UserProfile? {
        val file = File(context.filesDir, "current_profile")
        if (!file.exists()) return null

        val json = file.readText()
        val gson = Gson()
        return gson.fromJson(json, UserProfile::class.java)
    }
}
