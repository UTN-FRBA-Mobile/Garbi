package com.garbi.garbi_recolection.composables

import AppScaffold
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.amazonaws.HttpMethod
import com.garbi.garbi_recolection.models.Report
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.common_components.ReportStatusChip
import com.garbi.garbi_recolection.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import java.net.URL
import java.time.Instant
import java.util.*

@Composable
fun ReportDetailsScreen (navController: NavController? = null, reportId: String) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    //For the edit and delete buttons in the TopBar
    var isModifiable by remember { mutableStateOf(false) }

    var reportDetails: Report? by remember { mutableStateOf(null) }
    val newStatus = stringResource(R.string.status_new)
    LaunchedEffect(reportId) {
        val service = RetrofitClient.reportService
        try {
            val response = withContext(Dispatchers.IO) { service.getReport(reportId) }
            reportDetails = response
            println("reportDetails: $reportDetails")

            val listOfStatus = reportDetails!!.status
            if (listOfStatus!![listOfStatus.size -1].status == newStatus) { //TODO dedidir q queremos. si hacemos esto, al supervisor habria q avisarle q el recolector hizo tal cosa.
                isModifiable = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val items =  stringArrayResource(R.array.report_types).toList()
    val enumValueToItem = mapOf(
        stringResource(R.string.report_type_enum_contenedor_roto) to items[0],
        stringResource(R.string.report_type_enum_contenedor_sucio) to items[1],
        stringResource(R.string.report_type_enum_basura_calle) to items[2],
        stringResource(R.string.report_type_enum_contenedor_faltante) to items[3],
        stringResource(R.string.report_type_enum_otros) to items[4]
    )

    // for Delete report functionality
    val openAlertDialog = remember { mutableStateOf(false) }
    var deleteConfirmed by remember { mutableStateOf(false) }

    if (deleteConfirmed) {
        LaunchedEffect(reportId) {
            val service = RetrofitClient.reportService

            try {
                val response = withContext(Dispatchers.IO) { service.deleteReport(reportId) }
                println("response: $response")
                if (response.isSuccessful) {
                    navController?.navigate("reports")
                    Toast.makeText(context, R.string.report_deleted_toast, Toast.LENGTH_LONG).show()
                } else {
                    println("code: ${response.code()}")
                    println("errorbody: ${response.errorBody()?.string()}")
                    Toast.makeText(context, R.string.report_deletion_error_toast, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
            } finally {
                deleteConfirmed = false
            }
        }
    }


    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.report_details_screen),
        backButton = true,
        actions = isModifiable,
        onEditClick = { navController?.navigate("edit_report/$reportId") },
        onDeleteClick = { openAlertDialog.value = true }
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 0.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            reportDetails?.let { details ->
                val lastStatus = details.status!![details.status.size -1]

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp, 0.dp, 16.dp)
                ) {
                    Text(
                        text = details.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(0.dp, 8.dp, 16.dp, 0.dp)
                    )

                    ReportStatusChip(
                        lastStatus.status,
                        modifier = Modifier
                    )
                }

                TextField(
                    title = stringResource(R.string.creation_date_field),
                    content = details.createdAt!!.substring(0, 10)
                )
//                TextField( //TODO BE is returning incorrect date
//                    title = stringResource(R.string.last_status_update_field),
//                    content = lastStatus.updatedAt.substring(0, 10)
//                )

//                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    title = stringResource(R.string.type_dropdown),
                    content = enumValueToItem[details.type] ?: details.type
                )

                val description = details.description?.ifEmpty { null }
                TextField(
                    title = stringResource(R.string.description_field),
                    content = description,
                )

                if (details.imagePath != null) {
                    Text(
                        text = stringResource(R.string.photo_field),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    //val mockImagePath = R.drawable.broken_container2

                    AsyncImage(
                        model = generatePresignedUrl("garbi-app", details.imagePath!!),
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp, 280.dp)
                            .padding(0.dp, 8.dp)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    TextField(
                        title = stringResource(R.string.photo_field),
                        content = null
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    title = stringResource(R.string.container_id_field),
                    content = details.containerId
                )
                TextField(
                    title = stringResource(R.string.address_field),
                    content = details.address!!.convertToString()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //delete report dialog
            if (openAlertDialog.value) {
                com.garbi.garbi_recolection.common_components.AlertDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirmation = {
                        deleteConfirmed = true
                        openAlertDialog.value = false
                    },
                    dialogText = stringResource(R.string.delete_report_dialog_text),
                    confirmText = stringResource(R.string.delete_report_dialog_confirm)
                )
            }
        }
    }
}


@Composable
fun TextField(title: String, content: String?) {
    if (content != null) {
        Text(
            text = buildText(title, content),
            fontSize = 16.sp,
            modifier = Modifier.padding(0.dp, 8.dp)
        )
    } else {
        Text(
            text = buildText(title, "-"),
            fontSize = 16.sp,
            modifier = Modifier.padding(0.dp, 8.dp)
        )
    }
}

fun buildText(titleInBold: String, content: String): AnnotatedString {
    return buildAnnotatedString {
        append(titleInBold)
        addStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold), start = 0, end = titleInBold.length)
        append("\n")
        append(content)
    }
}


fun generatePresignedUrl(bucketName: String, objectKey: String): String {
    var preSignedUrl = ""
    val s3Client: AmazonS3Client?
    val credentials: BasicAWSCredentials?
    credentials = BasicAWSCredentials("","" )
    s3Client = AmazonS3Client(credentials)

    try {
        val expiration = Date()
        var expTimeMillis: Long = Instant.now().toEpochMilli()
        expTimeMillis += (1000 * 60 * 60 * 24 * 7).toLong()
        expiration.time = expTimeMillis

        val generateSignedUrlRequest = GeneratePresignedUrlRequest(bucketName, objectKey)
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration)
        val url: URL = s3Client.generatePresignedUrl(generateSignedUrlRequest)
        preSignedUrl = url.toString()
        println("getImagePreSignedUrl $preSignedUrl")
    }catch (illEx: IllegalArgumentException){
        println("error getImagePreSignedUrl $illEx")
    }

    return preSignedUrl
}
