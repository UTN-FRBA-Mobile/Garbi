import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.ui.theme.Garbi_recolectionTheme
import com.garbi.garbi_recolection.ui.theme.Green900

@Composable
fun AppScaffold(
    title: String? = null,
    navController: NavController? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val navigationIcon: (@Composable () -> Unit)? =
        if (navController?.previousBackStackEntry != null) {
            {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        } else null

    Garbi_recolectionTheme {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    backgroundColor = Green900
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
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painterResource(R.drawable.map), contentDescription = "Mapa", tint = Color.White)
                                Text("Mapa", color = Color.White)
                            }
                        }

                        IconButton(onClick = {
                            if (navController != null) {
                                navController.navigate("Reportes")
                            }
                        }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painterResource(R.drawable.receipt), contentDescription = "Reportes", tint = Color.White)
                                Text("Reportes", color = Color.White)
                            }
                        }

                        IconButton(onClick = {
                            if (navController != null) {
                                navController.navigate("Perfil")
                            }
                        }) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painterResource(R.drawable.person), contentDescription = "Perfil", tint = Color.White)
                                Text("Perfil", color = Color.White)
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
    }
}
