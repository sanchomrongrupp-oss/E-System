package com.example.e_system

import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// --- Re-mapping icon resources ---
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_system.ui.theme.ESystemTheme

// Ensure you have these resources in your project or replace them with appropriate Material Icons
// R.drawable.back -> Icons.AutoMirrored.Filled.ArrowBack
// R.drawable.drop_down -> Icons.Filled.ArrowDropDown
// R.drawable.description -> Icons.Default.Description (using Material Icons below)

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

                        // For demonstration, we'll log the intended route:
                        Log.d("Navigation", "Navigating to route: $route")
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

// **NEW:** Dropdown Composable for selecting academic year/semester
@Composable
fun SimpleDropdown(
    label: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(label) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // The element that, when clicked, will display the dropdown
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = selectedItem)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(R.drawable.drop_down), // **FIXED:** Using Material Icon
                contentDescription = "Dropdown Arrow",
                tint = Color.Gray
            )
        }

        // The DropdownMenu composable
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        selectedItem = item
                        expanded = false
                        onItemSelected(item)
                    }
                )
            }
        }
    }
}


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
                        // **NOTE:** R.drawable.back is used. Assuming it exists or replacing with a Material Icon for universal compatibility.
                        IconButton(onClick = onBackClicked) {
                            // Using a Material Icon for demonstration, replace with painterResource(R.drawable.back) if preferred.
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

                // **FIXED DROPDOWN PLACEMENT**
                SimpleDropdown(
                    label = "Academic Year: 2024-2025",
                    items = listOf("2024-2025", "2023-2024", "2022-2023"),
                    onItemSelected = { selectedYear ->
                        Log.d("Dropdown", "Selected Year: $selectedYear")
                        // Perform data filtering/refresh here
                    }
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

// --- Reusable Item Composable ---
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
                    // **FIXED:** Using a Material Icon for the document/description
                    Icon(
                        painter = painterResource(R.drawable.description),
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
        // **FIXED:** Using Icons.AutoMirrored.Filled.KeyboardArrowRight
        Icon(
            painter = painterResource(R.drawable.back),
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