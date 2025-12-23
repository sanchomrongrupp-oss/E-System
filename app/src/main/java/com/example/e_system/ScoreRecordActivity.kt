package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme

// --- Data Models for Mock Data ---
data class Subject(val name: String, val isTranscript: Boolean = false)

data class ScoreItem(
    val title: String,
    val weight: String,
    val score: Double,
    val isTotal: Boolean = false,
    val gradeValue: String? = null // Added for explicit grade display
)

data class SemesterScore(
    val semester: String,
    val scoreList: List<ScoreItem>
)

// --- Mock Data ---
private val mockSubjectList = listOf(
    Subject("Mobile App"),
    Subject("SE & IT"),
    Subject("MIS"),
    Subject("OOAD"),
    Subject("Windows Server"),
    Subject("Academic Transcript", isTranscript = true)
)

private val mockSemesterData = listOf(
    SemesterScore(
        semester = "Semester 1",
        scoreList = listOf(
            ScoreItem("Attendances", "10%", 10.0),
            ScoreItem("Assignment", "10%", 4.0),
            ScoreItem("Midterm", "20%", 15.0),
            ScoreItem("Final", "60%", 45.50),
            ScoreItem("Total Score", "100%", 74.50, isTotal = true),
            ScoreItem("Grade", "", 0.0, gradeValue = "C") // Explicit grade
        )
    ),
    SemesterScore(
        semester = "Semester 2",
        scoreList = listOf(
            ScoreItem("Attendances", "10%", 8.5),
            ScoreItem("Assignment", "10%", 9.0),
            ScoreItem("Midterm", "20%", 18.0),
            ScoreItem("Final", "60%", 50.0),
            ScoreItem("Total Score", "100%", 85.50, isTotal = true),
            ScoreItem("Grade", "", 0.0, gradeValue = "B")
        )
    )
)

// --- Activity Setup ---
class ScoreRecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                ScoreRecordNavigation(onActivityFinish = { finish() })
            }
        }
    }
}

// --- Navigation Composable (Manages screen switching) ---
@Composable
fun ScoreRecordNavigation(onActivityFinish: () -> Unit) {
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }

    Crossfade(targetState = selectedSubject, label = "ScreenTransition") { subject ->
        if (subject == null) {
            SubjectListScreen(
                subjects = mockSubjectList,
                onSubjectClick = { s -> selectedSubject = s },
                onBackClick = onActivityFinish
            )
        } else {
            ScoreDetailScreen(
                subjectName = subject.name,
                allSemesterData = mockSemesterData,
                onBackClick = { selectedSubject = null }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 1. Subject List Screen (List View) - No changes needed, already good.
// -----------------------------------------------------------------------------

@Composable
fun SubjectListScreen(
    subjects: List<Subject>,
    onSubjectClick: (Subject) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = { SimpleToolbar("Score Record", onBackClick) },
        containerColor = Color(0xFFF7F7F7),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Subjects",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(subjects) { subject ->
                        SubjectRow(subject = subject, onClick = { onSubjectClick(subject) })
                        if (subject != subjects.last()) {
                            Divider(color = Color(0xFFEEEEEE), thickness = 2.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectRow(subject: Subject, onClick: () -> Unit) {
    val leadingIcon: Painter = if (subject.isTranscript) painterResource(R.drawable.check) else painterResource(R.drawable.next)
    val color = if (subject.isTranscript) Color(0xFF1B5E20) else Color.Black
    val iconTint = if (subject.isTranscript) Color(0xFF1B5E20) else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = subject.name,
                    tint = iconTint.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = subject.name,
                fontSize = 16.sp,
                color = color
            )
        }
        Icon(
            painter = painterResource(R.drawable.next),
            contentDescription = "Details",
            tint = Color.Gray
        )
    }
}

// -----------------------------------------------------------------------------
// 2. Score Detail Screen (Detail View) - **UPDATED** to match image
// -----------------------------------------------------------------------------

@Composable
fun ScoreDetailScreen(
    subjectName: String,
    allSemesterData: List<SemesterScore>,
    onBackClick: () -> Unit
) {
    val semesters = allSemesterData.map { it.semester }
    var selectedSemester by remember { mutableStateOf(semesters.firstOrNull() ?: "") }

    val currentScores = allSemesterData.firstOrNull { it.semester == selectedSemester }?.scoreList
        ?: emptyList()

    Scaffold(
        topBar = { SimpleToolbar(subjectName, onBackClick) }, // Subject name in toolbar
        containerColor = Color(0xFFF7F7F7),
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // Add some space below toolbar

            // **UPDATED**: Header combining "Score Details" with selected semester
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Score Details: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                // **UPDATED**: Semester Dropdown for styling
                SemesterDropdownStyled(
                    selectedSemester = selectedSemester,
                    semesters = semesters,
                    onSemesterSelected = { selectedSemester = it }
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Score Detail Card (Main content card)
            Card(
                shape = RoundedCornerShape(12.dp),
                // **UPDATED**: Shadow color closer to image
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)) // Light blue background for the card
            ) {
                // **UPDATED**: Padding for the entire card content, no lazy column padding
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    currentScores.forEachIndexed { index, item ->
                        ScoreItemRow(item = item)
                        // **REMOVED**: No dividers between items in the image
                        // if (item != currentScores.last()) {
                        //     Divider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        // }
                    }
                }
            }
        }
    }
}

