package com.garbi.garbi_recolection.composables

import Address
import AppScaffold
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.garbi.garbi_recolection.models.Report
import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import coil.compose.AsyncImage
import com.garbi.garbi_recolection.services.RetrofitClient
import com.garbi.garbi_recolection.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.google.gson.Gson
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditReportScreen(navController: NavController? = null, reportId: String) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var isModifiable by remember { mutableStateOf(false) }

    val enumItems =  stringArrayResource(R.array.report_types).toList()
    val enumValueToItem = mapOf(
        stringResource(R.string.report_type_enum_contenedor_roto) to enumItems[0],
        stringResource(R.string.report_type_enum_contenedor_sucio) to enumItems[1],
        stringResource(R.string.report_type_enum_basura_calle) to enumItems[2],
        stringResource(R.string.report_type_enum_contenedor_faltante) to enumItems[3],
        stringResource(R.string.report_type_enum_otros) to enumItems[4]
    )

    var reportData by remember { mutableStateOf(Report(
        _id = null,
        userId = "",
        containerId = "", //todo
        managerId = null,
        title = "",
        observation = null,
        description = null, //TODO MAYBE SHOULD BE NULLABLE
        address = null, //todo
        phone = null,
        email = "",
        status = null,
        type = "",
        createdAt = null,
        deletedAt = null
    )) }

    var reportDetails: Report? by remember { mutableStateOf(null) }
    val newStatus = stringResource(R.string.status_new)
    var selectedItem by remember { mutableStateOf("") }
    LaunchedEffect(reportId) {
        val service = RetrofitClient.reportService
        try {
            val response = withContext(Dispatchers.IO) { service.getReport(reportId) }
            reportDetails = response
            println("reportDetails: $reportDetails")

            val listOfStatus = reportDetails!!.status
            if (listOfStatus!![listOfStatus.size -1].status == newStatus) {
                isModifiable = true
            }

            selectedItem = enumValueToItem[reportDetails!!.type] ?: reportDetails!!.type

            reportData = reportDetails!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    var imagePath by remember { mutableStateOf<String?>(null) }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = focusedContainer,
        unfocusedContainerColor = unfocusedContainer,
        focusedTextColor = Black,
        unfocusedTextColor = Black,
        focusedLabelColor = DarkGray,
        unfocusedLabelColor = DarkGray,
        cursorColor = Green900,
        focusedIndicatorColor = Green900,
        disabledContainerColor = DisabledField,
        disabledLabelColor = DisabledFieldContent
    )


    //Get userId
    LaunchedEffect(context) {
        val userDetails = RetrofitClient.getSession(context, navController!!)
        reportData = reportData.copy(userId = userDetails?._id ?: "")
        reportData = reportData.copy(email = userDetails?.email ?: "")
    }

    ////// Type Dropdown
    var expanded by remember { mutableStateOf(false) }
    val textItems =  stringArrayResource(R.array.report_types).toList()
    val itemToEnumValue = mapOf(
        textItems[0] to stringResource(R.string.report_type_enum_contenedor_roto),
        textItems[1] to stringResource(R.string.report_type_enum_contenedor_sucio),
        textItems[2] to stringResource(R.string.report_type_enum_basura_calle),
        textItems[3] to stringResource(R.string.report_type_enum_contenedor_faltante),
        textItems[4] to stringResource(R.string.report_type_enum_otros),
    )

    ////// Take picture button
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
            imagePath = tempFile.absolutePath
            isPhotoTaken = true
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            cameraLauncher.launch(tempFileUri)
        }
    }

    ////// Edit Report button
    val openAlertDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.edit_report_screen),
        backButton = true
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 0.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            reportDetails?.let { details ->

                TextField(
                    value = reportData.title,
                    onValueChange = { data -> reportData = reportData.copy(title = data) },
                    label = { Text(text = stringResource(R.string.title_field) + "*") },
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
                        value = selectedItem,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(text = stringResource(R.string.type_dropdown) + "*") },
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
                        textItems.forEach { item ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedItem = item
                                    reportData.type = itemToEnumValue[item] ?: item
                                    expanded = false
                                }
                            ) {
                                Text(item)
                            }
                        }
                    }
                }


                TextField(
                    value = reportData.description ?: "",
                    onValueChange = { data -> reportData = reportData.copy(description = data) },
                    label = { Text(text = stringResource(R.string.description_field)) },
                    colors = fieldColors,
                    singleLine = false,
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp)
                )


                if (details.imagePath != null) {
                    androidx.compose.material.Text(
                        text = stringResource(R.string.photo_field),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    val mockImagePath = R.drawable.broken_container2
                    AsyncImage(
                        model = mockImagePath,
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp, 280.dp)
                            .padding(0.dp, 8.dp)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                    //todo add delete icon here
                } else {
                    if (!isPhotoTaken) {
                        OutlinedButton(
                            onClick = {
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        tempFile = createTempFile()
                                        tempFileUri = FileProvider.getUriForFile(
                                            context,
                                            "com.garbi.garbi_recolection.provider",
                                            tempFile
                                        )
                                        cameraLauncher.launch(tempFileUri)
                                    }

                                    else -> {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add_a_photo),
                                contentDescription = "add photo button",
                                tint = Black,
                                modifier = Modifier.padding(8.dp, 8.dp, 4.dp, 8.dp)
                            )
                            Text(
                                text = stringResource(R.string.take_photo_text),
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
                                        imagePath = null
                                    },
                                    colors = IconButtonDefaults.outlinedIconButtonColors(
                                        containerColor = containerColor,
                                        contentColor = White
                                    ),
                                    border = BorderStroke(0.1.dp, White),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete photo",
                                        tint = White,
                                    )
                                }
                            }
                        }
                    }
                }


                TextField(
                    value = reportData.containerId,
                    enabled = false,
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.container_id_field)) },
                    colors = fieldColors,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp)
                )

                TextField(
                    value = reportData.address!!.convertToString(),
                    enabled = false,
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.address_field)) },
                    colors = fieldColors,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .padding(16.dp, 16.dp, 16.dp, 4.dp)
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = { openAlertDialog.value = true },
                        enabled = reportData.requiredFieldsCompleted(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green900,
                            contentColor = White,
                            disabledContainerColor = DisabledButton,
                            disabledContentColor = DisabledButtonText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.edit_report_save))
                    }

                    if (!reportData.requiredFieldsCompleted()) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Transparent)
                                .clickable(
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            R.string.complete_fields_toast,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                        )
                    }
                }
            }

            if (openAlertDialog.value) {
                AlertDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirmation = {
                        coroutineScope.launch {
                            val success = editReport(reportData, imagePath, context)
                            if (success) {
                                navController?.navigate("reports")
                                openAlertDialog.value = false
                            }
                        }
                    },
                    dialogText = stringResource(R.string.edit_report_dialog_text),
                    confirmText = stringResource(R.string.edit_report_dialog_confirm)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

suspend fun editReport(reportData: Report, imagePath: String?, context: Context): Boolean {
    val reportService = RetrofitClient.reportService
    return withContext(Dispatchers.IO) {
        try {
            val imagePart = createImagePart(imagePath)
            val reportPart  = createReportRequestBody(reportData)
            val response = reportService.editReport(reportData._id!!, reportPart, imagePart)
            withContext(Dispatchers.Main) {  // todo main? no era IO?
                if (response.isSuccessful) {
                    Toast.makeText(context, R.string.report_edited_toast, Toast.LENGTH_LONG).show()
                } else {
                    println("code: ${response.code()}")
                    println("errorbody: ${response.errorBody()?.string()}")
                    Toast.makeText(context, R.string.report_edition_error_toast, Toast.LENGTH_LONG).show()
                }
                response.isSuccessful
            }
        } catch (e: HttpException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}

//fun createImagePart(imagePath: String?): MultipartBody.Part? {
//    if (imagePath == null) return null
//
//    val file = File(imagePath)
//    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//    return MultipartBody.Part.createFormData("image", file.name, requestFile)
//}
//
//fun createReportRequestBody(report: Report): RequestBody {
//    val gson = Gson()
//    val json = gson.toJson(report)
//    return json.toRequestBody("application/json".toMediaTypeOrNull())
//}
