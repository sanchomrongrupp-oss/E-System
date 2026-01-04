package com.example.e_system

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_system.ui.theme.Base_Url
import com.example.e_system.ui.theme.ESystemTheme
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File
import java.io.FileOutputStream

// ================= API MODELS =================
data class ExerciseListResponse(val data: List<ExerciseItem>)
data class ExerciseItem(val _id: String, val title: String, val description: String, val dueDate: String, val maxScore: Int, val course: CourseExercise)
data class CourseExercise(val title: String, val fullName: String)
data class SubmitExerciseResponse(val status: String, val message: String?)
data class StuExercise(val _id: String,)

// ================= API SERVICE =================
interface ApiServiceExercise {
    @GET("api/v1/exercises")
    suspend fun getExercises(): Response<ExerciseListResponse>

    @Multipart
    @POST("api/v1/exercises/{id}/submit")
    suspend fun submitExercise(
        @Path("id") exerciseId: String,
        @Part("studentId") studentId: okhttp3.RequestBody,
        @Part("submittedText") submittedText: okhttp3.RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<SubmitExerciseResponse>
}

object RetrofitClientExercise {
    fun getApi(context: Context): ApiServiceExercise {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val token = TokenManager(context).getToken()
            val request = chain.request().newBuilder()
                .apply { if (!token.isNullOrEmpty()) addHeader("Authorization", "Bearer $token") }
                .build()
            chain.proceed(request)
        }.build()

        return Retrofit.Builder()
            .baseUrl(Base_Url.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServiceExercise::class.java)
    }
}

// ================= VIEWMODEL =================
class ExerciseViewModel(private val apiService: ApiServiceExercise) : ViewModel() {
    var exercise by mutableStateOf<ExerciseItem?>(null)
    var submitMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false) // Track success status

    init { fetchExercises() }

    private fun fetchExercises() {
        viewModelScope.launch {
            isLoading = true
            try {
                val res = apiService.getExercises()
                if (res.isSuccessful) exercise = res.body()?.data?.firstOrNull()
            } catch (e: Exception) { Log.e("API_ERR", e.toString()) }
            finally { isLoading = false }
        }
    }

    fun submitExercise(context: Context, answer: String, fileUri: Uri?, onComplete: () -> Unit) {
        val currentEx = exercise ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                val studentIdPart = "".toRequestBody("text/plain".toMediaTypeOrNull())
                val textPart = answer.toRequestBody("text/plain".toMediaTypeOrNull())

                var filePart: MultipartBody.Part? = null
                fileUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                }

                val response = apiService.submitExercise(currentEx._id, studentIdPart, textPart, filePart)
                if (response.isSuccessful) {
                    submitMessage = "Submitted successfully ✅"
                    isSuccess = true
                    onComplete() // Clear UI fields on success
                } else {
                    submitMessage = "Failed ❌"
                    isSuccess = false
                }
            } catch (e: Exception) {
                submitMessage = "Error: ${e.localizedMessage}"
                isSuccess = false
            } finally { isLoading = false }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_file.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        return file
    }

    class Factory(private val api: ApiServiceExercise) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ExerciseViewModel(api) as T
    }
}

// ================= UI =================
@Composable
fun ExerciseScreen() {
    val context = LocalContext.current
    val api = remember { RetrofitClientExercise.getApi(context) }
    val viewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModel.Factory(api))

    var answer by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Column(
        Modifier.fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()))
    {
        CustomExerciseTopBar()
        Spacer(Modifier.height(20.dp))

        if (viewModel.isLoading && viewModel.exercise == null) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        viewModel.exercise?.let { ex ->
            ExerciseDetailCard(
                exercise = ex,
                answer = answer,
                onAnswerChange = { if (!viewModel.isSuccess) answer = it }, // Prevent edit after success if desired, or remove condition to allow it
                onAttachClick = { if (!viewModel.isLoading) launcher.launch("image/*") },
                selectedUri = selectedImageUri,
                submitMessage = viewModel.submitMessage,
                isLoading = viewModel.isLoading,
                onSubmit = {
                    viewModel.submitExercise(context, answer, selectedImageUri) {
                        // This block runs only on success
                        answer = ""
                        selectedImageUri = null
                    }
                }
            )
        }
    }
}

@Composable
fun ExerciseDetailCard(
    exercise: ExerciseItem,
    answer: String,
    onAnswerChange: (String) -> Unit,
    onAttachClick: () -> Unit,
    selectedUri: Uri?,
    submitMessage: String,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(exercise.course.title, fontWeight = FontWeight.Bold, color = Color(0xFF00468C), fontSize = 18.sp)
            Text(exercise.description, fontSize = 14.sp, color = Color.DarkGray)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onAttachClick, enabled = !isLoading) {
                    Icon(
                        painter = painterResource(R.drawable.attach),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "Attach File",
                        tint = if (selectedUri != null) Color(0xFF00468C) else Color.Gray
                    )
                }
                OutlinedTextField(
                    value = answer,
                    onValueChange = onAnswerChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Your answer...") },
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading // Disable while sending to prevent input lag/errors
                )
            }

            if (selectedUri != null) {
                Text(
                    "📎 File attached",
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(start = 48.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onSubmit,
                Modifier.fillMaxWidth(),
                enabled = !isLoading && answer.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00468C))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Submit Exercise")
                }
            }

            if (submitMessage.isNotEmpty()) {
                Text(
                    submitMessage,
                    Modifier.padding(top = 12.dp).align(Alignment.CenterHorizontally),
                    color = if (submitMessage.contains("✅")) Color(0xFF2E7D32) else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CustomExerciseTopBar() {
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
                text = "Exercise",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

class ExerciseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ESystemTheme { Surface { ExerciseScreen() } } }
    }
}

@Preview(showBackground = true)
@Composable
fun ExercisePreview() {
    ESystemTheme {
        ExerciseDetailCard(
            exercise = ExerciseItem("1", "Title", "Write a summary about AI.", "2026", 100, CourseExercise("AI", "Intro")),
            answer = "Sample text",
            onAnswerChange = {},
            onAttachClick = {},
            selectedUri = null,
            submitMessage = "Submitted successfully ✅",
            isLoading = false,
            onSubmit = {}
        )
    }
}