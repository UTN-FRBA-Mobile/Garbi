package com.garbi.garbi_recolection.composables
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var token: String? by mutableStateOf("")

    fun updateToken(newToken: String) {
        token = newToken
    }

}