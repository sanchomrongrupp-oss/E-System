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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_system.ui.theme.ESystemTheme

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back Button",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* TODO: handle back */ }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Informations",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Main Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 160.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 80.dp, bottom = 16.dp), // leave space for profile image
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "San Chomrong",
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Profile Options
                ProfileOption(R.drawable.account, "My Account") { /*TODO*/ }
                Spacer(modifier = Modifier.height(16.dp))
                ProfileOption(R.drawable.trend, "Score Records") { /*TODO*/ }
                Spacer(modifier = Modifier.height(16.dp))
                ProfileOption(R.drawable.present, "Attendance Records") { /*TODO*/ }
                Spacer(modifier = Modifier.height(16.dp))
                ProfileOption(R.drawable.contact_school, "Contact School") { /*TODO*/ }
                Spacer(modifier = Modifier.height(16.dp))
                ProfileOption(R.drawable.reset_password, "Change Password") { /*TODO*/ }
                Spacer(modifier = Modifier.height(16.dp))
                ProfileOption(R.drawable.log_out, "Log Out", textColor = MaterialTheme.colorScheme.error) { /*TODO*/ }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Profile Image overlapping card
        Box(
            modifier = Modifier
                .padding(top = 100.dp)
                .size(120.dp)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            // Background Circle with shadow
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)

            )
            // Actual profile image
            Image(
                painter = painterResource(id = R.drawable.vanda),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun ProfileOption(
    iconRes: Int,
    text: String,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(23.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "Next",
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ESystemTheme {
        ProfileScreen()
    }
}
