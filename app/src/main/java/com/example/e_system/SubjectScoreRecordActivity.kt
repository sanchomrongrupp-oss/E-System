//package com.example.e_system
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.animation.Crossfade
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.e_system.ui.theme.ESystemTheme
//
//// --- Data Models for Mock Data ---
//data class Subject(val name: String, val isTranscript: Boolean = false)
//
//data class ScoreItem(
//    val title: String,
//    val weight: String,
//    val score: Double,
//    val isTotal: Boolean = false
//)
//
//data class SemesterScore(
//    val semester: String,
//    val scoreList: List<ScoreItem>
//)
//
//// --- Mock Data ---
//private val mockSubjectList = listOf(
//    Subject("Mobile App"),
//    Subject("SE & IT"),
//    Subject("MIS"),
//    Subject("OOAD"),
//    Subject("Windows Server"),
//    Subject("Academic Transcript", isTranscript = true)
//)
//
//private val mockSemesterData = listOf(
//    SemesterScore(
//        semester = "Semester 1",
//        scoreList = listOf(
//            ScoreItem("Attendances", "10%", 10.0),
//            ScoreItem("Assignment", "10%", 4.0),
//            ScoreItem("Midterm", "20%", 15.0),
//            ScoreItem("Final", "60%", 45.50),
//            ScoreItem("Total Score", "100%", 74.50, isTotal = true),
//            ScoreItem("Grade", "", 0.0) // Grade is handled separately in UI
//        )
//    ),
//    SemesterScore(
//        semester = "Semester 2",
//        scoreList = listOf(
//            ScoreItem("Attendances", "10%", 8.5),
//            ScoreItem("Assignment", "10%", 9.0),
//            ScoreItem("Midterm", "20%", 18.0),
//            ScoreItem("Final", "60%", 50.0),
//            ScoreItem("Total Score", "100%", 85.50, isTotal = true),
//            ScoreItem("Grade", "", 0.0)
//        )
//    )
//)
//
//// --- Activity Setup ---
//class SubjectScoreRecordActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            ESystemTheme {
//                // Start with the Subject List Screen
//                SubjectRecordNavigation(onActivityFinish = { finish() })
//            }
//        }
//    }
//}
//
//// --- Navigation Composable (Manages screen switching) ---
//@Composable
//fun SubjectRecordNavigation(onActivityFinish: () -> Unit) {
//    // State to manage which screen is currently visible
//    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
//
//    // Pass navigation logic to children
//    Crossfade(targetState = selectedSubject, label = "ScreenTransition") { subject ->
//        if (subject == null) {
//            SubjectListScreen(
//                subjects = mockSubjectList,
//                onSubjectClick = { s -> selectedSubject = s },
//                // Back button on the list screen finishes the activity
//                onBackClick = onActivityFinish
//            )
//        } else {
//            // The ScoreDetailScreen only shows if the selectedSubject is not null
//            ScoreDetailScreen(
//                subjectName = subject.name,
//                allSemesterData = mockSemesterData,
//                // Back button on the detail screen returns to the list
//                onBackClick = { selectedSubject = null }
//            )
//        }
//    }
//}
//
//// -----------------------------------------------------------------------------
//// 1. Subject List Screen (List View)
//// -----------------------------------------------------------------------------
//
//@Composable
//fun SubjectListScreen(
//    subjects: List<Subject>,
//    onSubjectClick: (Subject) -> Unit,
//    onBackClick: () -> Unit
//) {
//    Scaffold(
//        topBar = { SimpleToolbar("Score Record", onBackClick) },
//        containerColor = Color(0xFFF7F7F7), // Light gray background
//        modifier = Modifier.fillMaxSize()
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(horizontal = 16.dp)
//        ) {
//            Text(
//                text = "Subjects",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
//            )
//
//            Card(
//                shape = RoundedCornerShape(12.dp),
//                elevation = CardDefaults.cardElevation(4.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                LazyColumn(
//                    contentPadding = PaddingValues(vertical = 8.dp)
//                ) {
//                    items(subjects) { subject ->
//                        SubjectRow(subject = subject, onClick = { onSubjectClick(subject) })
//                        if (subject != subjects.last()) {
//                            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Note: Replace R.drawable.transcript and R.drawable.subject with actual drawable files or appropriate Material Icons
//@Composable
//fun SubjectRow(subject: Subject, onClick: () -> Unit) {
//    // Using simple icons since custom drawables are not available
//    val icon = if (subject.isTranscript) Icons.Default.Check else Icons.AutoMirrored.Filled.KeyboardArrowRight
//    val color = if (subject.isTranscript) Color(0xFF1B5E20) else Color.Black
//    val iconTint = if (subject.isTranscript) Color(0xFF1B5E20) else Color.Gray
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//            .padding(vertical = 12.dp, horizontal = 16.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            // Placeholder for the icon using a Material Icon
//            Box(
//                modifier = Modifier
//                    .size(28.dp)
//                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = icon, // Using a generic icon for demonstration
//                    contentDescription = subject.name,
//                    tint = iconTint.copy(alpha = 0.8f),
//                    modifier = Modifier.size(18.dp)
//                )
//            }
//            Spacer(Modifier.width(16.dp))
//            Text(
//                text = subject.name,
//                fontSize = 16.sp,
//                color = color
//            )
//        }
//        Icon(
//            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
//            contentDescription = "Details",
//            tint = Color.Gray
//        )
//    }
//}
//
//// -----------------------------------------------------------------------------
//// 2. Score Detail Screen (Detail View)
//// -----------------------------------------------------------------------------
//
//@Composable
//fun ScoreDetailScreen(
//    subjectName: String,
//    allSemesterData: List<SemesterScore>,
//    onBackClick: () -> Unit
//) {
//    // State Management for Semester Dropdown
//    val semesters = allSemesterData.map { it.semester }
//    // Ensure initial selection is valid, or default to the first
//    var selectedSemester by remember { mutableStateOf(semesters.firstOrNull() ?: "") }
//
//    val currentScores = allSemesterData.firstOrNull { it.semester == selectedSemester }?.scoreList
//        ?: emptyList()
//
//    Scaffold(
//        // FIX: Use the subjectName for the toolbar title
//        topBar = { SimpleToolbar(subjectName, onBackClick) },
//        containerColor = Color(0xFFF7F7F7),
//        modifier = Modifier.fillMaxSize()
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(horizontal = 16.dp)
//        ) {
//            Text(
//                text = "Score Details:",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
//            )
//
//            // Show dropdown only if there are semesters
//            if (semesters.isNotEmpty()) {
//                SemesterDropdown(
//                    selectedSemester = selectedSemester,
//                    semesters = semesters,
//                    onSemesterSelected = { selectedSemester = it }
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//
//            // Score Detail Card
//            Card(
//                shape = RoundedCornerShape(12.dp),
//                elevation = CardDefaults.cardElevation(4.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                LazyColumn(
//                    contentPadding = PaddingValues(vertical = 8.dp)
//                ) {
//                    items(currentScores) { item ->
//                        ScoreItemRow(item = item)
//                        if (item != currentScores.last()) {
//                            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ScoreItemRow(item: ScoreItem) {
//    val scoreColor = when {
//        item.title == "Grade" -> Color(0xFF1B5E20) // Deep Green
//        item.score < 50.0 && item.title != "Grade" && !item.isTotal -> Color.Red
//        item.isTotal -> Color(0xFF1B5E20) // Deep Green for Total Score
//        else -> Color.Black
//    }
//
//    val displayScore = when (item.title) {
//        "Grade" -> "C" // Mock Grade
//        else -> String.format("%.2f", item.score)
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 12.dp, horizontal = 16.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(
//                imageVector = Icons.Default.Check,
//                contentDescription = "Checked",
//                tint = Color(0xFF1B5E20),
//                modifier = Modifier.size(24.dp)
//            )
//            Spacer(Modifier.width(16.dp))
//            Text(
//                text = item.title,
//                fontSize = 16.sp,
//                fontWeight = if (item.isTotal || item.title == "Grade") FontWeight.Bold else FontWeight.Normal,
//                color = Color.Black
//            )
//        }
//
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            if (item.weight.isNotEmpty()) {
//                Text(
//                    text = item.weight,
//                    fontSize = 16.sp,
//                    color = Color.Gray
//                )
//                Spacer(Modifier.width(24.dp))
//            }
//            Text(
//                text = displayScore,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold,
//                color = scoreColor
//            )
//        }
//    }
//}
//
//@Composable
//fun SemesterDropdown(
//    selectedSemester: String,
//    semesters: List<String>,
//    onSemesterSelected: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = Modifier
//            .wrapContentSize(Alignment.TopStart)
//    ) {
//        OutlinedCard(
//            shape = RoundedCornerShape(8.dp),
//            onClick = { expanded = true },
//            border = CardDefaults.outlinedCardBorder(),
//            modifier = Modifier
//                .height(48.dp)
//                .fillMaxWidth(0.6f) // Takes up 60% of the width
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 12.dp)
//            ) {
//                Text(
//                    text = selectedSemester,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium,
//                    color = Color(0xFF2E4E68)
//                )
//                Icon(
//                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                    contentDescription = "Expand",
//                    tint = Color.Gray
//                )
//            }
//        }
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//            modifier = Modifier.fillMaxWidth(0.6f) // Match the width of the OutlinedCard
//        ) {
//            semesters.forEach { semester ->
//                DropdownMenuItem(
//                    text = { Text(semester) },
//                    onClick = {
//                        onSemesterSelected(semester)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}
//
//
//// -----------------------------------------------------------------------------
//// 3. Reusable Toolbar
//// -----------------------------------------------------------------------------
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SimpleToolbar(title: String, onBackClick: () -> Unit) {
//    TopAppBar(
//        title = {
//            Text(
//                title,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Medium,
//                color = Color.Black
//            )
//        },
//        navigationIcon = {
//            IconButton(onClick = onBackClick) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = "Back",
//                    tint = Color.Black
//                )
//            }
//        },
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = Color.White
//        )
//    )
//}
//
//// -----------------------------------------------------------------------------
//// 4. Previews
//// -----------------------------------------------------------------------------
//
//@Preview(showBackground = true)
//@Composable
//fun SubjectListPreview() {
//    ESystemTheme {
//        SubjectListScreen(subjects = mockSubjectList, onSubjectClick = {}, onBackClick = {})
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ScoreDetailPreview() {
//    ESystemTheme {
//        ScoreDetailScreen(
//            subjectName = "Mobile App Score",
//            allSemesterData = mockSemesterData,
//            onBackClick = {}
//        )
//    }
//}