package com.garbi.garbi_recolection.composables

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.MainActivity
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.fields.LoginField
import com.garbi.garbi_recolection.fields.PasswordField
import com.garbi.garbi_recolection.models.Credentials
import com.garbi.garbi_recolection.services.LoginRequest
import com.garbi.garbi_recolection.services.RetrofitClient
import com.garbi.garbi_recolection.ui.theme.Garbi_recolectionTheme
import com.garbi.garbi_recolection.ui.theme.Green900
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview
@Composable
fun LoginForm(navController: NavController? = null) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Garbi_recolectionTheme {
        Surface {
            var credentials by remember { mutableStateOf(Credentials()) }
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cargando...", color = Color.White)
                    }
                }
            } else{
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
                                credentials = Credentials()
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
                                isLoading = true
                                coroutineScope.launch {
                                    val success = checkCredentials(credentials, context)
                                    isLoading = false
                                    if (success) {
                                        navController?.navigate("home")
                                    }
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
                            Text("Iniciar sesión")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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

suspend fun checkCredentials(creds: Credentials, context: Context): Boolean {
    val loginService = RetrofitClient.loginService
    return if (creds.isNotEmpty()) {
        withContext(Dispatchers.IO) {
            try {
                val response = loginService.login(LoginRequest(creds.login, creds.pwd))
                withContext(Dispatchers.Main) {
                    if (response.success) {
                        RetrofitClient.setToken(context, response.token)
                    } else {
                        Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
                    }
                    response.success
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    } else {
        Toast.makeText(context, "Please enter credentials", Toast.LENGTH_SHORT).show()
        false
    }
}