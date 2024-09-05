package com.garbi.garbi_recolection

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.garbi.garbi_recolection.services.Route
import com.google.gson.Gson

object RouteManager {
    private const val PREFS_NAME = "RoutePrefs"
    private const val KEY_ROUTE_AVAILABLE = "routeAvailable"
    private const val KEY_ROUTE_MODAL = "routeModal"
    private const val KEY_ROUTE_START = "routeStart"
    private const val KEY_CONTINUE_ROUTE_MODAL = "continueRouteModal"
    private const val KEY_ROUTE = "route"

    var routeAvailable by mutableStateOf(false)
        private set

    var routeModal by mutableStateOf(false)
        private set
    var routeStart by mutableStateOf("")
        private set
    var continueRouteModal by mutableStateOf(false)
        private set
    var route by mutableStateOf<Route?>(null)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        routeAvailable = prefs.getBoolean(KEY_ROUTE_AVAILABLE, false)
        routeModal = prefs.getBoolean(KEY_ROUTE_MODAL, false)
        routeStart = prefs.getString(KEY_ROUTE_START, "") ?: ""
        continueRouteModal = prefs.getBoolean(KEY_CONTINUE_ROUTE_MODAL, false)

        val routeJson = prefs.getString(KEY_ROUTE,null)
        route = if (routeJson != null) Gson().fromJson(routeJson, Route::class.java) else null

    }

    fun updateRoute(context: Context, value: Route) {
        route = value
        saveToPreferences(context)
    }
    fun updateRouteAvailable(context: Context, value: Boolean) {
        routeAvailable = value
        saveToPreferences(context)

    }

    fun updateRouteModal(context: Context, value: Boolean) {
        routeModal = value
        saveToPreferences(context)
    }

    fun updateRouteStart(context: Context, start: String) {
        routeStart = start
        saveToPreferences(context)

    }
    fun updateContinueRouteModal(context: Context, value: Boolean) {
        continueRouteModal = value
        saveToPreferences(context)

    }
    private fun saveToPreferences(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(KEY_ROUTE_AVAILABLE, routeAvailable)
            putBoolean(KEY_ROUTE_MODAL, routeModal)
            putString(KEY_ROUTE_START, routeStart)
            putBoolean(KEY_CONTINUE_ROUTE_MODAL, continueRouteModal)

            val routeJson = Gson().toJson(route)
            putString(KEY_ROUTE, routeJson)
            apply()
        }
    }

}
