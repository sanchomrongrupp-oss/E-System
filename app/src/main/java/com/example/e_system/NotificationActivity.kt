package com.example.e_system

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.e_system.ui.theme.Base_Url
import com.example.e_system.ui.theme.ESystemTheme
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// --- Data Model for a Notification ---
// Matches the "announcements": [...] array in your JSON
data class AnnouncementResponse(
    val announcements: List<Announcement>
)

data class Announcement(
    val _id: String,
    val title: String,
    val content: String,
    val type: String,
    val createdAt: String
)

data class StudentMenotificationResponse(val id: String)

interface ApiServicestunotification {
    @GET("api/v1/student/me")
    suspend fun getStudentMe(): Response<StudentMenotificationResponse>

    // ADD THIS LINE
    @GET("api/v1/student/announcements")
    suspend fun getAnnouncements(): Response<AnnouncementResponse>
}

enum class AnnouncementVisualType(val color: Color) {
    COURSE(Color(0xFF64B5F6)),    // Blue
    EMERGENCY(Color(0xFFE57373)), // Red
    ACADEMIC(Color(0xFF81C784)),  // Green
    GENERAL(Color(0xFFBDBDBD));   // Gray

    companion object {
        fun fromString(type: String): AnnouncementVisualType {
            return entries.find { it.name.equals(type, ignoreCase = true) } ?: GENERAL
        }
    }
}

// --- Activity Definition ---
class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                NotificationScreen(onBackClick = { finish() })
            }
        }
    }
}

object RetrofitClientstunotification {
    // We store the instance so we don't recreate it every time
    private var retrofit: Retrofit? = null

    fun getClient(context: Context): ApiServicestunotification {
        if (retrofit == null) {
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

            retrofit = Retrofit.Builder()
                .baseUrl(Base_Url.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }

        // FIX: Ensure this class matches the function return type
        return retrofit!!.create(ApiServicestunotification::class.java)
    }
}

// --- Main Screen Composable ---
@Composable
fun NotificationScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClientstunotification.getClient(context).getAnnouncements()
            if (response.isSuccessful && response.body() != null) {
                announcements = response.body()!!.announcements
            } else {
                errorMessage = "Server Error: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Network Error: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }
    Scaffold(
        topBar = { NotificationToolbar(onBackClick = onBackClick) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(errorMessage!!, modifier = Modifier.align(Alignment.Center), color = Color.Red)
            } else {
                NotificationList(announcements = announcements)
            }
        }
    }
}

// --- Toolbar Component (Good UX: Clear title and back button) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationToolbar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Notifications",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

// --- Notification List (Good UX: Grouped and Scrollable) ---
@Composable
fun NotificationList(announcements: List<Announcement>) {
    LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        items(announcements) { announcement ->
            NotificationItem(announcement = announcement)
        }
    }
}

// --- Individual Notification Item (Good UX: Visual cues for unread/type) ---
@Composable
fun NotificationItem(announcement: Announcement) {
    val visualType = AnnouncementVisualType.fromString(announcement.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Type Indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .padding(top = 4.dp)
                    .clip(CircleShape)
                    .background(visualType.color)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = announcement.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D4B65)
                )
                Text(
                    text = announcement.content,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = announcement.type.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = visualType.color
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // Simple Date extraction from ISO string
                    val dateOnly = announcement.createdAt.split("T").firstOrNull() ?: ""
                    Text(
                        text = dateOnly,
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

// --- Preview Component ---
@Preview(showBackground = true)
@Composable
fun PreviewNotificationScreen() {
    ESystemTheme {
        NotificationScreen(onBackClick = {})
    }
}