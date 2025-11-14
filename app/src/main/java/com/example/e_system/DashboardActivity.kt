package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_system.ui.theme.ESystemTheme

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                DashboardScreen()
            }
        }
    }
}

@Composable
fun DashboardScreen() {
    var selectedItem by remember { mutableStateOf(0) }

    val items = listOf(
        BottomNavItem("Home", R.drawable.home),
        BottomNavItem("Exercise", R.drawable.exercise),
        BottomNavItem("Attendance", R.drawable.account)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF2D4B65),
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                        label = { Text(item.title, fontSize = 12.sp) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.LightGray,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.LightGray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // Fixed: Use a Box to handle padding and display the selected screen
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> HomeScreen()
                1 -> ExerciseScreen()
                2 -> AttendanceScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Home Screen",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ExerciseScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Exercise Screen",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AttendanceScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Attendance Screen",
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

data class BottomNavItem(val title: String, val icon: Int)

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    ESystemTheme {
        DashboardScreen()
    }
}