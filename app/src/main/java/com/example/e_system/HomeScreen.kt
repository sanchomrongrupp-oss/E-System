// HomeScreen.kt
package com.example.e_system

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.e_system.ui.theme.ESystemTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                HomeScreen { }
            }
        }
    }
}
@Composable
fun HomeScreen(onNavigateToProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Profile Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile Image inside Box
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { onNavigateToProfile() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vanda),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("សួស្ដី · Student", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Department Information Technology", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(25.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.message),
                    contentDescription = "Message",
                    modifier = Modifier.size(25.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.notifications),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        ) {
            Image(
                painter = painterResource(id = R.drawable.photorupp),
                contentDescription = "Hot View",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Courses Dropdown
        CourseYearSemesterDropdown(
            year = "Year 4",
            semester = "Semester 2",
            courses = listOf(
                "Mobile App" to R.drawable.smartphone,
                "OOAD" to R.drawable.ooad,
                "SE" to R.drawable.se,
                "Window" to R.drawable.window,
                "MIS" to R.drawable.mis,
                "More Major" to R.drawable.more
            )
        )
    }
}

@Composable
fun CourseYearSemesterDropdown(
    year: String,
    semester: String,
    courses: List<Pair<String, Int>>
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        // Dropdown Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEDEDED))
                .clickable { expanded = !expanded }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(year, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(semester, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Icon(
                painter = painterResource(id = if (expanded) R.drawable.arrow_up else R.drawable.drop_down),
                contentDescription = "Expand",
                modifier = Modifier.size(24.dp)
            )
        }

        // Expanded Items
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            courses.chunked(3).forEach { rowCourses ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowCourses.forEach { (title, imgRes) ->
                        CourseCard(title, imgRes, Modifier.weight(1f))
                    }
                    repeat(3 - rowCourses.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CourseCard(title: String, image: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.Black, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    ESystemTheme {
        HomeScreen(onNavigateToProfile = {})
    }
}
