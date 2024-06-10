package com.garbi.garbi_recolection.composables

import AppScaffold
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
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.garbi.garbi_recolection.models.Report
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.services.RetrofitClient
import com.garbi.garbi_recolection.ui.theme.BlueRevision
import com.garbi.garbi_recolection.ui.theme.GreenResolved
import com.garbi.garbi_recolection.ui.theme.OrangeNew
import com.garbi.garbi_recolection.ui.theme.RedRejected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ReportDetailsScreen (navController: NavController? = null, reportId: String) {
    val scrollState = rememberScrollState()

    //For the edit and delete buttons in the TopBar
    var isModifiable by remember { mutableStateOf(false) }

    var reportDetails: Report? by remember { mutableStateOf(null) }
    LaunchedEffect(reportId) {
        val service = RetrofitClient.reportService
        try {
            val response = withContext(Dispatchers.IO) { service.getReport(reportId) }
            reportDetails = response
            println("reportDetails: $reportDetails")

            val listOfStatus = reportDetails!!.status
            if (listOfStatus!![listOfStatus.size -1].status in listOf("NUEVO", "EN REVISIÓN")) { //TODO dedidir q queremos. si hacemos esto, al supervisor habria q avisarle q el recolector hizo tal cosa.
                isModifiable = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val items =  stringArrayResource(R.array.report_items).toList()
    val enumValueToItem = mapOf(
        "CONTENEDOR_ROTO" to items[0],
        "CONTENEDOR_SUCIO" to items[1],
        "BASURA_EN_LA_CALLE" to items[2],
        "CONTENEDOR_FALTANTE" to items[3],
        "OTROS" to items[4]
    )


    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.report_details_screen),
        backButton = true,
        actions = isModifiable,
        onEditClick = { /*TODO*/ },
        onDeleteClick = { /*TODO*/ }
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 0.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            reportDetails?.let { details ->
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

                    val listOfStatus = details.status!!
                    ReportStatusChip(listOfStatus[listOfStatus.size -1].status, null)
                }

                TextField(
                    title = stringResource(R.string.creation_date_field),
                    content = details.createdAt!!.substring(0, 10)
                )
                TextField(
                    title = stringResource(R.string.type_dropdown),
                    content = enumValueToItem[details.type] ?: details.type
                )
                TextField(
                    title = stringResource(R.string.description_field),
                    content = details.description,
                )

                if (details.imagePath != null) {
                    Text(
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
//                ?: run {
//                // Handle loading or error state
//                Text(text = "Loading...")
//            }

            Spacer(modifier = Modifier.height(16.dp))
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


@Composable
fun ReportStatusChip(status: String, modifier: Modifier?) {
    val chipColors = when (status) {
        "RESUELTO" -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = GreenResolved,
        )

        "RECHAZADO" -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = RedRejected
        )

        "NUEVO" -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = OrangeNew,
        )

        "EN REVISIÓN" -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = BlueRevision
        )

        else -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = RedRejected
        )
    }

    SuggestionChip(
        onClick = { },
        label = {
            Text(
                text = status,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        },
        colors = chipColors,
        border = null,
        modifier = modifier!!,
        enabled = false //adding this so that the chip does not display click animation on click
    )
}