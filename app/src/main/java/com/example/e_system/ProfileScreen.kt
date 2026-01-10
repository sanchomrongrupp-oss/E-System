package com.example.e_system

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import androidx.core.content.FileProvider
import com.example.e_system.ui.theme.Base_Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

data class StudentdentProfile(
    val fullName: String,
    val avatar: String?
)

interface ApiServicestudentprofile {
    @GET("api/v1/student/me")
    suspend fun getStudentMe(): Response<StudentdentProfile>

    @Multipart
    @POST("api/v1/users/profile/me/upload-picture")
    suspend fun uploadProfilePicture(
        @Part profile: MultipartBody.Part
    ): Response<Unit> // You can create a data class for the response if needed
}
object RetrofitClientstudentprofile {
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
            .baseUrl(Base_Url.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(ApiServicestudentprofile::class.java)
    }
}

fun uploadImageToServer(context: Context, uri: Uri, onComplete: (Boolean) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val contentResolver = context.contentResolver
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")

            contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("profile", file.name, requestFile)

            val response = RetrofitClientstudentprofile.getClient(context).uploadProfilePicture(body)

            withContext(Dispatchers.Main) {
                onComplete(response.isSuccessful)
                // Delete temp file after upload
                if (file.exists()) file.delete()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onComplete(false)
            }
        }
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
    var studentName by remember { mutableStateOf("Loading...") }
    var avatarUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClientstudentprofile.getClient(context).getStudentMe()
            if (response.isSuccessful) {
                val profile = response.body()
                studentName = profile?.fullName ?: "Unknown User"

                val rawAvatar = profile?.avatar // "/uploads/profiles/IMG_..."

                avatarUrl = if (!rawAvatar.isNullOrEmpty()) {
                    val base = Base_Url.BASE_URL.trimEnd('/')
                    val path = rawAvatar.trimStart('/')
                    "$base/$path" // This guarantees exactly one slash
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            studentName = "Error Loading"
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Information", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Use a Box to allow overlapping the Image on top of the Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. The Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 60.dp, bottom = 20.dp), // Pushed down to make room for image
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 70.dp, bottom = 24.dp), // Space inside for the overlapped image
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = studentName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Menu Options
                    ProfileOption(R.drawable.account, "Personal Information") {
                        context.startActivity(Intent(context, MyAccountActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfileOption(R.drawable.trend, "Score Records") {
                        context.startActivity(Intent(context, ScoreRecordActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfileOption(R.drawable.present, "Attendance Records") {
                        context.startActivity(Intent(context, AttRecordActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfileOption(R.drawable.contact_school, "E-System Support") {
                        context.startActivity(Intent(context, ContactSchoolActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfileOption(R.drawable.reset_password, "Change Password") {
                        context.startActivity(Intent(context, ChangePasswordActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfileOption(R.drawable.help, "About the App") {
                        context.startActivity(Intent(context, AboutAppActivity::class.java))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    ProfileOption(
                        R.drawable.log_out,
                        "Log Out",
                        textColor = MaterialTheme.colorScheme.error
                    ) {
                        showLogoutDialog = true
                    }
                }
            }

            // 2. The Overlapping Profile Image
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 0.dp) // Aligns with the Card's top padding
            ) {
                ProfileImageWithCamera(initialImageUrl = avatarUrl)
            }
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogoutConfirmed()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileImageWithCamera(initialImageUrl: String?) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var showOptions by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }//is Loading

    val displayImage: Any = when {
        localImageUri != null -> localImageUri!!
        !initialImageUrl.isNullOrEmpty() -> initialImageUrl
        else -> R.drawable.avatar
    }

    // 1. Prepare Camera URI
    val photoUri = remember {
        val file = File(context.cacheDir, "temp_profile_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    // Handle Upload Trigger
    val handleUpload: (Uri) -> Unit = { uri ->
        isUploading = true
        uploadImageToServer(context, uri) { success ->
            isUploading = false
            if (success) {
                localImageUri = uri
                Toast.makeText(context, "Upload Successful!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Upload Failed Check your connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. Launchers
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            showOptions = false
            handleUpload(photoUri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            showOptions = false
            handleUpload(uri)
        }
    }

    // 3. Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(photoUri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = rememberAsyncImagePainter(model = displayImage),
            contentDescription = "Profile",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape) // Adds a white ring around the image
        )
        if (isUploading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black
            )
        }

        // Camera Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp,Color.Black,CircleShape)
                .clickable { showOptions = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.camera),
                contentDescription = "Camera",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
    if (showOptions) {
        ModalBottomSheet(
            onDismissRequest = { showOptions = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Edit Profile Picture", style = MaterialTheme.typography.titleLarge)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Camera Option
                AttachmentOptionRow(
                    icon = painterResource(id = R.drawable.camera),
                    label = "Camera"
                ) {
                    // CHECK PERMISSION BEFORE LAUNCHING
                    val permissionCheckResult = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(photoUri)
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                }

                // Gallery Option
                AttachmentOptionRow(
                    icon = painterResource(id = R.drawable.photolibrary),
                    label = "Photo Gallery"
                ) {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }
    }
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