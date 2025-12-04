package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme

/**
 * Main Activity for the Exercise/Assignment details page.
 */
class ExerciseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ESystemTheme {
                // Example data setup for the screen
                ExerciseScreen(
                    exerciseName = "Android Jetpack Compose",
                    subjectName = "Mobile App Development",
                    dueDate = "2025-11-06",
                    status = "Not Started", // Change to "Submitted" or "Graded" to see status change
                    score = null, // Set to 92 if status is "Graded"
                    onBackClick = { finish() }
                )
            }
        }
    }
}

/**
 * Custom Top Bar component using a Card for better visual separation and styling.
 */
@Composable
fun CustomExerciseTopBar(
    exerciseName: String,
    subjectName: String,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        // Apply rounded shape only to the bottom edge
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Use surface color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(vertical = 4.dp), // Adjust padding for a compact look
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title and Subject
            Column(
                modifier = Modifier
                    .weight(1f) // Takes up most of the space
                    .padding(start = 8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = exerciseName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subjectName,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Actions Button
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More options")
            }
        }
    }
}

/**
 * Main Composable for the Exercise Screen, incorporating good UX/UI design.
 * @param exerciseName The title of the assignment.
 * @param subjectName The subject/course name.
 * @param dueDate The submission deadline.
 * @param status The current status (e.g., "Not Started", "Submitted", "Graded").
 * @param score The score received (nullable, only displayed if Graded).
 * @param onBackClick Lambda to handle navigation back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    exerciseName: String = "Exercise Title",
    subjectName: String = "Subject",
    dueDate: String = "N/A",
    status: String = "Pending",
    score: Int? = null,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            // Use the new custom card-based TopBar
            CustomExerciseTopBar(
                exerciseName = exerciseName,
                subjectName = subjectName,
                onBackClick = onBackClick,
                onMenuClick = { /* Handle menu/options */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Status and Details Card (Key Information)
            StatusCard(dueDate = dueDate, status = status, score = score)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Submission Button (Primary Action based on state)
            SubmissionButton(status = status) {
                // In a real app, this would trigger navigation or a submission workflow
                println("Submission action triggered. Status: $status")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Exercise Description/Content Card
            ContentCard()
        }
    }
}

/**
 * Displays the Due Date, Status, and Score in a clean Card layout.
 */
@Composable
fun StatusCard(dueDate: String, status: String, score: Int?) {
    // Define colors based on status for visual distinction
    val statusColor = when (status) {
        "Submitted" -> Color(0xFF4CAF50) // Green
        "Graded" -> Color(0xFF2196F3) // Blue
        "Not Started" -> Color(0xFFFBC02D) // Yellow/Amber
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Due Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Due Date:", fontWeight = FontWeight.Medium, color = Color.Gray)
                Text(dueDate, fontWeight = FontWeight.SemiBold)
            }
            Divider(Modifier.padding(vertical = 8.dp))

            // Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status:", fontWeight = FontWeight.Medium, color = Color.Gray)
                Chip(
                    label = status,
                    color = statusColor
                )
            }
            Divider(Modifier.padding(vertical = 8.dp))

            // Score Row (Only displays if status is Graded)
            if (status == "Graded") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Score:", fontWeight = FontWeight.Medium, color = Color.Gray)
                    Text(
                        text = if (score != null) "$score / 100" else "Not Released",
                        fontWeight = FontWeight.Bold,
                        color = if (score != null) Color(0xFF2196F3) else Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * The main action button, dynamically changing text and color based on the submission status.
 */
@Composable
fun SubmissionButton(status: String, onClick: () -> Unit) {
    val (buttonText, buttonColor, enabled) = when (status) {
        "Submitted" -> Triple("VIEW SUBMISSION", Color(0xFF1976D2), true) // Blue for review
        "Graded" -> Triple("VIEW FEEDBACK", Color(0xFF00796B), true) // Teal for graded
        else -> Triple("START ASSIGNMENT", Color(0xFF4CAF50), true) // Green for action
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 4.dp)
    ) {
        Text(buttonText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

/**
 * Card containing the assignment description and requirements.
 */
@Composable
fun ContentCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Description",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This assignment requires you to build a fully responsive single-screen application using Jetpack Compose, demonstrating state management and navigation. Please attach your source code zip file upon submission.",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Required Files: 1 x ZIP file (Max 10MB)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Reusable Status Chip for visual state representation.
 */
@Composable
fun Chip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.2f)) // Light background tint
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

// --- Preview Composable functions for visual verification ---

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewExerciseScreenNotStarted() {
    ESystemTheme {
        // Example 1: Not Started (Ready to Start Assignment)
        ExerciseScreen(
            exerciseName = "Compose Navigation Lab",
            subjectName = "Mobile App Dev",
            dueDate = "2025-12-10",
            status = "Not Started",
            score = null,
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewExerciseScreenGraded() {
    ESystemTheme {
        // Example 2: Graded (Ready to View Feedback)
        ExerciseScreen(
            exerciseName = "Database Design Project",
            subjectName = "Database Management",
            dueDate = "2025-10-15",
            status = "Graded",
            score = 92,
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewExerciseScreenSubmitted() {
    ESystemTheme {
        // Example 3: Submitted (Ready to View Submission)
        ExerciseScreen(
            exerciseName = "UI/UX Case Study",
            subjectName = "Human Computer Interaction",
            dueDate = "2025-11-01",
            status = "Submitted",
            score = null,
            onBackClick = {}
        )
    }
}