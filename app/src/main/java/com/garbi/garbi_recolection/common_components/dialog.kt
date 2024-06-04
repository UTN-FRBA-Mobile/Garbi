package com.garbi.garbi_recolection.common_components

import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.ui.theme.Green900

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogText: String,
    confirmText: String
) {
    AlertDialog(
        text = {
            Text(
                text = dialogText,
                color = Color.Black
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation() }
            ) {
                Text(color = Green900, text = confirmText)
            }
        },
        containerColor = Color.White,
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text(color = Green900, text = stringResource(R.string.dialog_dismiss))
            }
        }
    )
}