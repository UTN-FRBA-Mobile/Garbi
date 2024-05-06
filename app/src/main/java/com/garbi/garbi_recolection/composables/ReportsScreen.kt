package com.garbi.garbi_recolection.composables

import AppScaffold
import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController? = null) {
    AppScaffold(navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.reports_screen)
    ) {
        Text("Esta es la pantalla de reportes",fontFamily = FontFamily.SansSerif)
    }

}