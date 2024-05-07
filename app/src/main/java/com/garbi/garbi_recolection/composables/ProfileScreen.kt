package com.garbi.garbi_recolection.composables


import AppScaffold
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController? = null) {
    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.profile_screen)
    ) {
        Column (modifier = Modifier.fillMaxHeight())
        {
            Column (modifier = Modifier.weight(1f))
            {
                ProfileHeader(
                    image = painterResource(R.drawable.betular),
                    name = "Damian Betular")

                Text(
                    text = "damian_betular@cliba.com",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(16.dp, 0.dp)
                )
            }
            
            TextButton(
                onClick = {
                    if (navController != null) {
                        navController.navigate("login")
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.logout),
                    contentDescription = "logout button",
                    tint = Color.Red,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(id = R.string.logout_text),
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }

}

@Composable
fun ProfileHeader(image: Painter, name: String) {
    Row {
        Image(painter = image, contentDescription = "",
            modifier = Modifier.padding(16.dp))
        Column {
            Text(text = name,
                modifier = Modifier.padding(0.dp, 32.dp, 0.dp, 4.dp),
                color = Color(0xFF757575),
                fontWeight = FontWeight(1000),
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}