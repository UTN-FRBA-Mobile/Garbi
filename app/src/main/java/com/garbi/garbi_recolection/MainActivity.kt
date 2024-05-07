package com.garbi.garbi_recolection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.garbi.garbi_recolection.composables.LoginForm
import com.garbi.garbi_recolection.composables.MapsScreen
import com.garbi.garbi_recolection.composables.ProfileScreen
import com.garbi.garbi_recolection.composables.ReportsScreen

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
    NavHost(navController = navController, startDestination = "login") {
        composable("home") { MapsScreen(navController)
        }
        composable("reports") { ReportsScreen(navController)
        }
        composable("profile") { ProfileScreen(navController)
        }
        composable("login") { LoginForm(navController)
        }
    }
}


