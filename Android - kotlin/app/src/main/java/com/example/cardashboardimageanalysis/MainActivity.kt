package com.example.cardashboardimageanalysis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

// Define a dark color scheme with blue accents.
private val BlueDarkColorScheme = darkColorScheme(
    primary = Color(0xFF2196F3),       // Blue 500
    onPrimary = Color.White,
    secondary = Color(0xFF03A9F4),     // Light Blue accent
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    error = Color(0xFFCF6679)
)

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = BlueDarkColorScheme) {
                // Obtain the ViewModel instance.
                val viewModel: BakingViewModel = viewModel()
                // Local state for the message input and selected image.
                var message by remember { mutableStateOf("") }
                var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
                // Collect messages from the ViewModel.
                val messages by viewModel.messages.collectAsState()
                val context = LocalContext.current

                // Launchers for Camera and Gallery.
                val cameraLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                        selectedImageBitmap = imageBitmap
                    }
                }

                val galleryLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let {
                        try {
                            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                            } else {
                                val source = ImageDecoder.createSource(context.contentResolver, it)
                                ImageDecoder.decodeBitmap(source)
                            }
                            selectedImageBitmap = bitmap
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                // Scaffold with a top bar, main content, and bottom bar.
                Scaffold(
                    topBar = {
                        SmallTopAppBar(
                            title = { Text("Card Dashboard Image Analysis") }
                        )
                    },
                    bottomBar = {
                        BottomBar(
                            message = message,
                            onMessageChange = { message = it },
                            isSendEnabled = message.isNotBlank() || selectedImageBitmap != null,
                            onSendClick = {
                                // When sending, include the selected image with the message.
                                viewModel.sendMessage(message, selectedImageBitmap)
                                // Clear the input and the preview.
                                message = ""
                                selectedImageBitmap = null
                            },
                            onTakePhotoClick = {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    cameraLauncher.launch(cameraIntent)
                                } else {
                                    ActivityCompat.requestPermissions(
                                        context as Activity,
                                        arrayOf(Manifest.permission.CAMERA),
                                        100
                                    )
                                }
                            },
                            onChooseGalleryClick = {
                                val galleryPermission = if (Build.VERSION.SDK_INT >= 33)
                                    Manifest.permission.READ_MEDIA_IMAGES
                                else
                                    Manifest.permission.READ_EXTERNAL_STORAGE

                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        galleryPermission
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    galleryLauncher.launch("image/*")
                                } else {
                                    ActivityCompat.requestPermissions(
                                        context as Activity,
                                        arrayOf(galleryPermission),
                                        101
                                    )
                                }
                            },
                            // Show a preview of the selected image (if any) in the bottom bar.
                            selectedImageBitmap = selectedImageBitmap
                        )
                    }
                ) { innerPadding ->
                    // Main content: display the conversation (chat messages).
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        messages.forEach { msg ->
                            val background = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            val textColor = if (msg.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = background)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    // If the message has an image, display it inline.
                                    msg.imageBitmap?.let { bmp ->
                                        Image(
                                            bitmap = bmp.asImageBitmap(),
                                            contentDescription = "Sent Image",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    Text(
                                        text = msg.text,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Optional: Handle permissions if needed.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Add your handling (Snackbar, Toast, etc.) if desired.
    }
}

/**
 * BottomBar composable:
 * - Displays a preview of the selected image (if any),
 * - A text input for the message,
 * - Buttons for sending the message and picking images.
 */
@Composable
fun BottomBar(
    message: String,
    onMessageChange: (String) -> Unit,
    isSendEnabled: Boolean,
    onSendClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onChooseGalleryClick: () -> Unit,
    selectedImageBitmap: Bitmap?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Show a preview of the selected image in the bottom bar if one is selected.
        selectedImageBitmap?.let { bmp ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Selected Image Preview",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        // Row for text input and Send button.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                label = { Text("Type your message") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onSendClick,
                enabled = isSendEnabled
            ) {
                Text("Send")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Row for image picker buttons.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onTakePhotoClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Take Photo")
            }
            Button(
                onClick = onChooseGalleryClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Gallery")
            }
        }
    }
}
