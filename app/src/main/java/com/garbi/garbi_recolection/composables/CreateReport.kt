package com.garbi.garbi_recolection.composables

import AppScaffold
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.garbi.garbi_recolection.R
//import com.garbi.garbi_recolection.BuildConfig
import coil.compose.rememberImagePainter
import android.Manifest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateReportScreen(navController: NavController? = null) {
    var titleText by rememberSaveable { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    val items = listOf("Contenedor en mal estado", "Contenedor sucio", "Basura en la calle", "Contenedor faltante", "Otro")

    var descriptionText by rememberSaveable { mutableStateOf("") }
    var containerIdText by rememberSaveable { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }


    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // File and Uri for the camera photo
    val tempFile = remember {
        File.createTempFile("camera_image", ".jpg", context.externalCacheDir).apply {
            deleteOnExit()
        }
    }
    val tempFileUri: Uri = FileProvider.getUriForFile(context, "com.garbi.garbi_recolection.provider", tempFile)
    var isPhotoTaken by remember { mutableStateOf(false) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            selectedImageUri = tempFileUri
            isPhotoTaken = true
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            cameraLauncher.launch(tempFileUri)
        } else {
            // Handle permission denied case
        }
    }

    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.create_report_screen)
    ) {
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp, 16.dp)
        )
        {
            TextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            ) {
                TextField(
                    value = selectedText,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Tipo de problema") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedText = item
                                expanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            TextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                label = { Text("Descripción del problema") },
                singleLine = false,
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            )

            TextField( //ve esto? ya se pondría solo el id creo.
                value = containerIdText,
                onValueChange = {
                    containerIdText = it
                    isError = it.isNotEmpty() && !it.matches(Regex("^[0-9]*$"))
                },
                label = { Text("Id del contendor (6 dígitos)") },
                singleLine = true,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            )
            if (isError) {
                Text(
                    text = "Solo números pueden ser ingresados",
                    //FINISH THIS ERROR PART. la doc oficial decia cosas creo. poner tmb q se abra teclado numérico de una
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            if (!isPhotoTaken) {
                OutlinedButton(
                    onClick = {
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                cameraLauncher.launch(tempFileUri)
                            }

                            else -> {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_a_photo),
                        contentDescription = "add photo button",
                        tint = Color.Black,
                        modifier = Modifier.padding(8.dp, 8.dp, 4.dp, 8.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.take_photo_text),
                        modifier = Modifier.padding(4.dp, 8.dp, 8.dp, 8.dp)
                    )
                }
            }

            selectedImageUri?.let { uri ->
                Box(
                    modifier = Modifier.size(200.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (isPhotoTaken) {
                        OutlinedButton(
                            onClick = {
                                selectedImageUri = null
                                isPhotoTaken = false
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF000000).copy(alpha = 0.5f),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete photo",
                                tint = Color.White,
                            )
                        }
                    }
                }
            }

        }
    }
}