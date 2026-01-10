package com.example.e_system

import android.R.attr.delay
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.example.e_system.ui.theme.Base_Url
import com.example.e_system.ui.theme.ESystemTheme
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


// Matches your student JSON data
data class StudentMehomeProfile(
    val _id: String,
    val fullName: String,
    val nameKh: String,
    val email: String,
    val studentId: String,
    val studyShift: String,
    val avatar: String?
)
data class CourseResponse(
    val data: List<CourseItem>
)

data class CourseItem(
    val title: String,
    val credits: Int,
    val semester: String,
    val academicYear: String,
    val teacher: TeacherInfo
)

data class TeacherInfo(
    val fullName: String,
)

interface ApiServicestuhome {
    @GET("api/v1/student/me")
    suspend fun getStudentMe(): Response<StudentMehomeProfile>

    // Added: Fetch courses from your provided URL
    @GET("api/v1/courses")
    suspend fun getCourses(): Response<CourseResponse>
}
object RetrofitClientstuhome {
    fun getClient(context: Context): ApiServicestuhome {
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
            .baseUrl(Base_Url.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(ApiServicestuhome::class.java)
    }
}

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ESystemTheme {
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {

    val context = LocalContext.current
    var studentData by remember { mutableStateOf<StudentMehomeProfile?>(null) }
    var courseList by remember { mutableStateOf<List<CourseItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val avatarUrl = remember(studentData?.avatar) {
        val rawPath = studentData?.avatar
        if (!rawPath.isNullOrEmpty()) {
            val base = Base_Url.BASE_URL.trimEnd('/')
            val path = rawPath.trimStart('/')
            "$base/$path"
        } else {
            null
        }
    }
    LaunchedEffect(Unit) {
        try {
            val api = RetrofitClientstuhome.getClient(context)
            val studentResponse = api.getStudentMe()
            val courseResponse = api.getCourses()

            if (studentResponse.isSuccessful) {
                studentData = studentResponse.body()
            }
            if (courseResponse.isSuccessful) {
                courseList = courseResponse.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val displayCredit = courseList.firstOrNull()?.credits ?: 0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        // Profile Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Image(
                painter = rememberAsyncImagePainter(
                    model = avatarUrl ?: R.drawable.avatar, // Fallback to vanda if URL is null
                    placeholder = painterResource(id = R.drawable.avatar),
                    error = painterResource(id = R.drawable.avatar)
                ),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .clickable {
                            val intent = Intent(context, ProfileActivity::class.java)
                            context.startActivity(intent)
                        },
                    contentScale = ContentScale.Crop
                )


            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = if (isLoading) "Loading..." else "សួស្ដី · ${studentData?.fullName}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = if (isLoading) "Fetching ID..." else "ID: ${studentData?.studentId ?: "N/A"} | Dept: IT", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable{
                            val intent = Intent(context, SearchActivity::class.java)
                            context.startActivity(intent)
                        },
                )
                Image(
                    painter = painterResource(id = R.drawable.message),
                    contentDescription = "Message",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            val intent = Intent(context, AIChatActivity::class.java)
                            context.startActivity(intent)
                        },
                )
                Image(
                    painter = painterResource(id = R.drawable.notifications),
                    contentDescription = "Notifications",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            val intent = Intent(context, NotificationActivity::class.java)
                            context.startActivity(intent)
                        },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top Banner
        HotImageGallery()

        Spacer(modifier = Modifier.height(16.dp))

        val semester2Courses = courseList.filter {
            it.semester.contains("2") || it.semester.equals("Full Semester", ignoreCase = true)
        }
        DynamicCourseDropdown(
            year = "Year $displayCredit",
            semester = "Semester 2",
            apiCourses = semester2Courses,
            isLoading = isLoading,
            isInitiallyExpanded = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Semester 2 Dropdown ---
        // Filters for "Semester 2" or "Full Semester"
        val semester1Courses = courseList.filter {
            it.semester.contains("1") || it.semester.equals("Full Semester", ignoreCase = true)
        }
        DynamicCourseDropdown(
            year = "Year $displayCredit",
            semester = "Semester 1",
            apiCourses = semester1Courses,
            isLoading = isLoading,
            isInitiallyExpanded = false // Keep second one closed by default for better UI
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HotImageGallery() {
    val imageList = listOf(
        R.drawable.photorupp,
        R.drawable.rupphot,
        R.drawable.rupphot1
    )

    // 1. Pager State (Initializes the infinite count)
    // To make it feel infinite, we use a large number or just loop the index
    val pagerState = rememberPagerState(pageCount = { imageList.size })

    // 2. Auto-scroll logic: One by one with 2-second delay
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000) // Wait for 2 seconds
            val nextPage = (pagerState.currentPage + 1) % imageList.size
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
    }

    // 3. Hot Pulse Animation (Scale & subtle tilt)
    val infiniteTransition = rememberInfiniteTransition(label = "HotPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- The Hot Photo Slider ---
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(horizontal = 6.dp),
            pageSpacing = 16.dp
        ) { pageIndex ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Apply hot scale pulse
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
            ) {
                Image(
                    painter = painterResource(id = imageList[pageIndex]),
                    contentDescription = "Hot News Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- 4. Indicator Points (Blow Center) ---
        Row(
            modifier = Modifier.height(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(imageList.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.Red else Color.LightGray
                val width = if (pagerState.currentPage == iteration) 12.dp else 6.dp

                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(width = width, height = 6.dp)
                )
            }
        }
    }
}

@Composable
fun DynamicCourseDropdown(
    year: String,
    semester: String,
    apiCourses: List<CourseItem>,
    isLoading: Boolean,
    isInitiallyExpanded: Boolean = true
) {
    var expanded by remember { mutableStateOf(isInitiallyExpanded) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0F0F0))
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(year, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E4E68))
                Text(semester, fontSize = 14.sp, color = Color.Gray)
            }
            Icon(
                painter = painterResource(id = if (expanded) R.drawable.arrow_up else R.drawable.drop_down),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF2E4E68)
            )
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                color = Color(0xFF2E4E68)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            if (apiCourses.isEmpty() && !isLoading) {
                Text(
                    "No courses found for this semester.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                apiCourses.chunked(2).forEach { rowCourses ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowCourses.forEach { course ->
                            CourseCardFromApi(course, Modifier.weight(1f))
                        }
                        if (rowCourses.size < 2) Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun CourseCardFromApi(course: CourseItem, modifier: Modifier = Modifier) {
    // Map icons to your titles
    val iconRes = when {
        course.title.contains("Mobile", true) -> R.drawable.smartphone
        course.title.contains("MIS", true) -> R.drawable.mis
        course.title.contains("OOAD", true) -> R.drawable.ooad
        course.title.contains("Windows", true) -> R.drawable.window
        else -> R.drawable.se // Default icon
    }

    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = course.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = "Teacher. ${course.teacher.fullName}",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    ESystemTheme {
        HomeScreen()
    }
}
