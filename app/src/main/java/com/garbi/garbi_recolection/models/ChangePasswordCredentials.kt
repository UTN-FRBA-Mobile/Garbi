package com.garbi.garbi_recolection.models

data class ChangePasswordCredentials(
    var password1: String = "",
    var password2: String = "",
) {
    fun isNotEmpty(): Boolean {
        return password1.isNotEmpty() && password2.isNotEmpty()
    }
}