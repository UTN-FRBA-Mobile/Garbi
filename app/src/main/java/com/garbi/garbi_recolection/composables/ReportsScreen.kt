package com.garbi.garbi_recolection.composables

import AppScaffold
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun ReportsRow(name: String, reportState: String, date: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(10.dp)
    ) {
        Column(
            Modifier
                .weight(2F)
                .padding(start = 10.dp)
        ) {
            Text(
                text = name,
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(text = reportState)

            Text(text = date)


        }
    }
}

@Composable
fun Report(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(10.dp)
    ) {
        Column(
            Modifier
                .weight(2F)
                .padding(start = 10.dp)
        ) {
                    Text(
                        text = name,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(text = name)
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
                        .border(
                            shape = CircleShape,
                            width = 0.dp,
                            color = Color.LightGray
                                ),
                        ) {
                            ReportsRow(reportDataI.description, reportDataI.reportState, reportDataI.date)


                        }

                    }
                }
            }











