package com.garbi.garbi_recolection

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object RouteManager {
    private const val PREFS_NAME = "RoutePrefs"
    private const val KEY_ROUTE_AVAILABLE = "routeAvailable"
    private const val KEY_ROUTE_MODAL = "routeModal"
    private const val KEY_ROUTE_WAYPOINTS = "routeWaypoints"
    private const val KEY_ROUTE_DESTINATION = "routeDestination"

    var routeAvailable by mutableStateOf(false)
        private set

    var routeModal by mutableStateOf(false)
        private set

    var routeWaypoints by mutableStateOf("")
        private set
    var routeDestination by mutableStateOf("")
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        routeAvailable = prefs.getBoolean(KEY_ROUTE_AVAILABLE, false)
        routeModal = prefs.getBoolean(KEY_ROUTE_MODAL, false)
        routeWaypoints = prefs.getString(KEY_ROUTE_WAYPOINTS, "") ?: ""
        routeDestination = prefs.getString(KEY_ROUTE_DESTINATION, "") ?: ""

    }

    fun updateRouteAvailable(context: Context, value: Boolean) {
        routeAvailable = value
        saveToPreferences(context)

    }

    fun updateRouteModal(context: Context, value: Boolean) {
        routeModal = value
        saveToPreferences(context)
    }

    fun updateRouteWaypoints(context: Context, waypoints: String) {
        routeWaypoints = waypoints
        saveToPreferences(context)
    }

    fun updateRouteDestination(context: Context, destination: String) {
        routeDestination = destination
        saveToPreferences(context)

    }
    private fun saveToPreferences(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(KEY_ROUTE_AVAILABLE, routeAvailable)
            putBoolean(KEY_ROUTE_MODAL, routeModal)
            putString(KEY_ROUTE_WAYPOINTS, routeWaypoints)
            putString(KEY_ROUTE_DESTINATION, routeDestination)
            apply()
        }
    }

}
