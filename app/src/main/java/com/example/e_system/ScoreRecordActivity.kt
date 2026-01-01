package com.example.e_system

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_system.ui.theme.Base_Url
import com.example.e_system.ui.theme.ESystemTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// --- 1. Data Models ---
data class ScoreResponse(val data: List<ScoreDataRecord>)

data class ScoreDataRecord(
    val _id: String,
    val course: CourseInformation,
    val semesters: List<BackendSemester>
)

data class CourseInformation(val title: String)

data class BackendSemester(
    val name: String,
    val grade: String,
    val assignment: Double,
    val attendance: Double,
    val midterm: Double,
    val final: Double,
    val total: Double
)

data class ScoreItem(
    val title: String,
    val weight: String,
    val score: Double,
    val isTotal: Boolean = false,
    val gradeValue: String? = null
)

data class SemesterScore(val semester: String, val scoreList: List<ScoreItem>)

// --- 2. API & Retrofit ---
interface ApiServicescore {
    @GET("api/v1/student/scorerecords")
    suspend fun getScoreRecords(): ScoreResponse
}

object RetrofitClientscore {
    private var instance: ApiServicescore? = null
    fun getApiService(context: Context): ApiServicescore {
        return instance ?: synchronized(this) {
            val token = TokenManager(context).getToken() ?: ""
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(request)
                }
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(Base_Url.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(ApiServicescore::class.java).also { instance = it }
        }
    }
}

// --- 3. ViewModel ---
class ScoreViewModel : ViewModel() {
    private val _records = MutableStateFlow<List<ScoreDataRecord>>(emptyList())
    val records: StateFlow<List<ScoreDataRecord>> = _records
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchScores(context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = RetrofitClientscore.getApiService(context).getScoreRecords()
                _records.value = response.data
            } catch (e: Exception) {
                Log.e("API_ERROR", e.toString())
                _errorMessage.value = "Failed to load scores."
            } finally {
                _isLoading.value = false
            }
        }
    }
}

// --- 4. Main Activity ---
class ScoreRecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                val context = LocalContext.current
                val viewModel: ScoreViewModel = viewModel()
                LaunchedEffect(Unit) { viewModel.fetchScores(context) }
                ScoreRecordNavigation(viewModel, onActivityFinish = { finish() })
            }
        }
    }
}

// --- 5. Navigation & Helper ---
@Composable
fun ScoreRecordNavigation(viewModel: ScoreViewModel, onActivityFinish: () -> Unit) {
    val records by viewModel.records.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    var selectedRecord by remember { mutableStateOf<ScoreDataRecord?>(null) }
    var showFullTranscript by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedRecord != null || showFullTranscript) {
        selectedRecord = null
        showFullTranscript = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1B5E20)) }
    } else if (error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(error ?: "Error", color = Color.Red) }
    } else {
        Crossfade(targetState = Triple(selectedRecord, showFullTranscript, records), label = "") { (record, showTranscript, allRecords) ->
            when {
                showTranscript -> {
                    val (finalAverage, finalGrade) = calculateFinalGrade(allRecords)
                    ScoreDetailScreen(
                        subjectName = "Academic Transcript",
                        allSemesterData = generateTranscriptData(allRecords) + listOf(
                            SemesterScore("Final Total", listOf(
                                ScoreItem("Final Average", "", finalAverage, isTotal = true),
                                ScoreItem("Final Grade", "", 0.0, isTotal = true, gradeValue = finalGrade)
                            ))
                        ),
                        onBackClick = { showFullTranscript = false }
                    )
                }
                record != null -> {
                    val uiSemesters = record.semesters.map { sem ->
                        SemesterScore(sem.name, listOf(
                            ScoreItem("Attendance", "10%", sem.attendance),
                            ScoreItem("Assignment", "10%", sem.assignment),
                            ScoreItem("Midterm", "20%", sem.midterm),
                            ScoreItem("Final", "60%", sem.final),
                            ScoreItem("Total Score", "100%", sem.total, isTotal = true),
                            ScoreItem("Grade", "", 0.0, gradeValue = sem.grade)
                        ))
                    }
                    ScoreDetailScreen(record.course.title, uiSemesters, { selectedRecord = null })
                }
                else -> {
                    SubjectListScreen(
                        records = allRecords,
                        onSubjectClick = { selectedRecord = it },
                        onTranscriptClick = { showFullTranscript = true },
                        onBackClick = onActivityFinish
                    )
                }
            }
        }
    }
}

