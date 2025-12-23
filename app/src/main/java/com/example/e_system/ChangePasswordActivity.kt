package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_system.ui.theme.ESystemTheme // Assuming your theme is located here

class ChangePasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                ChangePasswordScreen(
                    currentEmail = "meng11@gmail.com", // Example user email
                    onBackClicked = { finish() },
                    onPasswordChanged = { oldPass, newPass ->
                        // In a real app, this is where you'd call your backend API
                        println("Attempting password change from $oldPass to $newPass")
                    }
                )
            }
        }
    }
}

// --- Main Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    currentEmail: String,
    onBackClicked: () -> Unit,
    onPasswordChanged: (String, String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Change Password",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            painter = painterResource(R.drawable.back),
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF7F7F7) // Light gray background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. Header Card (Lock Icon + Description) ---
            PasswordHeader(currentEmail)

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. Input Fields ---

            // Email (Non-editable, for user reference)
            InputField(
                value = currentEmail,
                label = "",
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.mail) ,
                        contentDescription = "Email Icon", // IMPORTANT for accessibility
                        tint = Color.Gray // Optional: Set a tint color
                    )
                              },
                isReadOnly = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Old Password
            PasswordInputField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = "Old Password"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // New Password
            PasswordInputField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "New Password"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Confirm New Password
            PasswordInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm New Password"
            )

            Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

            // --- 3. Submission Button ---
            Button(
                onClick = {
                    if (newPassword == confirmPassword && newPassword.isNotEmpty()) {
                        onPasswordChanged(oldPassword, newPassword)
                    } else {
                        // In a real app, you would show a Snackbar or error message here
                        println("Error: Passwords must match and cannot be empty.")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E4E68) // Dark Blue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Set new password",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(
                    painter = painterResource(R.drawable.send),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- Composable for Header Section ---
@Composable
fun PasswordHeader(email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated/Spinning Lock Icon (Simulated with Refresh)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.refresh), // Using Refresh to simulate the circular arrow
                    contentDescription = "Change Password",
                    tint = Color(0xFF2E4E68),
                    modifier = Modifier.size(32.dp)
                )
            }

            Column {
                Text(
                    text = "Change Password",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Update password for enhanced account security.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

// --- Reusable Composable for Text/Email Input ---
@Composable
fun InputField(
    value: String,
    label: String,
    leadingIcon: @Composable () -> Unit,
    isReadOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = { /* Only changes if not readOnly */ },
        label = if (label.isNotEmpty()) {
            @Composable { Text(label) }
        } else null,
        readOnly = isReadOnly,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.mail),
                contentDescription = "Email Icon Reset",
                tint = Color.Gray,
                modifier = Modifier
                    .size(24.dp)
            )
        },
        shape = RoundedCornerShape(10.dp),
        // --- FIX IS HERE ---
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2E4E68),
            unfocusedBorderColor = Color.LightGray,
            disabledBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White
        )
    )
}

@Composable
fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },

        // 1. FIX: Leading Icon uses R.drawable.padlock
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.padlock),
                contentDescription = "Lock",
                tint = Color.Gray,
                modifier = Modifier
                    .size(24.dp)
            )
        },

        // 2. FIX: Trailing Icon uses the correct drawables (view/hide)
        trailingIcon = {
            // Determine which drawable to show based on passwordVisible state
            val imageId = if (passwordVisible) R.drawable.hide else R.drawable.view

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    // Use the determined imageId
                    painter = painterResource(id = imageId),
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        },

        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),

        // The colors part is already correctly using Material 3 OutlinedTextFieldDefaults.colors
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2E4E68),
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    ESystemTheme {
        ChangePasswordScreen(
            currentEmail = "meng11@gmail.com",
            onBackClicked = {},
            onPasswordChanged = { _, _ -> }
        )
    }
}