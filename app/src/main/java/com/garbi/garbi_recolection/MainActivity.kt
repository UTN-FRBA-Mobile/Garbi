package com.garbi.garbi_recolection

import Address
import MapsViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.garbi.garbi_recolection.composables.ChangePasswordScreen
import com.garbi.garbi_recolection.composables.CreateReportScreen
import com.garbi.garbi_recolection.composables.LoginForm
import com.garbi.garbi_recolection.composables.MapsScreen
import com.garbi.garbi_recolection.composables.ProfileScreen
import com.garbi.garbi_recolection.composables.ReportDetailsScreen
import com.garbi.garbi_recolection.composables.ReportsScreen
import com.garbi.garbi_recolection.services.RetrofitClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Composable
private fun App() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val mapsViewModel = remember { MapsViewModel() }
    NavHost(navController = navController, startDestination = "login") {
        composable("home") {
            MapsScreen(navController,mapsViewModel)
        }
        composable("reports") { ReportsScreen(navController)
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
            "report_details/{reportId}",
            arguments = listOf(
                navArgument("reportId") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")!!
            ReportDetailsScreen(navController, reportId)
        }

        composable("profile") { ProfileScreen(navController)
        }
        composable("login") { LoginForm(navController)
        }
        composable("logout") {
            RetrofitClient.deleteSession(context)
            LoginForm(navController)
        }
        composable("change_password") { ChangePasswordScreen(navController)
        }
    }
}


