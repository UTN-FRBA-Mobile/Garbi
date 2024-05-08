package com.garbi.garbi_recolection.composables

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.MainActivity
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.fields.LoginField
import com.garbi.garbi_recolection.fields.PasswordField
import com.garbi.garbi_recolection.models.Credentials
import com.garbi.garbi_recolection.ui.theme.Garbi_recolectionTheme
import com.garbi.garbi_recolection.ui.theme.Green900

@Preview
@Composable
fun LoginForm(navController: NavController? = null) {
    Garbi_recolectionTheme {
        Surface {
            var credentials by remember { mutableStateOf(Credentials()) }
            val context = LocalContext.current
            Column {
                Spacer(modifier = Modifier.height(40.dp))
                Image(
                    painter = painterResource(id = R.mipmap.garbi_logo_recortado),
                    contentDescription = "Garbi logo",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                ) {

                    Spacer(modifier = Modifier.height(100.dp))
                    LoginField(
                        value = credentials.login,
                        onChange = { data -> credentials = credentials.copy(login = data) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PasswordField(
                        value = credentials.pwd,
                        onChange = { data -> credentials = credentials.copy(pwd = data) },
                        submit = {
                            if (!checkCredentials(credentials, context)) credentials = Credentials()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LabeledCheckbox(
                        label = "Quiero recordar mi usuario",
                        onCheckChanged = {
                            credentials = credentials.copy(remember = !credentials.remember)
                        },
                        isChecked = credentials.remember,

                        )
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            // if (!checkCredentials(credentials, context)) credentials = Credentials()
                            if (navController != null) {
                                navController.navigate("home")
                            }
                        },
                        enabled = credentials.isNotEmpty(),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Green900
                        )
                    ) {
                        Text("Inciar sesión")
                    }
                }
            }
        }
    }
}

@Composable
fun LabeledCheckbox(
    label: String,
    onCheckChanged: () -> Unit,
    isChecked: Boolean
) {
    Row(
        Modifier
            .clickable(
                onClick = onCheckChanged
            )
    ) {
        Checkbox(checked = isChecked, onCheckedChange = null)
        Spacer(Modifier.size(6.dp))
        Text(label)
    }
}

fun checkCredentials(creds: Credentials, context: Context): Boolean {
    if (creds.isNotEmpty() && creds.login == "admin") {
        context.startActivity(Intent(context, MainActivity::class.java))
        (context as Activity).finish()
        return true
    } else {
        Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show()
        return false
    }
}