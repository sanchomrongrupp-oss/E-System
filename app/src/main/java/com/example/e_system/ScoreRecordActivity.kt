package com.example.e_system

import android.R.attr.onClick
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_system.ui.theme.ESystemTheme

class ScoreRecordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESystemTheme {
                ScoreRecordScreen(
                    onBackClicked = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreRecordScreen(
    onBackClicked: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            Column {
                // Header with Score Record title and Back Button
                TopAppBar(
                    title = {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Score Record",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
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
                        containerColor = Color.White
                    )
                )

                // Subtitle "Subjects" below the main header
                Text(
                    text = "Subjects",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
                    color = Color.Black
                )
            }
        },
        containerColor = Color(0xFFF7F7F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Content Area with a Card for the list
            MenuCard("Mobile App",
                onClick={
                    val intent = Intent(context, SubjectScoreRecordActivity::class.java)
                    intent.putExtra("card_title", "Mobile App")   // <-- send text here
                    context.startActivity(intent)
                }
            )
            MenuCard("SE & IT",
                onClick={
                    val intent = Intent(context, SubjectScoreRecordActivity::class.java)
                    intent.putExtra("card_title", "SE & IT")   // <-- send text here
                    context.startActivity(intent)
                }
            )
            MenuCard("MIS",
                onClick={
                    val intent = Intent(context, SubjectScoreRecordActivity::class.java)
                    intent.putExtra("card_title", "MIS")   // <-- send text here
                    context.startActivity(intent)
                }
            )
            MenuCard("OOAD and Prog.",
                onClick={
                    val intent = Intent(context, SubjectScoreRecordActivity::class.java)
                    intent.putExtra("card_title", "OOAD and Prog.")   // <-- send text here
                    context.startActivity(intent)
                }
            )
            MenuCard("Windows Server",
                onClick={
                    val intent = Intent(context, SubjectScoreRecordActivity::class.java)
                    intent.putExtra("card_title", "Windows Server")   // <-- send text here
                    context.startActivity(intent)
                }
            )
            MenuCard("Academic Transcript",
                onClick={
                    val intent = Intent(context, SubjectScoreRecordActivity::class.java)
                    intent.putExtra("card_title", "Academic Transcript")   // <-- send text here
                    context.startActivity(intent)
                }
            )
        }
    }
}
@Composable
fun MenuCard(
    text: String,
    startIcon: Int = R.drawable.document,
    endIcon: Int = R.drawable.arrow_right,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Start Icon
            Icon(
                painter = painterResource(startIcon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Dynamic Text
            Text(
                text = text,
                modifier = Modifier.weight(1f)
            )

            // End Icon (arrow)
            Icon(
                painter = painterResource(endIcon),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScoreRecordScreenPreview() {
    ESystemTheme {
        ScoreRecordScreen(
            onBackClicked = {},
        )
    }
}