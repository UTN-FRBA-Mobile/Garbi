package com.garbi.garbi_recolection.common_components

import androidx.compose.material.Text
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.garbi.garbi_recolection.ui.theme.BlueRevision
import com.garbi.garbi_recolection.ui.theme.GreenResolved
import com.garbi.garbi_recolection.ui.theme.OrangeNew
import com.garbi.garbi_recolection.ui.theme.RedRejected

@Composable
fun ReportStatusChip(status: String, modifier: Modifier) {
    var statusText = status
    if (status == "EN_REVISION") {
        statusText = "EN REVISIÓN"
    }

    val chipColors = when (statusText) {
        "RESUELTO" -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = GreenResolved,
        )

        "RECHAZADO" -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = RedRejected
        )

        "NUEVO" -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = OrangeNew,
        )

        "EN REVISIÓN" -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = BlueRevision
        )

        else -> SuggestionChipDefaults.suggestionChipColors(
            disabledContainerColor = RedRejected
        )
    }

    SuggestionChip(
        onClick = { },
        label = {
            Text(
                text = statusText,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        },
        colors = chipColors,
        border = null,
        modifier = modifier,
        enabled = false //adding this so that the chip does not display click animation on click
    )
}