package com.example.e_system

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_system.ui.theme.Base_Url
import com.example.e_system.ui.theme.ESystemTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT

// --- 1. Data Models ---
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class ChangePasswordResponse(
    val message: String? = null,
    val status: String? = null
)

data class UserProfile(
    val email: String
)

// --- 2. API Interface ---
interface ChangePasswordApiService {
    @GET("api/v1/student/me")
    suspend fun getProfile(): Response<UserProfile>

    // Matches your router.put('/profile/me/change-password')
    @PUT("api/v1/users/profile/me/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<ChangePasswordResponse>
}
object RetrofitClientscorechange_pass {
    private var retrofit: Retrofit? = null

    // Ensure this returns 'Retrofit', NOT a specific API service
    fun getApiService(context: Context): Retrofit {
        if (retrofit == null) {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val token = TokenManager(context).getToken()
                    val request = chain.request().newBuilder()
                    if (!token.isNullOrEmpty()) {
                        request.addHeader("Authorization", "Bearer $token")
                    }
                    chain.proceed(request.build())
                }
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(Base_Url.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }
        return retrofit!!
    }
}
// --- 3. ViewModel ---
class ChangePasswordViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var userEmail by mutableStateOf("Loading...")
    var errorMessage by mutableStateOf<String?>(null)

    // 1. Fetch the user profile to display the email
    fun fetchProfile(context: Context) {
        viewModelScope.launch {
            try {
                // Ensure this uses the correct Retrofit client instance for your app
                val api = RetrofitClientscorechange_pass.getApiService(context).create(ChangePasswordApiService::class.java)
                val response = api.getProfile()

                if (response.isSuccessful) {
                    userEmail = response.body()?.email ?: "No email found"
                } else {
                    errorMessage = "Session expired. Please log in again."
                }
            } catch (e: Exception) {
                errorMessage = "Network error: Could not load profile."
            }
        }
    }

    // 2. Perform the Password Change (PUT Request)
    fun performPasswordChange(context: Context, currentPass: String, newPass: String, onFinished: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Get the Retrofit instance
                val retrofitInstance = RetrofitClientscorechange_pass.getApiService(context)

                // 2. Create the Service
                val api = retrofitInstance.create(ChangePasswordApiService::class.java)

                // 3. Make the call
                val response = api.changePassword(ChangePasswordRequest(currentPass, newPass))

                if (response.isSuccessful) {
                    onFinished()
                } else {
                    errorMessage = "Incorrect current password"
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}

// --- 4. Activity ---
class ChangePasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: ChangePasswordViewModel = viewModel()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                vm.fetchProfile(context)
            }

            ESystemTheme {
                ChangePasswordScreen(
                    currentEmail = vm.userEmail,
                    viewModel = vm,
                    onBackClicked = { finish() }
                )
            }
        }
    }
}

// --- 5. Main Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    currentEmail: String,
    viewModel: ChangePasswordViewModel,
    onBackClicked: () -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showSuccessAlert by remember { mutableStateOf(false) } // Alert State
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    if (showSuccessAlert) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(onClick = {
                    showSuccessAlert = false
                    onBackClicked()
                }) { Text("OK") }
            },
            title = { Text("Success", fontWeight = FontWeight.Bold) },
            text = { Text("Your password has been updated successfully.") },
            shape = RoundedCornerShape(12.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(painterResource(R.drawable.back), "Back", modifier = Modifier.size(24.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState), // Added scroll support
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PasswordHeader()
            Spacer(modifier = Modifier.height(24.dp))

            InputField(
                value = currentEmail,
                label = "Account Email",
                leadingIcon = { Icon(painterResource(R.drawable.mail), null, modifier = Modifier.size(24.dp)) },
                isReadOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInputField(
                value = oldPassword,
                onValueChange = { oldPassword = it; viewModel.errorMessage = null },
                label = "Old Password"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordInputField(
                value = newPassword,
                onValueChange = { newPassword = it; viewModel.errorMessage = null },
                label = "New Password"
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; viewModel.errorMessage = null },
                label = "Confirm New Password"
            )

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                // Inside your Button onClick in ChangePasswordScreen:
                onClick = {
                    when {
                        oldPassword.isEmpty() || newPassword.isEmpty() -> {
                            viewModel.errorMessage = "All fields are required"
                        }
                        newPassword.length < 8 -> {
                            viewModel.errorMessage = "New password is too short (min 8)"
                        }
                        newPassword != confirmPassword -> {
                            viewModel.errorMessage = "Passwords do not match"
                        }
                        else -> {
                            viewModel.performPasswordChange(context, oldPassword, newPassword) {
                                showSuccessAlert = true // Trigger Alert
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !viewModel.isLoading,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E4E68))
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Set new password", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(painterResource(R.drawable.send), null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- 6. Helper Components ---

@Composable
fun PasswordHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF2E4E68).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.refresh), null, tint = Color(0xFF2E4E68), modifier = Modifier.size(28.dp))
            }
            Column {
                Text("Change Password", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Update password for enhanced account security.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun InputField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit = {},
    leadingIcon: @Composable () -> Unit,
    isReadOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        readOnly = isReadOnly,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2E4E68),
            unfocusedBorderColor = Color(0xFFBDBDBD),
            unfocusedContainerColor = if (isReadOnly) Color(0xFFF2F2F2) else Color.White,
            focusedContainerColor = if (isReadOnly) Color(0xFFF2F2F2) else Color.White,
            disabledContainerColor = Color(0xFFF2F2F2),
            focusedLabelColor = Color(0xFF2E4E68)
        )
    )
}

@Composable
fun PasswordInputField(value: String, onValueChange: (String) -> Unit, label: String) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(painterResource(R.drawable.padlock), null, modifier = Modifier.size(24.dp)) },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    painterResource(if (passwordVisible) R.drawable.hide else R.drawable.view),
                    null,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2E4E68),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}