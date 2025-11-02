package com.example.e_system

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.e_system.ui.theme.ESystemTheme

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                SignInScreen()
            }
        }
    }
}

@Composable
fun SignInScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.rupp_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Let's go into account!",
            fontSize = 25.sp,
            color = Color(0xFF2D4B65),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
            text = "Login to your account to start your courses.",
            fontSize = 18.sp,
            color = Color(0xFF1E3445),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("អ៊ីមែល / Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.mail),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(24.dp)
                )
            },
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                containerColor = Color.White,
//                focusedBorderColor = Color(0xFF2D4B65),
//                unfocusedBorderColor = Color.Gray,
//                textColor = Color.Black,
//                placeholderColor = Color.Black
//            ),
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
                .height(60.dp),
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
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                containerColor = Color.White,
//                focusedBorderColor = Color(0xFF2D4B65),
//                unfocusedBorderColor = Color.Gray,
//                textColor = Color.Black,
//                placeholderColor = Color.Black
//            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Login Button
        Button(
            onClick = { /* Handle login action */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4B65)),
//            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
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
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen() {
    ESystemTheme {
        SignInScreen()
    }
}
