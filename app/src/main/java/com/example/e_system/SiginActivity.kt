package com.example.e_system

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.e_system.ui.theme.Base_Url
import com.example.e_system.ui.theme.ESystemTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.logging.HttpLoggingInterceptor


// --- 1. TOKEN MANAGER (Sync with MyAccountActivity) ---
class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun clear() = prefs.edit().clear().apply()
}

// --- 2. DATA MODELS ---
data class LoginRequest(val email: String, val password: String)
data class UserData(val _id: String, val fullName: String, val email: String, val role: String)
data class LoginResponse(val token: String, val user: UserData)

// --- 3. API SERVICE ---
interface ApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

// --- 4. RETROFIT CLIENT ---
object RetrofitClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Base_Url.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// 3. Activity & UI
class SigInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                var isLoading by remember { mutableStateOf(false) }

                SigInScreen(
                    isLoading = isLoading,
                    onSignIn = { email, password ->
                        handleLogin(email, password) { isLoading = it }
                    }
                )
            }
        }
    }

    private fun handleLogin(email: String, pass: String, setLoading: (Boolean) -> Unit) {
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, pass))

                if (response.isSuccessful && response.body() != null) {
                    // FIXED: Using TokenManager to save token to "user_prefs"
                    val token = response.body()!!.token
                    TokenManager(this@SigInActivity).saveToken(token)

                    Toast.makeText(this@SigInActivity, "Welcome ${response.body()?.user?.fullName}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@SigInActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@SigInActivity, "Login Failed: Check credentials", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SigInActivity, "Network Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }
}

@Composable
fun SigInScreen(isLoading: Boolean, onSignIn: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Enable vertical scrolling for small devices
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.rupp_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "Let's go into account!",
            fontSize = 25.sp,
            color = Color(0xFF2D4B65),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        Text(
            text = "Login to your account to start your courses.",
            fontSize = 18.sp,
            color = Color(0xFF1E3445),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("អ៊ីមែល / Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            enabled = !isLoading,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.mail),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(24.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("លេខកូដសម្ងាត់ / Password") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.padlock),
                    contentDescription = "Password Icon",
                    modifier = Modifier.size(24.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    val icon = if (passwordVisible) R.drawable.hide else R.drawable.view
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "Toggle Password Visibility",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = { onSignIn(email, password) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4B65)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            enabled = !isLoading
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Login Into Account",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(id = R.drawable.arrow_start),
                    contentDescription = "Next",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen() {
    ESystemTheme {
        // Pass false for isLoading so we can see the button text in preview
        SigInScreen(
            isLoading = false,
            onSignIn = { email, password -> /* Do nothing in preview */ }
        )
    }
}
