package com.garbi.garbi_recolection.composables

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.MainActivity
import com.garbi.garbi_recolection.fields.LoginField
import com.garbi.garbi_recolection.fields.PasswordField
import com.garbi.garbi_recolection.models.Credentials
import com.garbi.garbi_recolection.ui.theme.Garbi_recolectionTheme
import com.garbi.garbi_recolection.ui.theme.Green900

@Composable
fun LoginForm(navController: NavController? = null) {
    Garbi_recolectionTheme {
        Surface {
            var credentials by remember { mutableStateOf(Credentials()) }
            val context = LocalContext.current

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
            ) {
                LoginField(
                    value = credentials.login,
                    onChange = { data -> credentials = credentials.copy(login = data) },
                    modifier = Modifier.fillMaxWidth()
                )
                PasswordField(
                    value = credentials.pwd,
                    onChange = { data -> credentials = credentials.copy(pwd = data) },
                    submit = {
                        if (!checkCredentials(credentials, context)) credentials = Credentials()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                LabeledCheckbox(
                    label = "Quiero recordar mi usuario",
                    onCheckChanged = {
                        credentials = credentials.copy(remember = !credentials.remember)
                    },
                    isChecked = credentials.remember
                )
                Spacer(modifier = Modifier.height(20.dp))
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
            .padding(4.dp)
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