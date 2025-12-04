package com.example.e_system

import android.os.Bundle
import android.util.Log // Added for logging the navigation intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
// Removed unused import: import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_system.ui.theme.ESystemTheme

// --- Data Class for Subjects ---
data class Subjected(val name: String, val isTranscript: Boolean = false)

// --- Define Routes (for demonstration) ---
object ScoreRoutes {
    const val SUBJECT_DETAIL = "subject_detail/{subjectName}"
    const val ACADEMIC_TRANSCRIPT = "academic_transcript"
}

class ScoreRecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ESystemTheme {
                ScoreRecordScreen(
                    onBackClicked = { finish() },
                    onSubjectClicked = { subjectName ->
                        // --- Core Navigation Logic ---
                        val route = when (subjectName) {
                            "Academic Transcript" -> ScoreRoutes.ACADEMIC_TRANSCRIPT
                            else -> ScoreRoutes.SUBJECT_DETAIL.replace("{subjectName}", subjectName)
                        }

                        // In a real app, you would use a NavController here:
                        // navController.navigate(route)

                        // For demonstration, we'll log the intended route:
                        Log.d("Navigation", "Navigating to route: $route")
                        // Handle actual navigation to the detail screen for the subject/transcript
                    }
                )
            }
        }
    }
}

// --- List of subjects to display ---
val subjectsList = listOf(
    Subjected("Mobile App"),
    Subjected("SE & IT"),
    Subjected("MIS"),
    Subjected("OOAD"),
    Subjected("Windows Server"),
    Subjected("Academic Transcript", isTranscript = true)
)

// --- Main Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreRecordScreen(
    onBackClicked: () -> Unit,
    onSubjectClicked: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                // Header with Score Record title and Back Button
                TopAppBar(
                    title = {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Score Record",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                painter = painterResource(R.drawable.back),
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )

                // Subtitle "Subjects" below the main header
                Text(
                    text = "Subjects",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
                    color = Color.Black
                )
            }
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Content Area with a Card for the list
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subjectsList) { subject ->
                        // The click handler here passes the subject name up to the activity
                        SubjectListItem(
                            subject = subject,
                            onClick = { onSubjectClicked(subject.name) }
                        )
                        // Add divider after each item, except the last one
                        if (subject != subjectsList.last()) {
                            Divider(
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Reusable Item Composable (FIXED: Uses Material Icon instead of R.drawable.document) ---
@Composable
fun SubjectListItem(subject: Subjected, onClick: () -> Unit) {
    val textColor = if (subject.isTranscript) Color(0xFF1B5E20) else Color.Black // Dark green for transcript
    val iconTint = if (subject.isTranscript) Color(0xFF1B5E20) else Color(0xFFBDBDBD) // Matching or light gray icon

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side: Icon and Subject Name
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Document/File Icon Simulation (using Material Icons)
            Box(
                modifier = Modifier.size(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color(0xFFF5F5F5), // Very light gray background
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.description), // Using a standard file/document icon
                        contentDescription = "Document Icon",
                        tint = iconTint,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = subject.name,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Right side: Arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Navigate",
            tint = Color(0xFFC0C0C0)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreRecordScreenPreview() {
    ESystemTheme {
        ScoreRecordScreen(
            onBackClicked = {},
            onSubjectClicked = {}
        )
    }
}