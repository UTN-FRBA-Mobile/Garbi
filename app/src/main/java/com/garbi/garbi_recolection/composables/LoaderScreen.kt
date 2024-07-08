package com.garbi.garbi_recolection.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.ui.theme.*

@Composable
fun LoaderScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.loading_screen), color = White)
        }
    }
}