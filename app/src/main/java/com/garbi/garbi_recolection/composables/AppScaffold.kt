package com.garbi.garbi_recolection.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.ui.theme.Garbi_recolectionTheme
import com.garbi.garbi_recolection.ui.theme.Green900

@Composable
fun AppScaffold(
    title: String? = null,
    navController: NavController? = null,
    content: @Composable (PaddingValues) -> Unit) {
    val navigationIcon: (@Composable () -> Unit)? =
        if (navController?.previousBackStackEntry != null) {
            {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icons.Filled.ArrowBack
                }
            }
        } else null
    Garbi_recolectionTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = title ?: stringResource(id = R.string.app_name),
                            fontFamily = FontFamily(Font(R.font.montnaphasemibold))
                        )
                    },
                    navigationIcon = navigationIcon,
                    backgroundColor = Green900,
                )
            },
            content = {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    content(it)
                }
            }
        )
    }
}
