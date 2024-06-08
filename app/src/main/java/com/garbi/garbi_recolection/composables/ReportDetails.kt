package com.garbi.garbi_recolection.composables

import AppScaffold
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.ui.theme.BlueRevision
import com.garbi.garbi_recolection.ui.theme.GreenResolved
import com.garbi.garbi_recolection.ui.theme.OrangeNew
import com.garbi.garbi_recolection.ui.theme.RedRejected

@Composable
fun ReportDetailsScreen (navController: NavController? = null, containerId: String?) {

    AppScaffold( // TODO agregar la back arrow
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.report_details_screen) + " " + containerId
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 16.dp)
        ) {
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp, 0.dp, 16.dp)
            ) {
                Text(
                    text = "tacho roto pq se mete gente y abre la tapa todo el tiempo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 0.dp, 16.dp, 0.dp)
                )
                ReportStatusChip("En revisión")
            }

            TextField(title = stringResource(R.string.creation_date_field), content = "02/05/2024")
            TextField(title = stringResource(R.string.type_dropdown), content = "contenedor en mal estado")
            TextField(
                title = stringResource(R.string.description_field),
                content = "el tacho de enfrente de mi casa está roto y no puedo tirar la basura entonces hay olor arreglenlooooo",
            )
            TextField(title = stringResource(R.string.photo_field), content = "")
            TextField(title = stringResource(R.string.container_id_field), content = "1234")
            TextField(title = stringResource(R.string.address_field), content = "av santa fe 11")
        }
    }
}



@Composable
fun TextField(title: String, content: String) {
    Text(
        text = buildText(title, content),
        fontSize = 16.sp,
        modifier = Modifier.padding(0.dp, 8.dp)
    )
}

fun buildText(titleInBold: String, content: String): AnnotatedString {
    return buildAnnotatedString {
        append(titleInBold)
        addStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold), start = 0, end = titleInBold.length)
        append("\n")
        append(content)
    }
}


@Composable
fun ReportStatusChip(status: String) {
    val chipColors = when (status) {
        "Resuelto" -> SuggestionChipDefaults.suggestionChipColors(
            containerColor = GreenResolved
        )

        "Rechazado" -> SuggestionChipDefaults.suggestionChipColors(
            containerColor = RedRejected
        )

        "Nuevo" -> SuggestionChipDefaults.suggestionChipColors(
            containerColor = OrangeNew
        )

        "En revisión" -> SuggestionChipDefaults.suggestionChipColors(
            containerColor = BlueRevision
        )

        else -> SuggestionChipDefaults.suggestionChipColors(
            containerColor = RedRejected
        )
    }

    SuggestionChip(
        onClick = { },
        label = {
            Text (
                text = status,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = chipColors,
        border = null
    )
}