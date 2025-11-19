package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme

class AIChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Keep this if you want edge-to-edge layout
        setContent {
            ESystemTheme {
                // Pass a name (e.g., from an Intent or state) and navigation actions
                RUPPChatScreen(userName = "Kimleap", onBackClick = { finish() })
            }
        }
    }
}

@Composable
fun RUPPChatScreen(userName: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { RUPPToolbar(onBackClick = onBackClick) },
        bottomBar = { RUPPInputBar() },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        // Main Content Area: Chat History & Welcome Message
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center content vertically for the initial screen
        ) {
            Text(
                text = "Hello, $userName",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20) // Deep Green color similar to the image
            )
            // In a real chat app, you would have a LazyColumn here for messages
        }
    }
}

// --- 1. Top Bar Component ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RUPPToolbar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "RUPP Chat",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Handle Menu Click */ }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.Black,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

// --- 2. Input Bar Component ---
@Composable
fun RUPPInputBar() {
    Surface(
        // FIX: Add rounded corners to the top edges of the Surface
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp) // Adjusted height to be more realistic for controls
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Input controls (Left side)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.plus), // Replace with your actual resource
                        contentDescription = "Add",
                        tint = Color.Gray,
                        modifier = Modifier
                            .heightIn(40.dp)
                            .size(24.dp)
                            .clickable { /* Handle plus click */ }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.attach), // Replace with your actual resource
                        contentDescription = "Attach",
                        tint = Color.Gray,
                        modifier = Modifier
                            .heightIn(40.dp)
                            .size(24.dp)
                            .clickable { /* Handle clip click */ }
                    )
                }

                // FIX: Placeholder for Text Input Field (Added Card/Box for visual input field)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp) // Define input box height
                        .padding(horizontal = 10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Ask RUPP Chat",
                            color = Color.Gray,
                            fontSize = 16.sp,
                        )
                    }
                }

                // Voice/Play Icons (Right side)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.mic), // Replace with your actual resource
                        contentDescription = "Voice",
                        tint = Color.Gray,
                        modifier = Modifier
                            .heightIn(40.dp)
                            .size(24.dp)
                            .clickable { /* Handle mic click */ }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.voice), // Replace with your actual resource
                        contentDescription = "Send",
                        tint = Color.Gray,
                        modifier = Modifier
                            .heightIn(40.dp)
                            .size(24.dp)
                            .clickable { /* Handle send click */ }
                    )
                }
            }
        }
    }
}

// --- Preview Component ---
@Preview(showBackground = true)
@Composable
fun RUPPChatPreview() {
    ESystemTheme {
        RUPPChatScreen(userName = "Kimleap", onBackClick = {})
    }
}