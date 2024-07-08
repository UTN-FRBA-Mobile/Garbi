package com.garbi.garbi_recolection

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object RouteManager {
    var routeAvailable by mutableStateOf(false)
        private set

    var routeModal by mutableStateOf(false)
        private set

    fun updateRouteAvailable(value: Boolean) {
        routeAvailable = value
    }

    fun updateRouteModal(value: Boolean) {
        routeModal = value
    }
}