package com.example.e_system

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme

class IntroThirdActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                IntroThirdScreen(
                    onSkip = { startActivity(Intent(this, MainActivity::class.java)) },
                    onNext = { startActivity(Intent(this, MainActivity::class.java)) }
                )
            }
        }
    }
}

@Composable
fun IntroThirdScreen(onSkip: () -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {

        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Skip",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSkip() }
            )
        }
        Spacer(modifier = Modifier.height(80.dp))

        Image(
            painter = painterResource(id = R.drawable.rupp_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)  // <-- Center the Image
        )


        Spacer(modifier = Modifier.height(80.dp))

        // Title
        Text(
            text = "សិក្សារតាមអ៊ីនធឺណិត",
            fontSize = 28.sp,
            color = Color(0xFF2D4B65),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Description
        Text(
            text = "វិភាគពិន្ទុរបស់អ្នក និងតាមដានលទ្ធផលរបស់អ្នក",
            fontSize = 18.sp,
            color = Color(0xFF545454),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(234.dp))

// Indicators (SECOND SELECTED)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicators
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFFD5E2F5), RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFFD5E2F5), CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(10.dp)
                        .background(Color(0xFF2D4B65), CircleShape)
                )
            }

            // Next button
            Button(
                onClick = { onNext() },
                colors = ButtonDefaults.buttonColors(Color(0xFF2D4B65)),
                shape = CircleShape,
                modifier = Modifier
                    .height(70.dp)
                    .width(200.dp)
            ) {
                Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                    "Stsrt",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                    )
                    Image(
                        painter = painterResource(id = R.drawable.arrow_start),
                        contentDescription = "Next",
                        modifier = Modifier
                            .size(60.dp)
                            .padding(start = 10.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIntroThirdScreen() {
    ESystemTheme {
        IntroThirdScreen(
            onSkip = {},
            onNext = {}
        )
    }
}
