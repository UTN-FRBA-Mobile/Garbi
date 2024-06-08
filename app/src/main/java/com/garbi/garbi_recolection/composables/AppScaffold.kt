import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.ui.theme.Garbi_recolectionTheme
import com.garbi.garbi_recolection.ui.theme.Green900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String? = null,
    navController: NavController? = null,
    topBarVisible : Boolean,
    content: @Composable (PaddingValues) -> Unit
) {
    val navigationIcon: (@Composable () -> Unit)? = //creo q está mal pq si navego a la lista de Reportes para mi no tiene q aparecer la flecha
        if (navController?.previousBackStackEntry != null) {
            {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        } else null

    val navigationBarBackgroundColor = Color.White
    val topBarBackgroundColor = Green900
    val activeColor = Green900
    val inactiveColor = Color.LightGray


    val currentDestination = navController?.currentDestination?.route

    Garbi_recolectionTheme {
        Scaffold(
            topBar =  {
                if (topBarVisible) {
                    val navigationBarBackgroundColor  = Color.White
                    val topBarBackgroundColor = Green900
                    TopAppBar(
                        backgroundColor = topBarBackgroundColor,
                        contentColor = Color.White,
                        title = {
                            Text(text = title ?: stringResource(id = R.string.app_name))
                        }
                    )
                }
            },
            bottomBar = {
                BottomAppBar(
                    backgroundColor = navigationBarBackgroundColor,
                    elevation = AppBarDefaults.BottomAppBarElevation,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(onClick = {
                            if (navController != null) {
                                navController.navigate("home")
                            }
                        }) {
                            val iconColor = if (currentDestination == "home") activeColor else inactiveColor
                            val icon = if (currentDestination == "home") R.drawable.map_filled else R.drawable.map
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painterResource(icon), contentDescription = "Mapa", tint = iconColor)
                                Text("Mapa", color = iconColor)
                            }
                        }

                        IconButton(onClick = {
                            if (navController != null) {
                                navController.navigate("reports")

                            }
                        }) {
                            val iconColor: Color
                            val icon: Int
                            if (
                                currentDestination == "reports"
                                || currentDestination!!.startsWith("create_report")
                                || currentDestination.startsWith("report_details")
                            ) {
                                iconColor = activeColor
                                icon = R.drawable.receipt_filled
                            } else {
                                iconColor = inactiveColor
                                icon = R.drawable.receipt
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painterResource(icon), contentDescription = "Reportes", tint = iconColor)
                                Text("Reportes", color = iconColor)
                            }
                        }

                        IconButton(onClick = {
                            if (navController != null) {
                                navController.navigate("profile")
                            }
                        }) {
                            val iconColor = if (currentDestination == "profile") activeColor else inactiveColor
                            val icon = if (currentDestination == "profile") R.drawable.person_filled else R.drawable.person
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painterResource(icon), contentDescription = "Perfil", tint = iconColor)
                                Text("Perfil", color = iconColor)
                            }
                        }
                    }
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    content(it)
                }
            }


        )
    }}

