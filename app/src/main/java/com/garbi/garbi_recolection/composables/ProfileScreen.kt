package com.garbi.garbi_recolection.composables


import AppScaffold
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.ui.theme.Green900
import com.garbi.garbi_recolection.ui.theme.LightGreen
import com.garbi.garbi_recolection.ui.theme.LightGreenBackground
import com.garbi.garbi_recolection.common_components.*
import com.garbi.garbi_recolection.services.RetrofitClient
import com.garbi.garbi_recolection.services.UserDetails


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController? = null) {
    val openAlertDialog = remember { mutableStateOf(false) }
    var switchState = remember { mutableStateOf(false) }
    val context = LocalContext.current


    var userDetails by remember { mutableStateOf<UserDetails?>(null) }

    LaunchedEffect(context) {
        userDetails = RetrofitClient.getSession(context, navController!!)
        Log.v("user", userDetails.toString())
    }


    AppScaffold(
        navController = navController,
        topBarVisible = true,
        title = stringResource(R.string.profile_screen)
    ) {

        userDetails?.let {

        Column (modifier = Modifier.fillMaxHeight())
        {
            Column (modifier = Modifier.weight(1f))
            {
                ProfileHeader(
                    image = painterResource(R.drawable.betular),
                    name = userDetails!!.name + " " + userDetails!!.surname
                )

                Text(
                    text = userDetails!!.email,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(16.dp, 0.dp)
                )

                TextButton(
                    onClick = { navController?.navigate("change_password") },
                    modifier = Modifier.padding(16.dp, 32.dp, 16.dp, 4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lock_reset),
                        contentDescription = "change password button",
                        tint = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.change_password_button),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
            
            TextButton(
                onClick = { openAlertDialog.value = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.logout),
                    contentDescription = "logout button",
                    tint = Color.Red,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(id = R.string.logout_button),
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }

            if (openAlertDialog.value) {
                AlertDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirmation = {
                        navController?.navigate("logout")
                        openAlertDialog.value = false
                    },
                    dialogText = stringResource(R.string.logout_dialog_text),
                    confirmText = stringResource(R.string.logout_dialog_confirm)
                )
            }
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

@Composable
fun Switch(checked: MutableState<Boolean>) {
    Switch(
        checked = checked.value,
        onCheckedChange = {
            checked.value = it
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Green900,
            checkedTrackColor = LightGreenBackground,
            uncheckedThumbColor = LightGreen,
            uncheckedTrackColor = LightGreenBackground,
            uncheckedBorderColor = LightGreen
        )
    )
}
