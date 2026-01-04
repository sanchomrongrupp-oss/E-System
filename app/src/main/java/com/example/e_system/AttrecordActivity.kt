package com.example.e_system

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.Base_Url
import com.example.e_system.ui.theme.ESystemTheme
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// --- 1. Data Models (Strictly Named) ---

data class AttendanceUIModel(
    val courseName: String,
    val presentCount: Int,
    val absentCount: Double,
    val percentage: String
)

data class AttendanceResponseattrecord(val attendance: List<AttendanceRecordattrecord>)

data class AttendanceRecordattrecord(
    val _id: String,
    val course: CourseInfoattrecord?,
    val status: String,
    val date: String
)

data class CourseInfoattrecord(val _id: String, val title: String)

// --- 2. API Service ---
interface AttendanceApiServiceattrecord {
    @GET("api/v1/student/attendance")
    suspend fun getAttendance(): Response<AttendanceResponseattrecord>
}

// --- 3. Retrofit Client ---
object RetrofitClientattrecord {
    private var retrofit: Retrofit? = null

    fun getApiServiceattrecord(context: Context): AttendanceApiServiceattrecord {
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
        return retrofit!!.create(AttendanceApiServiceattrecord::class.java)
    }
}

// --- 4. Logic to Process Data (Fixed Names) ---
fun processAttendance(records: List<AttendanceRecordattrecord>): List<AttendanceUIModel> {
    // 1. Group by the course title (handling nulls)
    val grouped = records.groupBy { it.course?.title ?: "Unknown Course" }

    return grouped.map { (courseName, courseRecords) ->
        // 2. Count occurrences of each status
        val present = courseRecords.count { it.status.equals("Present", ignoreCase = true) }
        val permission = courseRecords.count { it.status.equals("Permission", ignoreCase = true) }
        val absentOnly = courseRecords.count { it.status.equals("Absent", ignoreCase = true) }

        // 3. Calculation: 1 Permission = 0.5 Absent
        val totalCalculatedAbsents = absentOnly + (permission * 0.5)

        // 4. Calculate Percentage: (Present / Total Classes) * 100
        val totalClasses = courseRecords.size
        val percent = if (totalClasses > 0) {
            ((present.toDouble() / totalClasses) * 100).toInt()
        } else 0

        AttendanceUIModel(
            courseName = courseName,
            presentCount = present,
            absentCount = totalCalculatedAbsents,
            percentage = "$percent%"
        )
    }
}

// ================= ACTIVITY =================

class AttRecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ESystemTheme {
                val context = LocalContext.current
                var attendanceList by remember { mutableStateOf<List<AttendanceUIModel>>(emptyList()) }
                var selectedCourse by remember { mutableStateOf<AttendanceUIModel?>(null) }
                var isLoading by remember { mutableStateOf(true) }
                var error by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    try {
                        val api = RetrofitClientattrecord.getApiServiceattrecord(context)
                        val response = api.getAttendance()

                        if (response.isSuccessful) {
                            val rawData = response.body()?.attendance ?: emptyList()
                            // Correctly passing the raw list to the processing function
                            val processed = processAttendance(rawData)
                            attendanceList = processed
                            selectedCourse = processed.firstOrNull()
                        } else {
                            error = "Server Error: ${response.code()}"
                        }
                    } catch (e: Exception) {
                        error = e.localizedMessage ?: "Network error"
                    } finally {
                        isLoading = false
                    }
                }

                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when {
                        isLoading -> LoadingBox()
                        error != null -> ErrorBox(error!!)
                        selectedCourse != null ->
                            AttendanceReportScreen(
                                reportData = selectedCourse!!,
                                allCourses = attendanceList,
                                onCourseSelected = { selectedCourse = it },
                                onBackClicked = { finish() }
                            )
                        else -> EmptyBox()
                    }
                }
            }
        }
    }
}

// ================= UI COMPONENTS =================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceReportScreen(
    reportData: AttendanceUIModel,
    allCourses: List<AttendanceUIModel>,
    onCourseSelected: (AttendanceUIModel) -> Unit,
    onBackClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Record", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(painter = painterResource(id = R.drawable.back),
                            modifier = Modifier
                                .size(24.dp)
                            ,
                            contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Course Details", color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            CourseDropdownCard(
                currentCourse = reportData.courseName,
                courses = allCourses,
                onCourseSelected = onCourseSelected
            )

            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(24.dp)) {
                    ReportDataRow("Present", reportData.presentCount.toString())
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    // Format absent count to remove .0 if it's a whole number
                    val absDisplay = if (reportData.absentCount % 1 == 0.0)
                        reportData.absentCount.toInt().toString()
                    else reportData.absentCount.toString()

                    ReportDataRow("Absents", absDisplay)
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    val percentValue = reportData.percentage.replace("%", "").toIntOrNull() ?: 0
                    val statusColor = if (percentValue < 75) Color(0xFFD32F2F) else Color(0xFF388E3C)

                    ReportDataRow("Percentage", reportData.percentage, statusColor)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text("*1 Permission = 0.5 Absent", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CourseDropdownCard(
    currentCourse: String,
    courses: List<AttendanceUIModel>,
    onCourseSelected: (AttendanceUIModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            border = BorderStroke(1.dp, Color.LightGray),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(currentCourse, Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Icon(painterResource(id = R.drawable.drop_down), contentDescription = null)
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course.courseName) },
                    onClick = {
                        onCourseSelected(course)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ReportDataRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        Text(label, color = Color.DarkGray)
        Text(value, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable fun LoadingBox() = Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
@Composable fun ErrorBox(msg: String) = Box(Modifier.fillMaxSize(), Alignment.Center) { Text(msg, color = Color.Red) }
@Composable fun EmptyBox() = Box(Modifier.fillMaxSize(), Alignment.Center) { Text("No record found") }