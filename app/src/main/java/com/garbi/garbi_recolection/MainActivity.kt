package com.garbi.garbi_recolection

import Address
import MapsViewModel
import ReportsViewModel
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.garbi.garbi_recolection.composables.*
import com.garbi.garbi_recolection.services.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


private lateinit var fusedLocationClient: FusedLocationProviderClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            App(fusedLocationClient)
        }
    }
}

@Composable
private fun App(fusedLocationClient: FusedLocationProviderClient) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val mapsViewModel = remember { MapsViewModel() }
    val reportViewModel = remember { ReportsViewModel() }
    var startDestination by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        val token = RetrofitClient.getToken(context)
        startDestination = if (token == null || !RetrofitClient.isTokenValid()) "login" else "home"
        Log.v("página inicial", startDestination ?: "null")

    }

    if (startDestination != null) {
        NavHost(navController = navController, startDestination = startDestination!!) {
            composable("home") {
                MapsScreen(navController, mapsViewModel, fusedLocationClient)
            }

            composable("reports") {
                ReportsScreen(navController, reportViewModel)
            }

            composable(
                "create_report/{containerId}/{street}/{number}/{neighborhood}",
                arguments = listOf(
                    navArgument("containerId") { type = NavType.StringType },
                    navArgument("street") { type = NavType.StringType },
                    navArgument("number") { type = NavType.StringType },
                    navArgument("neighborhood") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val containerId = backStackEntry.arguments?.getString("containerId")
                val street = backStackEntry.arguments?.getString("street")
                val number = backStackEntry.arguments?.getString("number")
                val neighborhood = backStackEntry.arguments?.getString("neighborhood")
                val address = Address(
                    street = street.toString(),
                    number = number.toString(),
                    neighborhood = neighborhood.toString()
                )
                CreateReportScreen(navController, containerId, address)
            }

            composable(
                "edit_report/{reportId}",
                arguments = listOf(
                    navArgument("reportId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId")!!
                EditReportScreen(navController, reportId)
            }

            composable(
                "report_details/{reportId}",
                arguments = listOf(
                    navArgument("reportId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId")!!
                ReportDetailsScreen(navController, reportId)
            }

            composable("profile") {
                ProfileScreen(navController)
            }
            composable("login") {
                LoginScreen(navController)
            }
            composable("logout") {
                RetrofitClient.deleteSession(context)
                RetrofitClient.deleteToken(context)
                navController.navigate("login") {
                    popUpTo("logout") { inclusive = true }
                }
            }
            composable("change_password") {
                ChangePasswordScreen(navController)
            }
        }
    } else {
        LoadingScreen()
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
    }
}
