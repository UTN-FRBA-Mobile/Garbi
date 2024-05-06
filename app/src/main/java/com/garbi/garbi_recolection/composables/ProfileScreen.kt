package com.garbi.garbi_recolection.composables


import AppScaffold
import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController? = null) {
    AppScaffold(navController = navController, topBarVisible = true) {
        Text("Esta es la pantalla del perfil",fontFamily = FontFamily.SansSerif)
    }

}