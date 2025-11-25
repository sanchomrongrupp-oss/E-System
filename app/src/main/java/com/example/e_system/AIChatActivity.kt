package com.example.e_system

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.e_system.ui.theme.ESystemTheme
import java.io.File


// Replace with your actual application ID from build.gradle
private const val FILE_PROVIDER_AUTHORITY = "com.example.e_system.fileprovider"

class AIChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESystemTheme {
                // Pass navigation actions and the file handler
                RUPPChatScreen(
                    userName = "Kimleap",
                    onBackClick = { finish() },
                    onFileSelected = { uri ->
                        println("Selected File URI: $uri")
                        // TODO: Implement file upload/preview logic here
                    }
                )
            }
        }
    }
}

@Composable
fun RUPPChatScreen(
    userName: String,
    onBackClick: () -> Unit,
    onFileSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = { RUPPToolbar(onBackClick = onBackClick) },
        bottomBar = {
            RUPPInputBar(
                message = currentMessage,
                onMessageChange = { currentMessage = it },
                onSendClick = {
                    if (currentMessage.isNotBlank()) {
                        // Implement logic to send message
                        println("Sending message: $currentMessage")
                        currentMessage = "" // Clear input field
                    }
                },
                onFileSelected = onFileSelected
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello, $userName",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E4E68)
            )
            // TODO: Chat History LazyColumn goes here
        }
    }
}

// ----------------------------------------------------
// 1. Top Bar Component
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RUPPToolbar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "RUPP Chat",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.back), // Assumes R.drawable.back exists
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Handle Menu Click */ }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.Black,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

// ----------------------------------------------------
// 2. Input Bar Component
// ----------------------------------------------------
@Composable
fun RUPPInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onFileSelected: (Uri) -> Unit
) {
    val isMessageEmpty = message.isBlank()
    val sendIconColor = if (isMessageEmpty) Color.Gray else Color(0xFF2E4E68) // Active color

    Surface(
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Input controls (Left side: Plus & Attach)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "Add",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { /* Handle plus click */ }
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    // --- Attachment Picker Trigger ---
                    AttachmentPickerTrigger(onFileSelected = onFileSelected)
                    // -------------------------------

                    Spacer(modifier = Modifier.width(10.dp))
                }

                // Actual Text Input Field
                OutlinedTextField(
                    value = message,
                    onValueChange = onMessageChange,
                    placeholder = {
                        Text(
                            text = "Ask RUPP Chat",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = false,
                    maxLines = 4,
                )

                // Voice/Send Icons (Right side)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(10.dp))
                    if (isMessageEmpty) {
                        // Mic Icon when input is empty
                        Icon(
                            painter = painterResource(id = R.drawable.mic),
                            contentDescription = "Voice",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { /* Handle mic click */ }
                        )
                    } else {
                        // Send Button when input is not empty
                        IconButton(onClick = onSendClick, enabled = !isMessageEmpty) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = sendIconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. Attachment Picker Components (Bottom Sheet Logic)
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentPickerTrigger(
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

// ----------------------------------------------------
// MODIFIED AttachmentOptionRow (FIXED)
// ----------------------------------------------------
@Composable
fun AttachmentOptionRow(
    icon: Painter, // Input is a Painter
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // FIX: Use the 'painter' parameter of the Icon composable.
        // The original code was incorrectly using 'imageVector = icon' when 'icon' is a Painter.
        Icon(
            painter = icon, // Pass the Painter directly
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}

// ----------------------------------------------------
// 4. Preview Component
// ----------------------------------------------------
@Preview(showBackground = true)
@Composable
fun RUPPChatPreview() {
    ESystemTheme {
        RUPPChatScreen(userName = "Kimleap", onBackClick = {}, onFileSelected = {})
    }
}