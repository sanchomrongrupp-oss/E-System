package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.graphics.shapes.circle
import com.example.e_system.ui.theme.ESystemTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Top Row: Profile + actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile image
                Image(
                    painter = painterResource(id = R.drawable.vanda),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Gray, CircleShape)
                        .padding(0.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Name & department
                Column {
                    Text(
                        text = "សួស្ដី · Student",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Department Information Technology",
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search",
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.message),
                        contentDescription = "Message",
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.notifications),
                        contentDescription = "Notifications",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Banner / Hot View
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
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable Courses Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
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
                CourseYearSemesterDropdown(
                    year = "Year 4",
                    semester = "Semester 1",
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
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(12.dp)
                .background(Color(0xFFEDEDED), RoundedCornerShape(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text(
                text = year,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
                Text(
                    text = semester,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }

            Icon(
                painter = painterResource(
                    id = if (expanded) R.drawable.arrow_up else R.drawable.drop_down
                ),
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp),
            )

            // Split courses manually for two rows
            val firstRow = courses.take(3)   // Mobile App, OOAD, SE
            val secondRow = courses.drop(3)  // Window, MIS, More Major

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                firstRow.forEach { course ->
                    CourseCard(
                        title = course.first,
                        name_image = course.second,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                secondRow.forEach { course ->
                    CourseCard(
                        title = course.first,
                        name_image = course.second,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun CourseCard(title: String,name_image: Int,modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = name_image),
                contentDescription = "Image_Major",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.Black,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.home), contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.exercise), contentDescription = "Exercise") },
            label = { Text("Excerise") },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.person), contentDescription = "Attendance") },
            label = { Text("Attendance") },
            selected = false,
            onClick = { /*TODO*/ }
        )
    }
}

// ------------------- Previews -------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    ESystemTheme {
        HomeScreen()
    }
}