// --- 6. Generate Transcript & Totals ---
fun generateTranscriptData(records: List<ScoreDataRecord>): List<SemesterScore> {
    // 1. Get all unique semester names from the data dynamically
    val semesterNames = records.flatMap { it.semesters.map { s -> s.name } }.distinct()

    return semesterNames.map { semName ->
        // 2. Extract totals for this specific semester to calculate stats
        val semesterTotals = records.mapNotNull { rec ->
            rec.semesters.find { it.name == semName }?.total
        }

        val totalSum = semesterTotals.sum()
        val average = if (semesterTotals.isNotEmpty()) totalSum / semesterTotals.size else 0.0

        // 3. Determine the Letter Grade based on the average
        val semesterLetterGrade = when {
            average >= 90 -> "A"
            average >= 80 -> "B"
            average >= 70 -> "C"
            average >= 60 -> "D"
            average >= 50 -> "E"
            else -> "F"
        }

        // 4. Map each course to a ScoreItem
        val courseItems = records.map { rec ->
            val sem = rec.semesters.find { it.name == semName }
            ScoreItem(
                title = rec.course.title,
                weight = "100%    ",
                score = sem?.total ?: 0.0,
                isTotal = true
            )
        }

        // 5. Add the Summary Rows (Total, Average, and Grade)
        val summaryItems = listOf(
            ScoreItem("Semester Total", "", totalSum, isTotal = true),
            ScoreItem("Semester Average", "", average, isTotal = true),
            ScoreItem("Semester Grade", "", 0.0, isTotal = true, gradeValue = semesterLetterGrade)
        )

        SemesterScore(semName, courseItems + summaryItems)
    }
}

// --- 7. Calculate Final Total & Grade ---
fun calculateFinalGrade(records: List<ScoreDataRecord>): Pair<Double, String> {
    val allTotals = records.flatMap { it.semesters }.map { it.total }
    val totalSum = allTotals.sum()
    val average = if (allTotals.isNotEmpty()) totalSum / allTotals.size else 0.0
    val grades = when {
        average >= 90 -> "A"
        average >= 80 -> "B"
        average >= 70 -> "C"
        average >= 60 -> "D"
        else -> "F"
    }
    return Pair(average, grades)
}

// --- 8. Screens ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListScreen(records: List<ScoreDataRecord>, onSubjectClick: (ScoreDataRecord) -> Unit, onTranscriptClick: () -> Unit, onBackClick: () -> Unit) {
    Scaffold(topBar = { SimpleToolbar("Score Record", onBackClick) }, containerColor = Color(0xFFF7F7F7)) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Subjects", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                LazyColumn {
                    items(records) { record ->
                        SubjectRow(record.course.title) { onSubjectClick(record) }
                        Divider(Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                    }
                    item { SubjectRow("Academic Transcript", isSpecial = true) { onTranscriptClick() } }
                }
            }
        }
    }
}

@Composable
fun SubjectRow(title: String, isSpecial: Boolean = false, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).background(if (isSpecial) Color(0xFF1B5E20) else Color(0xFFE8F5E9), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Icon(painterResource(if (isSpecial) R.drawable.check else R.drawable.next), null, tint = if (isSpecial) Color.White else Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Icon(painterResource(R.drawable.next), null, tint = Color.LightGray)
    }
}

@Composable
fun ScoreDetailScreen(subjectName: String, allSemesterData: List<SemesterScore>, onBackClick: () -> Unit) {
    var selectedSemesterName by remember(allSemesterData) { mutableStateOf(allSemesterData.firstOrNull()?.semester ?: "") }
    val currentScores = remember(selectedSemesterName, allSemesterData) { allSemesterData.find { it.semester == selectedSemesterName }?.scoreList ?: emptyList() }

    Scaffold(topBar = { SimpleToolbar(subjectName, onBackClick) }, containerColor = Color(0xFFF7F7F7)) { padding ->
        Box(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Column {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Score Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    SemesterDropdownStyled(selected = selectedSemesterName, options = allSemesterData.map { it.semester }, onSelected = { selectedSemesterName = it })
                }
                Spacer(Modifier.height(16.dp))
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        currentScores.forEach { item -> ScoreItemRow(item) }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreItemRow(item: ScoreItem) {
    val isHeader = item.isTotal || item.title == "Grade"
    val color = if (isHeader) Color(0xFF1B5E20) else Color.Black
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.check), null, tint = if (isHeader) Color(0xFF1B5E20) else Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(12.dp))
            Text(item.title, fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal, color = color)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (item.weight.isNotEmpty()) Text(item.weight, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(end = 12.dp))
            val valueColor = when {
                item.title == "Grade" -> Color(0xFF2196F3)
                item.isTotal -> Color(0xFF4CAF50)
                else -> Color.Black
            }
            Text(text = item.gradeValue ?: item.score.toString(), fontWeight = FontWeight.Bold, color = valueColor, fontSize = 16.sp)
        }
    }
}

@Composable
fun SemesterDropdownStyled(selected: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(contentAlignment = Alignment.TopEnd) {
        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.width(160.dp).clickable { expanded = !expanded }) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(selected, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Icon(painterResource(R.drawable.drop_down), null, tint = Color(0xFF1B5E20), modifier = Modifier.size(20.dp).rotate(if (expanded) 180f else 0f))
            }
        }
        if (expanded) {
            Card(modifier = Modifier.padding(top = 45.dp).width(160.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(8.dp)) {
                Column {
                    options.forEach { option ->
                        Box(Modifier.fillMaxWidth().clickable { onSelected(option); expanded = false }.padding(12.dp)) { Text(option, fontSize = 14.sp) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleToolbar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
        navigationIcon = { IconButton(onClick = onBackClick) { Icon(painterResource(R.drawable.back), null, modifier = Modifier.size(24.dp)) } },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}
