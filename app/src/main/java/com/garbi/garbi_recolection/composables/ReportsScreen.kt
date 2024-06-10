package com.garbi.garbi_recolection.composables

import AppScaffold
import android.annotation.SuppressLint
import android.view.View
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.garbi.garbi_recolection.core.ReportData
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.garbi.garbi_recolection.core.ReportState
import com.garbi.garbi_recolection.models.Report
import com.garbi.garbi_recolection.models.ReportResponse
import com.garbi.garbi_recolection.services.RetrofitClient
import com.garbi.garbi_recolection.ui.theme.Green700
import com.garbi.garbi_recolection.ui.theme.Green900
import com.garbi.garbi_recolection.ui.theme.LightGreenStateBackground
import com.garbi.garbi_recolection.ui.theme.Orange600
import com.garbi.garbi_recolection.ui.theme.lightOrangeStateBackground
import com.garbi.garbi_recolection.ui.theme.lightRedStateBackground
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



@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
fun ReportsScreen() {
    val context = LocalContext.current
    var userId: String = "";
    var reportDetails: ReportResponse? by remember { mutableStateOf(null) }

    LaunchedEffect(context) {
        val userDetails = RetrofitClient.getSession(context)
        if (userDetails != null) {
            userId=userDetails._id
        };
    }

    LaunchedEffect(userId) {
        val service = RetrofitClient.reportService
        try {
            val response = withContext(Dispatchers.IO) { service.getReports(userId) }
            reportDetails = response



        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    val listOdReports =  listOf(
        ReportData(
            description = reportDetails!!.documents[0].description.toString(),
            reportState =  reportDetails!!.documents[0].status.toString(),
            date =  reportDetails!!.documents[0].createdAt!!.substring(0, 10),
            address =  reportDetails!!.documents[0].address!!.convertToString()


        )
    )

}



@Composable
fun ReportsRow(name: String, reportState: String, date: String, address: String, navController: NavController? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            navController?.navigate("report_details/66650be4edce87678adab6b1") }
    ) {

        Column(
            Modifier
                .weight(2F)
                .padding(start = 10.dp)
        )


        {
            Text(
                modifier = Modifier.padding(2.dp),
                text = name,
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )


            Text(text = date,
                    modifier = Modifier.padding(2.dp)
            )

            Text(text = address,
                    modifier = Modifier.padding(2.dp)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (reportState == ReportState.ACTIVO.toString()) lightRedStateBackground else if (reportState == ReportState.EN_PROGRESO.toString()) lightOrangeStateBackground else LightGreenStateBackground)
                              ){
                Text(text = if(reportState==ReportState.ACTIVO.toString()) "Activo" else if(reportState==ReportState.EN_PROGRESO.toString()) "En progreso" else "Resuelto",
                    Modifier
                        .padding(3.dp),
                    color = Color.Black,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
            }



        }

        Column(
        ) {
            EditReportItem(
                onClick = {  })
            DeleteReportItem(
                onClick = {  })

        }


    }
}



@Composable
fun Reports(reports: List<ReportData>, navController: NavController? = null) {

    LazyColumn {
            items(items = reports) { reportDataI ->
                Box(
                    modifier = Modifier
                        .background(Color.White)

                    ) {
                            ReportsRow(reportDataI.description, reportDataI.reportState, reportDataI.date, reportDataI.address, navController)
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    }

                        }

            }

}













