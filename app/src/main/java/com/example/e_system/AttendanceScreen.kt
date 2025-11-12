package com.example.e_system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen() {
    var selectedOption by remember { mutableStateOf("Select your major") }
    var expanded by remember { mutableStateOf(false) }
    val dropdownOptions = listOf(
        "Mobile System & App",
        "S.E and IT PM",
        "Windows Server Admin",
        "M.I.S",
        "OOAD and Prog"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title
        Text("Attendance", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(), // required for correct positioning
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                dropdownOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOption = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Status Row (Responsive)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusBox("Present", Color(0xFF4CAF50), R.drawable.present, 10, Modifier.weight(1f))
            StatusBox("Absent", Color(0xFF700000), R.drawable.absent, 2, Modifier.weight(1f))
            StatusBox("Permission", Color(0xFF2D4B65), R.drawable.permission, 0, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Attendance Items
        val attendanceList = listOf(
            Triple("Tue, 07 Oct", "KHY CHOUER", "P"),
            Triple("Tue, 05 Oct", "KHY CHOUER", "A"),
            Triple("Tue, 03 Oct", "KHY CHOUER", "L")
        )

        attendanceList.forEach { (date, name, status) ->
            val color = when (status) {
                "P" -> Color(0xFF4CAF50)
                "A" -> Color(0xFF700000)
                else -> Color(0xFF2D4B65)
            }
            AttendanceItem(date, name, status, color)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun StatusBox(label: String, color: Color, imageRes: Int, count: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .aspectRatio(1f) // keeps box square and responsive
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.4f) // responsive icon size
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(Color(0x33000000))
                .padding(4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
        Text("$count", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun AttendanceItem(date: String, name: String, status: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(date, fontSize = 14.sp)
            Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .weight(0.2f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(status, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAttendanceScreen() {
    AttendanceScreen()
}
