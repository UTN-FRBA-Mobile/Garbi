package com.garbi.garbi_recolection.composables


import AppScaffold
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChangePasswordScreen(navController: NavController? = null) {

    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.change_password_screen)
    ) {

    }

}
