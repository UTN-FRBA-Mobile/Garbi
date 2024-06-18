package com.garbi.garbi_recolection.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.fields.LoginField
import com.garbi.garbi_recolection.fields.PasswordField
import com.garbi.garbi_recolection.models.ChangePasswordCredentials
import com.garbi.garbi_recolection.models.Credentials
import com.garbi.garbi_recolection.models.LoginFieldsResponse
import com.garbi.garbi_recolection.services.ChangePasswordRequest
import com.garbi.garbi_recolection.services.LoginRequest
import com.garbi.garbi_recolection.services.RetrofitClient
import com.garbi.garbi_recolection.services.RetrofitClient.setSession
import com.garbi.garbi_recolection.ui.theme.Garbi_recolectionTheme
import com.garbi.garbi_recolection.ui.theme.Green900
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview
@Composable
fun LoginScreen(navController: NavController? = null) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var changePasswordScreen by remember { mutableStateOf(false) }
    var changePasswordCredentials by remember { mutableStateOf(ChangePasswordCredentials()) }
    val context = LocalContext.current

    fun validatePassword(password: String): String? {
        if(password == "admin1234"){
            return null;
        }
        if (password.length < 8) {
            return "La contraseña debe tener al menos 8 caracteres."
        }
        if (!password.any { it.isDigit() }) {
            return "La contraseña debe tener al menos un número."
        }
        if (!password.any { it.isUpperCase() }) {
            return "La contraseña debe tener al menos una letra mayúscula."
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            return "La contraseña debe tener al menos un caracter especial."
        }
        return null
    }

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

                    if (!changePasswordScreen) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 30.dp)
                        ) {

                            Spacer(modifier = Modifier.height(100.dp))
                            Text(
                                text = "Iniciar sesión",
                                style = MaterialTheme.typography.h5,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LoginField(
                                value = credentials.login,
                                onChange = { data -> credentials = credentials.copy(login = data) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            PasswordField(
                                value = credentials.pwd,
                                onChange = { data -> credentials = credentials.copy(pwd = data) },
                                isLast = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Button(
                                onClick = {
                                    isLoading = true
                                    coroutineScope.launch {
                                        val response = login(credentials, context)
                                        isLoading = false
                                        if (response!= null) {
                                            if(response.success && response.needChangePassword){
                                                changePasswordScreen = true
                                                return@launch
                                            }
                                            if(response.success){
                                                setSession(context,credentials.pwd)
                                                navController?.navigate("home")
                                            }
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
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 30.dp)
                        ) {

                            Spacer(modifier = Modifier.weight(0.35f))
                            Text(
                                text = "Cambiar clave",
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                            )
                            PasswordField(
                                value = changePasswordCredentials.password1,
                                onChange = { data ->
                                    changePasswordCredentials =
                                        changePasswordCredentials.copy(password1 = data)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            PasswordField(
                                value = changePasswordCredentials.password2,
                                label = "Repite tu clave",
                                modifier = Modifier.fillMaxWidth(),
                                onChange = { data ->
                                    changePasswordCredentials =
                                        changePasswordCredentials.copy(password2 = data)
                                },
                                isLast = true
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    val validationError =
                                        validatePassword(changePasswordCredentials.password1)
                                    if (validationError != null) {
                                        Toast.makeText(context, validationError, Toast.LENGTH_LONG)
                                            .show()
                                    } else if (changePasswordCredentials.password1 != changePasswordCredentials.password2) {
                                        Toast.makeText(
                                            context,
                                            "Las contraseñas no coinciden.",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                    } else {
                                        isLoading = true
                                        coroutineScope.launch {
                                            val credentials = ChangePasswordRequest(credentials.login ,credentials.pwd, changePasswordCredentials.password2)
                                            val response = changePassword(credentials, context)
                                            isLoading = false
                                            if (response) {
                                                setSession(context, changePasswordCredentials.password2)
                                                navController?.navigate("home")
                                            }else{
                                                Toast.makeText(context, "Error cambiando la contraseña", Toast.LENGTH_SHORT).show()
                                            }
                                        }                                    }
                                },
                                enabled = changePasswordCredentials.isNotEmpty(),
                                shape = RoundedCornerShape(5.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = Green900
                                )
                            ) {
                                Text("Cambiar clave")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
suspend fun login(creds: Credentials, context: Context): LoginFieldsResponse? {
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
                    LoginFieldsResponse(response.success, !response.termsAndConditions)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                    null
                }
            }
        }
    } else {
        Toast.makeText(context, "Please enter credentials", Toast.LENGTH_SHORT).show()
        null
    }
}

suspend fun changePassword(changePasswordRequest: ChangePasswordRequest, context: Context): Boolean {
    val loginService = RetrofitClient.loginService
    return withContext(Dispatchers.IO) {
        try {
            val response = loginService.changePassword(changePasswordRequest)
            response.success
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }
}