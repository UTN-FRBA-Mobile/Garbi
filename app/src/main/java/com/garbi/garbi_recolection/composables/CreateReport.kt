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
import androidx.compose.material.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.common_components.*
import android.Manifest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.window.PopupProperties
import com.garbi.garbi_recolection.ui.theme.Green900
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateReportScreen(navController: NavController? = null) {

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Green900.copy(alpha = 0.2f),
        unfocusedContainerColor = Green900.copy(alpha = 0.2f),
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color.DarkGray,
        unfocusedLabelColor = Color.DarkGray,
        cursorColor = Green900,
        focusedIndicatorColor = Green900,
        disabledContainerColor = Color.LightGray,
        disabledLabelColor = Color.DarkGray
    )

    ////// Title field
    var titleText by rememberSaveable { mutableStateOf("") }

    ////// Type Dropdown
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    val items =  stringArrayResource(R.array.report_items).toList()

    ////// Description field
    var descriptionText by rememberSaveable { mutableStateOf("") }
    var containerIdText by rememberSaveable { mutableStateOf("") }

    ////// Take picture button
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Function to create a new temporary file
    fun createTempFile(): File {
        return File.createTempFile("camera_image", ".jpg", context.externalCacheDir).apply {
            deleteOnExit()
        }
    }

    // File and Uri for the camera photo
    var tempFile by remember { mutableStateOf(createTempFile()) }
    var tempFileUri: Uri = FileProvider.getUriForFile(context, "com.garbi.garbi_recolection.provider", tempFile)
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

    ////// Create report button
    val isCreateReportEnabled = titleText.isNotEmpty() && selectedText.isNotEmpty()
    val openAlertDialog = remember { mutableStateOf(false) }


    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.create_report_screen)
    ) {
        Column (
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp, 16.dp)
        ) {
            TextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text(text = stringResource(R.string.title_field)) },
//                supportingText = { Text(text = stringResource(R.string.supporting_text_required) ) },
                singleLine = true,
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            )


            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.padding(0.dp, 8.dp)
            ) {
                TextField(
                    value = selectedText,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.type_dropdown)) },
//                    supportingText = { Text(text = stringResource(R.string.supporting_text_required)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = fieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(
                        focusable = true,
                        dismissOnClickOutside = true,
                        dismissOnBackPress = true
                    ),
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                selectedText = item
                                expanded = false
                            }
                        ) {
                            Text(item)
                        }
                    }
                }
            }


            TextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                label = { Text(text = stringResource(R.string.description_field)) },
                colors = fieldColors,
                singleLine = false,
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            )

            if (!isPhotoTaken) {
                OutlinedButton(
                    onClick = {
                        when {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED -> {
                                tempFile = createTempFile()
                                tempFileUri = FileProvider.getUriForFile(context, "com.garbi.garbi_recolection.provider", tempFile)
                                cameraLauncher.launch(tempFileUri)
                            }
                            else -> {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
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
                    modifier = Modifier
                        .size(200.dp, 280.dp)
                        .padding(0.dp, 8.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (isPhotoTaken) {
                        OutlinedIconButton(
                            onClick = {
                                selectedImageUri = null
                                isPhotoTaken = false
                            },
                            colors = IconButtonDefaults.outlinedIconButtonColors(
                                containerColor = Color(0xFF000000).copy(alpha = 0.5f),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(0.1.dp, Color.White),
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

            TextField(
                value = containerIdText,
                enabled = false,
                onValueChange = {
                    containerIdText = it
                },
                label = { Text(text = stringResource(R.string.container_id_field)) },
                colors = fieldColors,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            )

            TextField(
                value = containerIdText,
                enabled = false,
                onValueChange = {containerIdText = it },
                label = { Text(text = stringResource(id = R.string.address_field)) },
                colors = fieldColors,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { openAlertDialog.value = true },
                enabled = isCreateReportEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green900,
                    contentColor = Color.White,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.DarkGray,
                ),
                modifier = Modifier
                    .padding(16.dp, 32.dp, 16.dp, 4.dp)
                    .fillMaxWidth()
            ) {
                Text( text = stringResource(id = R.string.create_report_button) )
            }

            if (openAlertDialog.value) {
                AlertDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirmation = {
                        navController?.navigate("reports")
                        openAlertDialog.value = false
                    },
                    dialogText = stringResource(R.string.create_report_dialog_text),
                    confirmText = stringResource(R.string.create_report_dialog_confirm)
                )
            }
        }
    }
}