// **UPDATED**: ScoreItemRow for grade logic and alignment
@Composable
fun ScoreItemRow(item: ScoreItem) {
    val scoreColor = when {
        item.title == "Grade" -> Color(0xFF1B5E20) // Deep Green
        item.score < 50.0 && !item.isTotal -> Color.Red
        item.isTotal -> Color(0xFF1B5E20) // Deep Green for Total Score
        else -> Color(0xFF1B5E20) // Default green for other scores
    }

    val displayScore = when (item.title) {
        "Grade" -> item.gradeValue ?: "N/A" // Use gradeValue if available
        else -> String.format("%.1f", item.score) // Format to one decimal place
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp), // Adjust padding as needed
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Distribute space
    ) {
        // Left side: Icon and Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.check),
                contentDescription = "Checked",
                tint = Color(0xFF1B5E20), // Green checkmark
                modifier = Modifier.size(20.dp) // Smaller icon
            )
            Spacer(Modifier.width(12.dp)) // Smaller spacer
            Text(
                text = item.title,
                fontSize = 15.sp, // Slightly smaller font
                fontWeight = if (item.isTotal || item.title == "Grade") FontWeight.Bold else FontWeight.Normal,
                color = Color.Black
            )
        }

        // Right side: Weight and Score
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (item.weight.isNotEmpty()) {
                Text(
                    text = item.weight,
                    fontSize = 15.sp, // Match title font size
                    color = Color.Gray,
                    modifier = Modifier.width(40.dp) // Fixed width for weight alignment
                )
            }
            // Spacer here if weight is present, to push score to the right
            if (item.weight.isNotEmpty()) {
                Spacer(Modifier.width(16.dp))
            }

            Text(
                text = displayScore,
                fontSize = 15.sp, // Match title font size
                fontWeight = FontWeight.Bold,
                color = scoreColor,
                modifier = Modifier.width(50.dp), // Fixed width for score alignment
            )
        }
    }
}


// **NEW/UPDATED**: Styled Semester Dropdown to match the image
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterDropdownStyled(
    selectedSemester: String,
    semesters: List<String>,
    onSemesterSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .height(IntrinsicSize.Min) // Allow children to define min height
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            onClick = { expanded = true },
            colors = CardDefaults.cardColors(containerColor = Color.White), // White background for the dropdown trigger
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .width(150.dp) // Fixed width for dropdown
                .height(35.dp) // Fixed height for dropdown
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp) // Adjust padding inside the card
            ) {
                Text(
                    text = selectedSemester,
                    fontSize = 15.sp, // Smaller font for dropdown text
                    fontWeight = FontWeight.Medium,
                    color = Color.Black, // Black text
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = if (expanded) painterResource(R.drawable.arrow_up) else painterResource(R.drawable.drop_down),
                    contentDescription = "Expand",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp) // Smaller icon
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(150.dp) // Match the width of the Card
        ) {
            semesters.forEach { semester ->
                DropdownMenuItem(
                    text = {
                        Text(
                            semester,
                            fontSize = 15.sp, // Match dropdown item font
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onSemesterSelected(semester)
                        expanded = false
                    }
                )
            }
        }
    }
}


// -----------------------------------------------------------------------------
// 3. Reusable Toolbar - No changes needed, already good.
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleToolbar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
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
            containerColor = Color.White
        )
    )
}

// -----------------------------------------------------------------------------
// 4. Previews
// -----------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun SubjectListPreview() {
    ESystemTheme {
        SubjectListScreen(subjects = mockSubjectList, onSubjectClick = {}, onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ScoreDetailPreview() {
    ESystemTheme {
        ScoreDetailScreen(
            subjectName = "Mobile App", // Subject name to display in toolbar
            allSemesterData = mockSemesterData,
            onBackClick = {}
        )
    }
}