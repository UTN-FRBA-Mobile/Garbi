package com.garbi.garbi_recolection.composables

import AppScaffold
import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateReportScreen(navController: NavController? = null) {
    var titleText by rememberSaveable { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    val items = listOf("Contenedor en mal estado", "Contenedor sucio", "Basura en la calle", "Contenedor faltante", "Otro")

    var descriptionText by rememberSaveable { mutableStateOf("") }
    var containerIdText by rememberSaveable { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }


    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.create_report_screen)
    ) {
        Column (modifier = Modifier.fillMaxHeight())
        {
            TextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 24.dp, 24.dp, 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 8.dp)
            ) {
                TextField(
                    value = selectedText,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Tipo de problema") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedText = item
                                expanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            TextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                label = { Text("Descripción del problema") },
                singleLine = false,
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 8.dp, 24.dp, 8.dp)
            )

            TextField(
                value = containerIdText,
                onValueChange = {
                    containerIdText = it
                    isError = it.isNotEmpty() && !it.matches(Regex("^[0-9]*$"))
                },
                label = { Text("Id del contendor (6 dígitos)") },
                singleLine = true,
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 8.dp, 24.dp, 8.dp)
            )
            if (isError) {
                Text(
                    text = "Solo números pueden ser ingresados",
                    //FINISH THIS ERROR PART. la doc oficial decia cosas creo. poner tmb q se abra teclado numérico de una
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }
}