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
import com.garbi.garbi_recolection.core.ReportsAPI
import androidx.compose.foundation.Image
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.painterResource
import com.garbi.garbi_recolection.core.ReportState
import com.garbi.garbi_recolection.ui.theme.Green700
import com.garbi.garbi_recolection.ui.theme.Green900
import com.garbi.garbi_recolection.ui.theme.Orange600

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController? = null) {

    AppScaffold(
        navController = navController,
        title = stringResource(R.string.reports_screen),
        topBarVisible = true
    ) {
        Reports(ReportsAPI.MockReportApi.sampleReports())
    }

}



@Composable
fun ReportsRow(name: String, reportState: String, date: String, address: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(15.dp)
    ) {

        Column(
            Modifier
                .weight(2F)
                .padding(start = 10.dp)
        )


        {
            Text(
                text = name,
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )
            Box(
                modifier = Modifier
                    .background(if(reportState==ReportState.ACTIVO.toString()) Color.Red else if(reportState==ReportState.EN_PROGRESO.toString()) Orange600 else Green700)
                    .padding(5.dp)
                    .border(
                        shape = CircleShape,
                        width = 0.dp,
                        color = Color.LightGray,
                    )){
                        Text(text = if(reportState==ReportState.ACTIVO.toString()) "Activo" else if(reportState==ReportState.EN_PROGRESO.toString()) "En progreso" else "Resuelto",
                            color = Color.Black
                        )
                    }



            Text(text = date)

            Text(text = address)



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
fun Reports(reports: List<ReportData>) {

    LazyColumn {
            items(items = reports) { reportDataI ->
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(5.dp)
                        .border(
                            shape = CircleShape,
                            width = 0.dp,
                            color = Color.LightGray
                        ),
                        ) {
                            ReportsRow(reportDataI.description, reportDataI.reportState, reportDataI.date, reportDataI.address)

                    }

                        }

            }

}













