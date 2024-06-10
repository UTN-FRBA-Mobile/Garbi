package com.garbi.garbi_recolection.composables

import AppScaffold
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.garbi.garbi_recolection.models.Report
import com.garbi.garbi_recolection.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
private fun EditReportItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Create,
            contentDescription = stringResource(R.string.edit_button),
            tint = Color.LightGray
        )
    }
}

@Composable
private fun DeleteReportItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.delete_button),
            tint = Color.LightGray
        )
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ReportsScreen(navController: NavController? = null) {
    val context = LocalContext.current
    var userId by remember { mutableStateOf("") }
    val reports = remember { mutableStateOf<List<Report>>(emptyList()) }

    LaunchedEffect(context) {
        //Get user id
        val userDetails = RetrofitClient.getSession(context)
        userId = userDetails?._id ?: ""

        //Get reports
        val service = RetrofitClient.reportService
        try {
            val response = withContext(Dispatchers.IO) { service.getReports(userId) }
            reports.value = response.documents
            println("reports: " + reports)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.reports_screen)
    ) {
        if (reports.value.isEmpty()) { //TODO ver si es solo ==null o si vacio tmb
            Text(text = "no hay reportes")
        } else {
            LazyColumn {
                items(items = reports.value) { reportDataI ->
                    Box(
                        modifier = Modifier.background(Color.White)
                    ) {
                        ReportsRow(
                            reportDataI.title,
                            reportDataI.status!![0].status,
                            reportDataI.createdAt!!.substring(0, 10),
                            reportDataI.address!!.convertToString(),
                            navController
                        )
                        Divider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ReportsRow(name: String, reportState: String, date: String, address: String, navController: NavController? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { navController?.navigate("report_details/66650be4edce87678adab6b1") }
    ) {
        Column(
            Modifier
                .weight(2F)
                .padding(start = 10.dp)
        ) {
            Text(
                modifier = Modifier.padding(2.dp),
                text = name,
                style = TextStyle( fontWeight = FontWeight.Bold )
            )

            Text(
                text = date,
                modifier = Modifier.padding(2.dp)
            )

            Text(
                text = address,
                modifier = Modifier.padding(2.dp)
            )

            ReportStatusChip(reportState, modifier = Modifier.defaultMinSize(minWidth = 10.dp, minHeight = 16.dp))
        }

        Column(
        ) {
            EditReportItem( onClick = {  })
            DeleteReportItem( onClick = {  })
        }
    }
}
