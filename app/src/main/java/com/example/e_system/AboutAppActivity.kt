package com.example.e_system

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme

class AboutAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                AboutAppScreen(onBackClick = { finish() })
            }
        }
    }
}

// --- Main Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About This App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painter = painterResource(R.drawable.back), contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = Color(0xFFF5F5F5) // Light background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. App Logo/Icon
            AppBranding(appName = "E-System", appVersion = "1.0.0")

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Core Information Card
            AppInfoCard(
                title = "App Information",
                items = listOf(
                    "Developer" to "San Chomrong\nSuk Sokha\nKimleap Meng\nKhy Chouer\nChev Son",
                    "Release Date" to "15th December 2025",
                    "License" to "Proprietary"
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Legal/Links Section
            AppLinksCard(
                onPrivacyClick = {
                    // Example: Open a web link
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yourcompany.com/privacy"))
                    context.startActivity(intent)
                },
                onTermsClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yourcompany.com/terms"))
                    context.startActivity(intent)
                },
                onContactClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@yourcompany.com")
                        putExtra(Intent.EXTRA_SUBJECT, "E-System App Support")
                    }
                    context.startActivity(Intent.createChooser(intent, "Send Email"))
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Footer / Copyright
            Text(
                text = "© 2025 E-System. All rights reserved.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- Reusable Components ---

@Composable
fun AppBranding(appName: String, appVersion: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Placeholder for App Icon (Use your actual R.drawable.app_icon)
        Card(
            modifier = Modifier.size(96.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Assuming you have an app icon resource named 'app_icon'
                // If not, use Icons.Default.Info as a temporary placeholder
                Icon(
                    painter = painterResource(R.drawable.info),
                    contentDescription = appName,
                    tint = Color(0xFF2E4E68), // Dark Blue color
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = appName,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Text(
            text = "Version $appVersion",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun AppInfoCard(title: String, items: List<Pair<String, String>>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Divider(color = Color.LightGray, thickness = 1.dp)

            items.forEach { (label, value) ->
                AppDetailRow(label = label, value = value)
                if (value != items.last().second) {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun AppDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = Color.Black
        )
    }
}

@Composable
fun AppLinksCard(
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onContactClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AppLinkItem(label = "Privacy Policy", onClick = onPrivacyClick)
            Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
            AppLinkItem(label = "Terms of Service", onClick = onTermsClick)
            Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
            AppLinkItem(label = "Contact Support", onClick = onContactClick)
        }
    }
}

@Composable
fun AppLinkItem(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF2E4E68) // Use a distinct color for clickable links
        )
        Icon(
            painter = painterResource(id = R.drawable.arrow_right), // Assuming you have R.drawable.arrow_right
            contentDescription = "Go to $label",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// --- Preview ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AboutAppScreenPreview() {
    ESystemTheme {
        AboutAppScreen(onBackClick = {})
    }
}