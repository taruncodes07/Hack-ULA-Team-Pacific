package com.example.myapplication2.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication2.data.UserRepository
import com.example.myapplication2.ui.theme.AppBlack
import com.example.myapplication2.ui.theme.AppPurple
import kotlinx.coroutines.delay

@Composable
fun GuestMainPage() {
    val context = LocalContext.current
    val userProfile = remember { UserRepository.getCurrentProfile(context) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBlack),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Welcome, Guest!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = AppPurple
                )

                Spacer(modifier = Modifier.height(40.dp))

                userProfile?.let { profile ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AppPurple.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "User Profile",
                                style = MaterialTheme.typography.headlineMedium,
                                color = AppPurple
                            )

                            ProfileItem(label = "Name", value = profile.name)
                            ProfileItem(label = "Phone", value = profile.phoneNumber)
                            ProfileItem(label = "Email", value = profile.email)
                            ProfileItem(label = "Address", value = profile.address)
                        }
                    }
                } ?: run {
                    Text(
                        text = "No profile data found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppPurple
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = AppPurple.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = AppPurple
        )
    }
}
