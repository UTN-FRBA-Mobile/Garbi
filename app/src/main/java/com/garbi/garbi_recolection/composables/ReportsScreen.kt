package com.garbi.garbi_recolection.composables

import AppScaffold
import ReportsViewModel
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.common_components.ReportStatusChip
import com.garbi.garbi_recolection.services.RetrofitClient
import com.garbi.garbi_recolection.ui.theme.LightGray
import com.garbi.garbi_recolection.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReportsScreen(navController: NavController? = null, reportsViewModel: ReportsViewModel) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        reportsViewModel.loadReports(context, navController!!)
    }

    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.reports_screen)
    ) {
        if(reportsViewModel.isLoading){
            LoaderScreen()
        } else {
            if (reportsViewModel.reports.isEmpty()) {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.empty_reports),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.empty_reports_description),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn {
                    items(items = reportsViewModel.reports) { reportDataI ->
                        Box(
                            modifier = Modifier.background(White)
                        ) {
                            ReportsRow(
                                reportDataI.title,
                                reportDataI.status!![reportDataI.status.size -1].status,
                                reportDataI.createdAt!!.substring(0, 10),
                                reportDataI.address!!.convertToString(),
                                navController,
                                reportDataI._id!!
                            )
                            Divider(
                                color = LightGray,
                                thickness = 1.dp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun ReportsRow(title: String, status: String, creationDate: String, address: String, navController: NavController? = null, reportId: String) {
    // for Delete report functionality
    val context = LocalContext.current
    val openAlertDialog = remember { mutableStateOf(false) }
    var deleteConfirmed by remember { mutableStateOf(false) }

    if (deleteConfirmed) {
        LaunchedEffect(reportId) {
            val service = RetrofitClient.reportService

            try {
                val response = withContext(Dispatchers.IO) { service.deleteReport(reportId) }
                println("response: $response")
                if (response.isSuccessful) {
                    navController?.navigate("reports") //todo hará que se recargue? sino hay q hacer algo para q se actualice la pantalla y no se vea el reporte
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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { navController?.navigate("report_details/$reportId") }
    ) {
        Column(
            Modifier
                .weight(2F)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = title,
                style = TextStyle( fontWeight = FontWeight.Bold )
            )

            Text(
                text = creationDate,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            Text(
                text = address,
            )

            ReportStatusChip(
                status = status,
                modifier = Modifier.defaultMinSize(minWidth = 10.dp, minHeight = 16.dp)
            )
        }

        var iconButtonsEnabled = false
        if (status == stringResource(R.string.status_new)) {
            iconButtonsEnabled = true
        }
        Column(
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Row {
                EditReportItem(
                    onClick = { navController?.navigate("edit_report/$reportId") },
                    enabled = iconButtonsEnabled
                )
                DeleteReportItem(
                    onClick = { openAlertDialog.value = true },
                    enabled = iconButtonsEnabled
                )
            }
        }

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

@Composable
private fun EditReportItem(
    onClick: () -> Unit,
    enabled: Boolean
){
    IconButton(
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Filled.Create,
            contentDescription = stringResource(R.string.edit_button),
        )
    }
}

@Composable
private fun DeleteReportItem(
    onClick: () -> Unit,
    enabled: Boolean
){
    IconButton(
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.delete_button),
        )
    }
}