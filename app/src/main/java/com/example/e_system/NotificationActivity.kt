package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme

// --- Data Model for a Notification ---
data class AppNotification(
    val id: Int,
    val title: String,
    val content: String,
    val time: String,
    val isUnread: Boolean,
    val type: NotificationType
)

enum class NotificationType(val color: Color) {
    ASSIGNMENT(Color(0xFFE57373)), // Light Red
    ANNOUNCEMENT(Color(0xFF64B5F6)), // Light Blue
    REMINDER(Color(0xFFFFF176)) // Light Yellow
}

// --- Activity Definition ---
class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                NotificationScreen(onBackClick = { finish() })
            }
        }
    }
}

// --- Main Screen Composable ---
@Composable
fun NotificationScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = { NotificationToolbar(onBackClick = onBackClick) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NotificationList(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// --- Toolbar Component (Good UX: Clear title and back button) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationToolbar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Notifications",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

// --- Notification List (Good UX: Grouped and Scrollable) ---
@Composable
fun NotificationList(modifier: Modifier = Modifier) {
    // Dummy Data Grouped by Time for better UX
    val notificationsByGroup = remember {
        listOf(
            "Today" to listOf(
                AppNotification(1, "New Assignment", "Mobile App Dev due tomorrow.", "10:30 AM", true, NotificationType.ASSIGNMENT),
                AppNotification(2, "Class Cancelled", "SE class at 2 PM is cancelled.", "9:00 AM", true, NotificationType.ANNOUNCEMENT),
            ),
            "Yesterday" to listOf(
                AppNotification(3, "Reminder", "Submit your OOAD case study.", "5:00 PM", false, NotificationType.REMINDER),
            ),
            "Last Week" to listOf(
                AppNotification(4, "Grading Complete", "Check your MIS quiz score.", "Oct 28", false, NotificationType.ANNOUNCEMENT),
            )
        )
    }

    LazyColumn(modifier = modifier.fillMaxSize().background(Color(0xFFF0F0F0))) {
        notificationsByGroup.forEach { (groupTitle, notifications) ->
            // Group Header (UX: Clear separation)
            item {
                Text(
                    text = groupTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // List of Notifications in the Group
            items(notifications) { notification ->
                NotificationItem(notification = notification)
            }

            // Separator between groups
            item {
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.LightGray))
            }
        }
    }
}

// --- Individual Notification Item (Good UX: Visual cues for unread/type) ---
@Composable
fun NotificationItem(notification: AppNotification) {
    val backgroundColor = if (notification.isUnread) Color(0xFFE3F2FD) else MaterialTheme.colorScheme.surface // Light Blue for unread
    val fontWeight = if (notification.isUnread) FontWeight.Bold else FontWeight.Normal

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { /* Handle notification click/open */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon/Type Indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(notification.type.color)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontSize = 16.sp,
                    fontWeight = fontWeight,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = notification.content,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = Color.Gray
                )
            }

            // Time Stamp
            Text(
                text = notification.time,
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

// --- Preview Component ---
@Preview(showBackground = true)
@Composable
fun PreviewNotificationScreen() {
    ESystemTheme {
        NotificationScreen(onBackClick = {})
    }
}