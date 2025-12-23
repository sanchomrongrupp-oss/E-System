package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_system.ui.theme.ESystemTheme

// --- Data Model for Contact Items ---
data class ContactDetail(
    val title: String,
    val detail: String,
    val icon: Int,
    val actionType: String // e.g., "CALL", "EMAIL", "MAP"
)

class ContactSchoolActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                ContactSchoolScreen(
                    onBackClicked = { finish() },
                    onContactAction = { actionType, detail ->
                        // Handle the specific action (e.g., start phone dialer, open email client)
                        println("Action: $actionType for $detail")
                    }
                )
            }
        }
    }
}

// --- Sample Contact Data ---
val schoolContactInfo = listOf(
    ContactDetail(
        title = "Phone",
        detail = "+1 (555) 123-4567",
        icon = R.drawable.call,
        actionType = "CALL"
    ),
    ContactDetail(
        title = "Email",
        detail = "registrar@schoolname.edu",
        icon = R.drawable.mail,
        actionType = "EMAIL"
    ),
    ContactDetail(
        title = "Address",
        detail = "123 Academic Way, City, State 10001",
        icon = R.drawable.location,
        actionType = "MAP"
    )
)

// --- Main Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSchoolScreen(
    onBackClicked: () -> Unit,
    onContactAction: (String, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contact School",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            painter = painterResource(R.drawable.back),
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White // Clean white header
                )
            )
        },
        containerColor = Color(0xFFF7F7F7) // Light gray background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "Reach out to the school using the methods below:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                )
            }
            items(schoolContactInfo) { contact ->
                ContactItem(contact = contact) {
                    onContactAction(contact.actionType, contact.detail)
                }
                Spacer(modifier = Modifier.height(12.dp)) // Space between cards
            }
        }
    }
}

// --- Reusable Contact Item Card ---
@Composable
fun ContactItem(contact: ContactDetail, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium, // Rounded corners
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon
            Icon(
                painter = painterResource(R.drawable.contact_school),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, // Use a primary color for the icon
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.size(16.dp))

            // Center: Title and Detail
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = contact.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Right: Arrow Indicator
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "Action",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactSchoolScreenPreview() {
    ESystemTheme {
        ContactSchoolScreen(
            onBackClicked = {},
            onContactAction = { _, _ -> }
        )
    }
}