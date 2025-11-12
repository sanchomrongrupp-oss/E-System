package com.example.e_system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen() {
    var selectedOption by remember { mutableStateOf("Select Semester") }
    val dropdownOptions = listOf("Semester 1", "Semester 2", "Semester 3")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Attendance", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.drop_down),
                        contentDescription = null
                    )
                },
                readOnly = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusBox("Present", Color(0xFF4CAF50), R.drawable.present, 10)
            StatusBox("Absent", Color(0xFF700000), R.drawable.absent, 2)
            StatusBox("Permission", Color(0xFF2D4B65), R.drawable.permission, 0)
        }

        Spacer(modifier = Modifier.height(24.dp))

        AttendanceItem("Tue, 07 Oct", "KHY CHOUER", "P", Color(0xFF4CAF50))
        AttendanceItem("Tue, 05 Oct", "KHY CHOUER", "A", Color(0xFF700000))
        AttendanceItem("Tue, 03 Oct", "KHY CHOUER", "L", Color(0xFF2D4B65))
    }
}

@Composable
fun StatusBox(label: String, color: Color, imageRes: Int, count: Int) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .height(120.dp)
            .background(color, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(8.dp)
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
            .height(70.dp)
            .padding(vertical = 4.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(date, fontSize = 14.sp)
            Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(status, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
