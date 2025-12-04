package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

/**
 * Main Activity for the User Account/Profile page (My Account Information).
 */
class MyAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                MyAccountScreen(onBackClick = { finish() })
            }
        }
    }
}

/**
 * Main Composable for the My Account Screen, displaying user details in a table format.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAccountScreen(onBackClick: () -> Unit) {
    // Dummy Data to replicate the image content
    val userData = mapOf(
        "Full Name:" to "Meng Kimleap",
        "Name in Khmer:" to "ម៉េង គីមលាប",
        "Gender:" to "Female",
        "Date of Birth:" to "03-April-2004",
        "Place of Birth:" to "Phnom Penh",
        "Email:" to "meng11@gmail.com",
        "Phone:" to "098 776 656",
        "Occupation:" to "Student",
        "Address:" to "Samraong Kraom, Pursenchey, Phnom Penh",
        "Study Shift:" to "Evening : 5:30 - 8:30PM"
    )
    Scaffold(
        topBar = {
            // Reusing the simple Top Bar structure from the image
            TopAppBar(
                title = {
                    Text(
                        text = "Personal Information",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.back) ,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White // Light Blue bar background
                ),
                modifier = Modifier.background(Color(0xFFE3F2FD)) // Optional: Add light blue to the background of the entire TopAppBar area
            )
        },
        containerColor = Color(0xFFF0F4F7) // Light gray/blue background for the screen
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Card Container for the detailed fields
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Iterate through the map to create the rows
                    userData.entries.forEachIndexed { index, entry ->
                        AccountDetailRow(
                            label = entry.key,
                            value = entry.value,
                            isLastItem = (index == userData.size - 1)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single Row for displaying a label and its corresponding value.
 */
@Composable
fun AccountDetailRow(
    label: String,
    value: String,
    isLastItem: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Label (e.g., Full Name)
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.weight(1f))

            // Value (e.g., Meng Kimleap)
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }

        // Horizontal Divider as shown in the image, except for the last item
        if (!isLastItem) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyAccountScreenPreview() {
    ESystemTheme {
        MyAccountScreen(onBackClick = {})
    }
}