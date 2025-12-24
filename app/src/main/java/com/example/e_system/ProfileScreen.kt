package com.example.e_system

import android.content.Context
import android.content.Intent
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
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class StudentdentProfile(
    val fullName: String,
)

interface ApiServicestudentprofile {
    @GET("api/v1/student/me")
    suspend fun getStudentMe(): Response<StudentdentProfile>
}
object RetrofitClientstudentprofile {
    private const val BASE_URL = "http://10.0.2.2:4000/"

    fun getClient(context: Context): ApiServicestudentprofile {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = TokenManager(context).getToken()
                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(ApiServicestudentprofile::class.java)
    }
}

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                ProfileScreen(
                    onNavigateToHome = {
                        // Assuming MainActivity is your home screen
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onLogoutConfirmed = {
                        // 1. CLEAR THE TOKEN
                        TokenManager(this).clear()

                        // 2. REDIRECT TO LOGIN
                        val intent = Intent(this, SigInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onLogoutConfirmed: () -> Unit = {}
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    // --- FETCH DATA STATE ---
    var studentName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClientstudentprofile.getClient(context).getStudentMe()
            if (response.isSuccessful) {
                studentName = response.body()?.fullName ?: "Unknown User"
            } else {
                studentName = "Guest User"
            }
        } catch (e: Exception) {
            studentName = "Error Loading"
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth(),
                title = {
                    Text(
                        text = "Informations",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                navigationIcon = {
                    Image(
                        // Note: If you don't have R.drawable.back, use Icons.AutoMirrored.Filled.ArrowBack
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back Button",
                        modifier = Modifier
                            .height(30.dp)
                            .width(30.dp)
                            .padding(4.dp)
                            .clickable { onNavigateToHome() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 100.dp), // Position card below image area
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 80.dp, bottom = 16.dp), // Space for profile image overlap
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = studentName,
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Profile Options
                    ProfileOption(R.drawable.account, "Personal Information") {
                        context.startActivity(Intent(context, MyAccountActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileOption(R.drawable.trend, "Score Records") {
                        context.startActivity(Intent(context, ScoreRecordActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileOption(R.drawable.present, "Attendance Records") {
                        context.startActivity(Intent(context, AttRecordActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileOption(R.drawable.contact_school, "E-System Support") {
                        context.startActivity(Intent(context, ContactSchoolActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileOption(R.drawable.reset_password, "Change Password") {
                        context.startActivity(Intent(context, ChangePasswordActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileOption(R.drawable.help, "About the App") {
                        context.startActivity(Intent(context, AboutAppActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- LOGOUT OPTION (TRIGGER DIALOG) ---
                    ProfileOption(R.drawable.log_out, "Log Out", textColor = MaterialTheme.colorScheme.error) {
                        showLogoutDialog = true // <-- Show the dialog on click
                    }
                    // ------------------------------------

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Profile Image overlapping card
            Box(
                modifier = Modifier
                    .offset(y = (-50).dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    // Note: If you don't have R.drawable.vanda, replace it with a placeholder image
                    painter = painterResource(id = R.drawable.vanda),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }

    // --- LOGOUT DIALOG COMPOSABLE ---
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogoutConfirmed() // Execute the actual logout function
            }
        )
    }
    // --------------------------------
}

// --- LOGOUT DIALOG FUNCTION ---
@Composable
fun LogoutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Logout Account",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
//                IconButton(onClick = onDismiss) {
//                    // Using a standard close icon (X)
//                    Icon(
//                        painter = painterResource(id = R.drawable.absent), // Assuming R.drawable.close_icon exists
//                        contentDescription = "Close",
//                        tint = Color.Gray,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
            }
        },
        text = {
            Text("Are you sure you want to logout?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    "Logout",
                    color = MaterialTheme.colorScheme.error, // Red color for danger action
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
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
        ProfileScreen(onNavigateToHome = {})
    }
}