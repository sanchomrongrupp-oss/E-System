package com.example.e_system

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

data class StudentMeProfile(
    val _id: String,
    val role: String,
    val fullName: String,
    val nameKh: String,
    val gender: String,
    val dateOfBirth: String,
    val placeOfBirth: String,
    val phone: String,
    val occupation: String,
    val address: String,
    val nationality: String,
    val email: String,
    val studentId: String,
    val studyShift: String
)

interface ApiServicestuprofile {
    @GET("api/v1/student/me")
    suspend fun getStudentMe(): Response<StudentMeProfile>
}
object RetrofitClientstuprofile {
    fun getClient(context: Context): ApiServicestuprofile {
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
            .create(ApiServicestuprofile::class.java)
    }
}
class MyAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                MyAccountScreen(onBackClick = { finish() })
            }
        }
    }
}

/**
 * Main Composable for the My Account Screen, displaying user details in a table format.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAccountScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    // --- STATE MANAGEMENT ---
    var studentProfile by remember { mutableStateOf<StudentMeProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // --- FETCH DATA ---
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClientstuprofile.getClient(context).getStudentMe()
            if (response.isSuccessful) {
                studentProfile = response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }
    Scaffold(
        topBar = {
            // Reusing the simple Top Bar structure from the image
            TopAppBar(
                title = {
                    Text(
                        text = "Personal Information",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.back) ,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White // Light Blue bar background
                ),
                modifier = Modifier.background(Color(0xFFE3F2FD)) // Optional: Add light blue to the background of the entire TopAppBar area
            )
        },
        containerColor = Color(0xFFF0F4F7) // Light gray/blue background for the screen
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Card Container for the detailed fields
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
            } else if (studentProfile != null) {
                // Map the profile data to a list for the table
                val profileDetails = listOf(
                    "ID: " to studentProfile!!.studentId,
                    "Full Name: " to studentProfile!!.fullName,
                    "Name (KH): " to studentProfile!!.nameKh,
                    "Occupation: " to studentProfile!!.occupation,
                    "Gender: " to studentProfile!!.gender,
                    "Date of Birth: " to studentProfile!!.dateOfBirth.split("T")[0], // Simple date format
                    "Phone: " to studentProfile!!.phone,
                    "Email: " to studentProfile!!.email,
                    "Shift: " to studentProfile!!.studyShift,
                    "Place of Birth: " to studentProfile!!.placeOfBirth,
                    "Nationality: " to studentProfile!!.nationality,
                    "Address: " to studentProfile!!.address
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        profileDetails.forEachIndexed { index, detail ->
                            AccountDetailRow(
                                label = detail.first,
                                value = detail.second,
                                isLastItem = index == profileDetails.size - 1
                            )
                        }
                    }
                }
            } else {
                Text("Failed to load profile data.")
            }
        }
    }
}

/**
 * Single Row for displaying a label and its corresponding value.
 */
@Composable
fun AccountDetailRow(
    label: String,
    value: String,
    isLastItem: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Label (e.g., Full Name)
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.DarkGray
            )
        }

        // Horizontal Divider as shown in the image, except for the last item
        if (!isLastItem) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )
        }
    }
}

// --- Preview ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyAccountScreenPreview() {
    ESystemTheme {
        MyAccountScreen(onBackClick = {})
    }
}