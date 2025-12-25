package com.example.e_system

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


// --- 1. Data Models (Matched to your JSON) ---
data class AttendanceResponse(val attendance: List<AttendanceRecord>)

data class AttendanceRecord(
    val _id: String,
    val student: String, // Your JSON currently shows this as a String ID
    val course: CourseInfo?,
    val date: String,
    val status: String,
    val recordedBy: RecordedBy?
)

data class CourseInfo(
    val _id: String,
    val title: String,
)
data class StudentProfileatt(
    val _id: String,
    val fullName: String,
)

data class RecordedBy(val fullName: String)

// --- 2. API Service ---
interface ApiServicestuatt {
    @GET("api/v1/student/attendance")
    suspend fun getAttendance(): Response<AttendanceResponse>

    @GET("api/v1/student/me")
    suspend fun getStudentProfileatt(): Response<StudentProfileatt>
}

// --- 3. Retrofit Client ---
object RetrofitClientstuatt {
    private const val BASE_URL = "http://10.0.2.2:4000/"
    private var retrofit: Retrofit? = null

    fun getClient(context: Context): ApiServicestuatt {
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
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }
        return retrofit!!.create(ApiServicestuatt::class.java)
    }
}

class AttendanceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ESystemTheme {
                AttendanceScreen()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen() {
    val context = LocalContext.current

    var fullAttendanceList by remember { mutableStateOf<List<AttendanceRecord>>(emptyList()) }
    var studentProfile by remember { mutableStateOf<StudentProfileatt?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var expanded by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf("Select Courses") }

    // ✅ Derived course titles from API data
    val courseOptions = remember(fullAttendanceList) {
        listOf("Select Courses") +
                fullAttendanceList
                    .mapNotNull { it.course?.title }
                    .distinct()
                    .sorted()
    }

    // ✅ Load API ONCE
    LaunchedEffect(key1 = Unit) {
        try {
            val api = RetrofitClientstuatt.getClient(context)

            // Use async/await or handle responses sequentially to avoid thread blocking
            val response = api.getAttendance()
            val userProfile = api.getStudentProfileatt()

            if (response.isSuccessful && userProfile.isSuccessful) {
                fullAttendanceList = response.body()?.attendance ?: emptyList()
                studentProfile = userProfile.body()
            }
        } catch (e: Exception) {
            // Log the error instead of crashing
            android.util.Log.e("AttendanceError", "Data fetch failed", e)
        } finally {
            isLoading = false
        }
    }

    // ✅ Ensure selected course is valid AFTER data loads
    LaunchedEffect(courseOptions) {
        if (!courseOptions.contains(selectedCourse)) {
            selectedCourse = "Select Courses"
        }
    }

    // Filter logic
    val filteredList = remember(selectedCourse, fullAttendanceList) {
        if (selectedCourse == "Select Courses") {
            fullAttendanceList
        } else {
            fullAttendanceList.filter {
                it.course?.title == selectedCourse
            }
        }
    }


    val presentCount = if (filteredList.isEmpty()) 0 else filteredList.count { it.status.lowercase() == "present" }
    val absentCount = if (filteredList.isEmpty()) 0 else filteredList.count { it.status.lowercase() == "absent" }
    val permissionCount = if (filteredList.isEmpty()) 0 else filteredList.count { it.status.lowercase() == "permission" }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Top Title Bar
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Attendance",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Responsive Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCourse,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                label = { Text("Filter by Course") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // FIX: Added 'course ->' to the lambda to identify the current item
                courseOptions.forEach { courseTitle ->
                    DropdownMenuItem(
                        text = {
                            Text(text = courseTitle)
                        },
                        onClick = {
                            selectedCourse = courseTitle
                            expanded = false
                        },
                        // Optional: Adds standard padding for Material 3 menus
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }



        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Attendance Summary",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Responsive Status Boxes Row
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusBox(
                label = "Present",
                color = Color(0xFF4CAF50),
                imageres = R.drawable.present,
                presentCount,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusBox(
                label = "Absent",
                color = Color(0xFF700000),
                imageres = R.drawable.absent,
                absentCount,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusBox(
                label = "Permission",
                color = Color(0xFF2D4B65),
                imageres = R.drawable.permission,
                permissionCount,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Scrollable List Box
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // 7. Scrollable List using LazyColumn for efficiency
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFECECEC))
                    .padding(10.dp)
            ) {
                items(filteredList) { record ->
                    // UI mapping
                    val (label, color) = when (record.status.lowercase()) {
                        "present" -> "P" to Color(0xFF4CAF50)
                        "absent" -> "A" to Color(0xFF700000)
                        else -> "L" to Color(0xFF2D4B65)
                    }

                    AttendanceItem(
                        date = formatDate(record.date),
                        name = studentProfile?.fullName ?: "Unknown Student",
                        status = label,
                        color = color,
                        courseTitle = record.course?.title ?: ""
                    )
                }
            }
        }
    }
}

fun formatDate(isoString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(isoString)
        SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(date!!)
    } catch (e: Exception) { isoString.split("T").first() }
}
@Composable
fun StatusBox(label: String, color: Color, imageres: Int, count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(130.dp)
            .background(color, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(id = imageres),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(8.dp)
            )

            Text(
                text = label,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$count",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AttendanceItem(date: String, name: String, status: String, color: Color, courseTitle: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)) {
        Text(text = "$date", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(start = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Box(modifier = Modifier
                .size(50.dp)
                .background(color, CircleShape), contentAlignment = Alignment.Center) {
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
