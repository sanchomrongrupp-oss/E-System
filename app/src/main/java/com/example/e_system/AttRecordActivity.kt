package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_system.ui.theme.ESystemTheme

// --- Data Model ---
data class AttendanceData(
    val courseName: String,
    val presentCount: Int,
    val absentCount: Int,
    val percentage: String
)

class AttRecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                // Example data for the report
                val reportData = AttendanceData(
                    courseName = "Data Structures",
                    presentCount = 168,
                    absentCount = 0,
                    percentage = "100%"
                )
                AttendanceReportScreen(
                    reportData = reportData,
                    onBackClicked = { finish() },
                    onDownloadClicked = { /* Handle PDF download logic here */ }
                )
            }
        }
    }
}

// --- Main Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceReportScreen(
    reportData: AttendanceData,
    onBackClicked: () -> Unit,
    onDownloadClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Attendance Report",
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
                    containerColor = Color.White // Match the clean white look
                )
            )
        },
        containerColor = Color(0xFFF7F7F7) // Light gray background for the overall screen
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp) // Content padding
        ) {
            // 1. Course Name
            Text(
                text = "Course: ${reportData.courseName}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Report Details (Present, Absent, Percentage)
            Column {
                ReportDataRow(label = "Present:", value = reportData.presentCount.toString())
                Spacer(modifier = Modifier.height(24.dp))
                ReportDataRow(label = "Absent:", value = reportData.absentCount.toString())
                Spacer(modifier = Modifier.height(24.dp))
                ReportDataRow(label = "Percentage:", value = reportData.percentage)
            }

            Spacer(modifier = Modifier.height(56.dp))

            // 3. Download Button
            Button(
                onClick = onDownloadClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.small, // Slightly rounded corners
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black // Black text on white button
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp) // Subtle shadow
            ) {
                Text(
                    text = "Download Report PDF",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// --- Reusable Data Row Composable ---
@Composable
fun ReportDataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label (left side)
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            modifier = Modifier.weight(1f) // Takes up most of the space
        )
        // Value (right side)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold // Highlight the value
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AttendanceReportScreenPreview() {
    ESystemTheme {
        val previewData = AttendanceData(
            courseName = "Data Structures",
            presentCount = 168,
            absentCount = 0,
            percentage = "100%"
        )
        AttendanceReportScreen(
            reportData = previewData,
            onBackClicked = {},
            onDownloadClicked = {}
        )
    }
}