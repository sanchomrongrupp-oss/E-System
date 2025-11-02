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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen() {
    var selectedOption by remember { mutableStateOf("Select Semester") }
    val dropdownOptions = listOf("Semester 1", "Semester 2", "Semester 3")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        // Top Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 55.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Attendance",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = true,
                onExpandedChange = {}
            ) {
                OutlinedTextField(
                    value = selectedOption,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.drop_down),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    readOnly = true
                )
            }
        }

        // Attendance Summary Label
        Text(
            text = "Attendance Summary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Present / Absent / Permission Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusBox(label = "Present", color = Color(0xFF4CAF50),imageres = R.drawable.present,10)   // Green
            StatusBox(label = "Absent", color = Color(0xFF700000),imageres = R.drawable.absent,2)   // Red
            StatusBox(label = "Permission", color = Color(0xFF2D4B65),imageres = R.drawable.permission,0)  // Yellow
        }

        // Scroll Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(490.dp)
                .padding(top = 20.dp)
                .background(Color(0xFFECECEC), RoundedCornerShape(8.dp))
                .verticalScroll(rememberScrollState())
        ) {
            AttendanceItem("Tue, 07 Oct", "KHY CHOUER", "P", Color(0xFF4CAF50))
            AttendanceItem("Tue, 05 Oct", "KHY CHOUER", "A", Color(0xFF700000))
            AttendanceItem("Tue, 03 Oct", "KHY CHOUER", "L", Color(0xFF2D4B65))
        }
    }
}

@Composable
fun StatusBox(label: String, color: Color,imageres: Int,count: Int) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp)
            .background(color, RoundedCornerShape(8.dp)),

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, start = 8.dp),
        ) {Image(
            painter = painterResource(id = imageres),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.White, CircleShape)
                .padding(9.dp)
        )
            Spacer(modifier = Modifier.height(8.dp))

            Text(label,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("$count",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Composable
fun AttendanceItem(date: String, name: String, status: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = date, fontSize = 18.sp, modifier = Modifier.padding(5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp
            )
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(status, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAttendanceScreen() {
    AttendanceScreen()
}
