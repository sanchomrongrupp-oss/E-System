package com.example.e_system

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.e_system.ui.theme.ESystemTheme
import java.io.File

class ExerciseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ESystemTheme {
                ExerciseScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomExerciseTopBar()

            Spacer(modifier = Modifier.height(32.dp))

            ContentCard()
        }
    }

@Composable
fun CustomExerciseTopBar(
) {
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
@Composable
fun ContentCard() {
    var answer by remember { mutableStateOf("") }
    Card(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //Title
            Text(
                "Name Major",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            //Description
            Text(
                "Question",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, // This centers items vertically
                horizontalArrangement = Arrangement.Center
            ) {
                AttachmentPicker { uri ->
                    // Handle URI selection here
                }

                Spacer(modifier = Modifier.width(8.dp)) // Space between icon and field

                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    placeholder = {
                        Text(
                            text = "Answer",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = false,
                    maxLines = 4,
                )

            }
            Spacer(modifier = Modifier.height(16.dp)) // Space between icon and field
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                customButtonsubmit()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentPicker(
    onFileSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var tempCameraFileUri by remember { mutableStateOf<Uri?>(null) }

    // --- Launchers ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onFileSelected(uri)
        showBottomSheet = false
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) onFileSelected(uri)
        showBottomSheet = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraFileUri != null) onFileSelected(tempCameraFileUri!!)
        showBottomSheet = false
    }

    // Attachment Icon (Trigger)
    Icon(
        painter = painterResource(id = R.drawable.attach),
        contentDescription = "Attach",
        tint = Color.Gray,
        modifier = Modifier
            .size(24.dp)
            .clickable { showBottomSheet = true } // Trigger the bottom sheet
    )

    // Attachment Action Sheet (Modal Bottom Sheet)
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Add Attachment",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Divider()

                // Option 1: Take Photo
                AttachmentOptionRow(
                    icon = painterResource(id = R.drawable.camera), // FIX: Use 'id =' and correct access
                    label = "Camera"
                ) {
                    val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                    val uri = FileProvider.getUriForFile(
                        context,
                        FILE_PROVIDER_AUTHORITY,
                        file
                    )
                    tempCameraFileUri = uri
                    cameraLauncher.launch(uri)
                }

                // Option 2: Choose from Gallery
                AttachmentOptionRow(
                    icon = painterResource(id = R.drawable.photolibrary), // FIX: Use proper snake_case resource name (if assumed)
                    label = "Photo Gallery"
                ) {
                    galleryLauncher.launch("image/*") // MIME type for images
                }

                // Option 3: Choose File/Document
                AttachmentOptionRow(
                    icon = painterResource(id = R.drawable.attach), // FIX: Use 'id ='
                    label = "Document/File"
                ) {
                    fileLauncher.launch(arrayOf("*/*")) // Launch file picker for all file types
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun customButtonsubmit(){
    Button(
        onClick = { /* Handle Submit */ },
        modifier = Modifier
            .width(140.dp)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00468C))
    ){
        Text("Submit",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
        )
    }
}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewExerciseScreenNotStarted() {
    ESystemTheme {
        ExerciseScreen(

        )
    }
}
