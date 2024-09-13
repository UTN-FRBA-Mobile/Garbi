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
    private const val KEY_FIRST_ROUTE = "firstRoute"
    private const val KEY_CURRENT_STEP_INDEX = "currentStepIndex"
    private const val KEY_PREVIOUS_DISTANCE_TO_END = "previousDistanceToEnd"
    private const val KEY_ROUTE_ID = "routeId"

    var routeAvailable by mutableStateOf(false)
        private set

    var routeModal by mutableStateOf(false)
        private set
    var route by mutableStateOf<Route?>(null)
        private set
    var currentStepIndex by mutableStateOf(0)
        private set
    var previousDistanceToEnd by mutableStateOf(Double.POSITIVE_INFINITY)
        private set
    var routeId by mutableStateOf("")
        private set
    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        routeAvailable = prefs.getBoolean(KEY_ROUTE_AVAILABLE, false)
        routeModal = prefs.getBoolean(KEY_ROUTE_MODAL, false)
        currentStepIndex = prefs.getInt(KEY_CURRENT_STEP_INDEX, 0)
        previousDistanceToEnd = prefs.getString(KEY_PREVIOUS_DISTANCE_TO_END, Double.POSITIVE_INFINITY.toString())
            ?.toDouble()!!

        val routeJson = prefs.getString(KEY_ROUTE,null)
        route = if (routeJson != null) Gson().fromJson(routeJson, Route::class.java) else null


        routeId = prefs.getString(KEY_ROUTE_ID, "") ?: ""

    }

    fun updateRoute(context: Context, value: Route?) {
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

    fun updateCurrentStepIndex(context: Context, value: Int) {
        currentStepIndex = value
        saveToPreferences(context)
    }

    fun updatePreviousDistanceToEnd(context: Context, value: Double) {
        previousDistanceToEnd = value
        saveToPreferences(context)
    }


    fun updateRouteId(context: Context, value: String) {
        routeId = value
        saveToPreferences(context)

    }
    private fun saveToPreferences(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(KEY_ROUTE_AVAILABLE, routeAvailable)
            putBoolean(KEY_ROUTE_MODAL, routeModal)
            putInt(KEY_CURRENT_STEP_INDEX, currentStepIndex)
            putString(KEY_PREVIOUS_DISTANCE_TO_END, previousDistanceToEnd.toString())
            putString(KEY_ROUTE_ID, routeId)

            val routeJson = Gson().toJson(route)
            putString(KEY_ROUTE, routeJson)
            apply()
        }
    }

}
