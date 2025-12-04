package com.example.e_system

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_system.ui.theme.ESystemTheme

class IntroFirstActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                IntroFirstScreen(
                    onSkip = {
                        // ✅ Go directly to main activity
                        startActivity(Intent(this, SigInActivity::class.java))
                        finish() // finish to prevent going back
                    },
                    onNext = {
                        // ✅ Go to next intro page
                        startActivity(Intent(this, IntroSecondActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun IntroFirstScreen(onSkip: () -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        // Skip Button (top-right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Skip",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D4B65),
                modifier = Modifier.clickable { onSkip() }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logo section
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.rupp_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Title & Description
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ការរៀនតាមអ៊ីនធឺណិត",
                fontSize = 28.sp,
                color = Color(0xFF2D4B65),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "យើងផ្តល់ជូនថ្នាក់រៀនអនឡាញ\nនិងមេរៀនដែលបានកត់ត្រាទុកជាមុន។",
                fontSize = 18.sp,
                color = Color(0xFF545454),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Indicators + Next Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicators
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(10.dp)
                        .background(Color(0xFF2D4B65), RoundedCornerShape(20.dp))
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
                        .size(10.dp)
                        .background(Color(0xFFD5E2F5), CircleShape)
                )
            }

            // Next Button
            Button(
                onClick = { onNext() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4B65)),
                shape = CircleShape,
                modifier = Modifier.size(70.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = "Next",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewIntroFirstScreen() {
    ESystemTheme {
        IntroFirstScreen(onSkip = {}, onNext = {})
    }
}
