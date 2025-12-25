package com.example.e_system

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.rotate
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
import kotlin.collections.forEach


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
    var selectedCourse by remember { mutableStateOf("Select Courses") }


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


    val courseOptions = remember(fullAttendanceList) {
        fullAttendanceList
            .mapNotNull { it.course?.title }
            .distinct()
            .sorted()
    }

// 2. Filter the list based on selection
    val filteredList = remember(selectedCourse, fullAttendanceList) {
        if (selectedCourse == "Select Courses") {
            emptyList() // Default state: show nothing
        } else {
            fullAttendanceList.filter { it.course?.title == selectedCourse }
        }
    }

// 3. Counts will now auto-update when selectedCourse changes
    val presentCount = filteredList.count { it.status.lowercase() == "present" }
    val absentCount = filteredList.count { it.status.lowercase() == "absent" }
    val permissionCount = filteredList.count { it.status.lowercase() == "permission"}

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
        CardDropdown(
            selectedText = selectedCourse,
            coursesItem = courseOptions,
            onCourseSelected = { selectedCourse = it }
        )

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
        }else if (filteredList.isEmpty()) {
            // Optional: Show a message when no course is selected
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Please select a course to view attendance", color = Color.Gray)
            }
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
fun CardDropdown(
    selectedText: String,
    coursesItem: List<String>,
    onCourseSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Box anchors the dropdown menu to the card
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedText,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    color = if (selectedText == "Select Courses") Color.Gray else Color.Black
                )

                // The Arrow icon that rotates up (180 deg) or down (0 deg)
                Icon(
                    painter = painterResource(id = R.drawable.drop_down),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (expanded) 180f else 0f), // Flip logic
                    tint = Color.DarkGray
                )
            }
            if (expanded) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        coursesItem.forEach { course ->
                            Box(
                                modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCourseSelected(course)
                                    expanded = false
                                }
                                .padding(12.dp)
                            ) {
                                Text(text = course, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

//@Composable
//fun CourseCardItem(
//    courseTitle: String,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 6.dp)
//    ) {
//        // Main Card
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(12.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White),
//            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    // Course Title Label
//                    Text(
//                        text = courseTitle,
//                        fontSize = 14.sp,
//                        color = Color(0xFF1976D2), // A nice blue for the course
//                        fontWeight = FontWeight.Medium
//                    )
//                }
//            }
//        }
//    }
//}


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
