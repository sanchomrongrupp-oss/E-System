package com.example.e_system

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// --- Data Class for Subjects ---
data class Subjected(
    val name: String,
    val academicYear: String,
    val isTranscript: Boolean = false
)

// --- Define Routes (for demonstration) ---
object ScoreRoutes {
    const val SUBJECT_DETAIL = "subject_detail/{subjectName}"
    const val ACADEMIC_TRANSCRIPT = "academic_transcript"
}

// --- List of subjects to display (MOCKED DATA with all years) ---
val allSubjectsList = listOf(
    Subjected("Mobile App", "Academic Year: 4"),
    Subjected("SE & IT", "Academic Year: 4"),
    Subjected("MIS", "Academic Year: 4"),
    Subjected("OOAD", "Academic Year: 4"),
    Subjected("Windows Server", "Academic Year: 4"),
    Subjected("Academic Transcript", "Academic Year: 4", isTranscript = true) // Keeping transcript here
)

// Group subjects by year and sort by year descending (4, then 1)
val groupedSubjects = allSubjectsList
    .groupBy { it.academicYear }
    .toSortedMap(compareByDescending { it.substringAfter(": ").toIntOrNull() ?: 0 })


// The Activity remains unchanged
class ScoreRecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ESystemTheme {
                ScoreRecordScreen(
                    onBackClicked = { finish() },
                    onSubjectClicked = { subjectName ->
                        // **ROUTING LOGIC** (Placeholder for Navigation)
                        val route = if (subjectName == "Academic Transcript") {
                            ScoreRoutes.ACADEMIC_TRANSCRIPT
                        } else {
                            val encodedSubjectName = URLEncoder.encode(subjectName, StandardCharsets.UTF_8.toString())
                            ScoreRoutes.SUBJECT_DETAIL.replace("{subjectName}", encodedSubjectName)
                        }

                        // In a real app, this would use an Intent or NavController to switch screens.
                        Log.d("Navigation", "Navigating to route: $route")
                    }
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 1. Main Screen Composable (Grouped by Year with Scrolling Headers)
// -----------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreRecordScreen(
    onBackClicked: () -> Unit,
    onSubjectClicked: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Score Record",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            painter = painterResource(R.drawable.back),
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF7F7F7) // Light gray background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Space between elements/groups
        ) {
            // Iterate over the grouped subjects (keys are academic years)
            groupedSubjects.forEach { (academicYear, subjects) ->
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    // **Academic Year/Semester Header Card** - Scrolls with content
                    YearHeaderCard(year = academicYear)
                }

                // Group the actual subject items under the header
                items(subjects) { subject ->
                    SubjectCardItem( // Each subject gets its own card
                        subject = subject,
                        onClick = { onSubjectClicked(subject.name) }
                    )
                }

                // Add a bigger space between different academic year groups
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. Year Header Card (Scrolls with list)
// -----------------------------------------------------------------------------
@Composable
fun YearHeaderCard(year: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)), // Light gray background for header
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = year,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 3. Subject Card Item (Individual Card for each subject)
// -----------------------------------------------------------------------------
@Composable
fun SubjectCardItem(subject: Subjected, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), // Padding to separate from other subject cards/headers
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        // The content inside this card is the SubjectListItem
        SubjectListItem(subject = subject, onClick = onClick)
    }
}

// -----------------------------------------------------------------------------
// 4. Reusable Item Composable (List Row content)
// -----------------------------------------------------------------------------
@Composable
fun SubjectListItem(subject: Subjected, onClick: () -> Unit) {
    // Define colors based on whether it's the Academic Transcript
    val textColor = if (subject.isTranscript) Color(0xFF1B5E20) else Color.Black
    val iconTint = if (subject.isTranscript) Color(0xFF1B5E20) else Color(0xFFBDBDBD)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Clickable on the whole row inside the card
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Side: Icon and Text
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.document),
                        contentDescription = "Document Icon",
                        tint = iconTint,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Subject name only (year is in the header card)
            Text(
                text = subject.name,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Right Side: Navigation Arrow
        Icon(
            painter = painterResource(R.drawable.next),
            contentDescription = "Navigate",
            tint = Color(0xFFC0C0C0)
        )
    }
}

// -----------------------------------------------------------------------------
// 5. Preview
// -----------------------------------------------------------------------------
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