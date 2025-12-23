package com.example.e_system

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.e_system.ui.theme.ESystemTheme

class AttendanceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ESystemTheme {
                AttendanceScreen()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Select Semester") }
    val dropdownOptions = listOf(
        "Mobile System & App",
        "M.I.S",
        "S.E and IT PM",
        "Windows Server Admin",
        "OOAD and Prog"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Top Title Bar
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Attendance",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Responsive Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
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

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Attendance Summary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Responsive Status Boxes Row
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusBox(
                label = "Present",
                color = Color(0xFF4CAF50),
                imageres = R.drawable.present,
                count = 10,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusBox(
                label = "Absent",
                color = Color(0xFF700000),
                imageres = R.drawable.absent,
                count = 2,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusBox(
                label = "Permission",
                color = Color(0xFF2D4B65),
                imageres = R.drawable.permission,
                count = 0,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Scrollable List Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)  // Makes it auto-fill any screen size
                .background(Color(0xFFECECEC), RoundedCornerShape(8.dp))
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AttendanceItem("Tue, 07 Oct", "KHY CHOUER", "P", Color(0xFF4CAF50))
            AttendanceItem("Tue, 05 Oct", "KHY CHOUER", "A", Color(0xFF700000))
            AttendanceItem("Tue, 03 Oct", "KHY CHOUER", "L", Color(0xFF2D4B65))
        }
    }
}

@Composable
fun StatusBox(label: String, color: Color, imageres: Int, count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(130.dp)
            .background(color, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(id = imageres),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(8.dp)
            )

            Text(
                text = label,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$count",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AttendanceItem(date: String, name: String, status: String, color: Color) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Text(text = date, fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontSize = 17.sp
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = status,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAttendanceScreen() {
    AttendanceScreen()
}
