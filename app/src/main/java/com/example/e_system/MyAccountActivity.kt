package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme
import com.google.rpc.Help

/**
 * Main Activity for the User Account/Profile page.
 */
class MyAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                MyAccountScreen(onBackClick = { finish() })
            }
        }
    }
}

/**
 * Main Composable for the My Account Screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAccountScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            AccountTopBar(onBackClick = onBackClick)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Profile Header (Avatar and Primary Info)
            ProfileHeader(
                name = "Kimleap",
                studentId = "20220901",
                major = "Computer Science"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Settings Group
            AccountSection(title = "Account Settings") {
                AccountActionItem(
                    icon = Icons.Default.Person,
                    label = "Personal Information",
                    onClick = { /* Navigate to personal info edit screen */ }
                )
                AccountActionItem(
                    icon = Icons.Default.Lock,
                    label = "Change Password",
                    onClick = { /* Navigate to password change screen */ }
                )
                AccountActionItem(
                    icon = Icons.Default.Email,
                    label = "Manage Email & Notifications",
                    onClick = { /* Navigate to notification settings */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. System Actions Group
            AccountSection(title = "System") {
                AccountActionItem(
                    icon = Icons.Default.Info,
                    label = "About the App",
                    onClick = { /* Show app version/info */ }
                )
                AccountActionItem(
                    icon = Icons.Default.Home,
                    label = "Help & Support",
                    onClick = { /* Open support link */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Logout Button
            LogoutButton {
                // TODO: Implement actual sign out logic
                println("User attempting to log out")
            }
        }
    }
}

/**
 * Custom Top Bar for the Account Screen, using Card for consistent styling with ExerciseActivity.
 */
@Composable
fun AccountTopBar(onBackClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "My Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
            // Optional: Settings/Edit icon could go here if needed
        }
    }
}

/**
 * Displays the user's profile picture and core identity information.
 */
@Composable
fun ProfileHeader(name: String, studentId: String, major: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Placeholder for Profile Picture (using a simple colored circle)
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50)), // Green color for avatar
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.first().toString(),
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name and Major
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = major,
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ID: $studentId",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
    }
}

/**
 * Groups related account settings items under a clear title.
 */
@Composable
fun AccountSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

/**
 * Reusable row item for settings and actions.
 */
@Composable
fun AccountActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = label,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color.Gray
            )
        }
        if (showDivider) {
            // Check if it's the last item to avoid drawing an unnecessary divider
            Divider(modifier = Modifier.padding(start = 56.dp), thickness = 0.5.dp)
        }
    }
}

/**
 * Dedicated Logout button with a distinct red color for safety.
 */
@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Red color
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 4.dp)
    ) {
        Text("LOG OUT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// --- Preview ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyAccountScreenPreview() {
    ESystemTheme {
        MyAccountScreen(onBackClick = {})
    }
}