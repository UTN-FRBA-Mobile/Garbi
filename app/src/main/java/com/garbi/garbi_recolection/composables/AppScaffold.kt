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
import com.garbi.garbi_recolection.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String? = null,
    navController: NavController? = null,
    topBarVisible: Boolean,
    backButton: Boolean? = false,
    onBackButtonClick: (() -> Unit)? = null,
    actions: Boolean? = false,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val activeColor = Green900
    val inactiveColor = LightGray

    val currentDestination = navController?.currentDestination?.route

    Garbi_recolectionTheme {
        Scaffold(
            topBar =  {
                if (topBarVisible) {
                    TopAppBar(
                        backgroundColor = Green900,
                        contentColor = White,
                        title = {
                            Text(text = title ?: stringResource(R.string.app_name))
                        },
                        navigationIcon = if (backButton == true) {
                            {
                                if (onBackButtonClick != null) {
                                    IconButton(onClick = { onBackButtonClick.invoke() })
                                    {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                } else {
                                    IconButton(onClick = { navController!!.popBackStack() })
                                    {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                }
                            }
                        } else {
                            null
                        },
                        actions = {
                            if (actions == true) {
                                Row(modifier = Modifier.padding(end = 8.dp))
                                {
                                    IconButton(onClick = { onEditClick?.invoke() }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                        )
                                    }
                                    IconButton(onClick = { onDeleteClick?.invoke() }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                BottomAppBar(
                    backgroundColor = White,
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
                                Text(text = stringResource(R.string.map_screen), color = iconColor)
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
                                || currentDestination.startsWith("edit_report")
                            ) {
                                iconColor = activeColor
                                icon = R.drawable.receipt_filled
                            } else {
                                iconColor = inactiveColor
                                icon = R.drawable.receipt
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painterResource(icon), contentDescription = "Reportes", tint = iconColor)
                                Text(text = stringResource(R.string.reports_screen), color = iconColor)
                            }
                        }

                        IconButton(onClick = {
                            if (navController != null) {
                                navController.navigate("profile")
                            }
                        }) {
                            val iconColor: Color
                            val icon: Int
                            if (currentDestination == "profile" || currentDestination == "change_password") {
                                iconColor = activeColor
                                icon = R.drawable.person_filled
                            } else {
                                iconColor = inactiveColor
                                icon = R.drawable.person
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painterResource(icon), contentDescription = "Perfil", tint = iconColor)
                                Text(text = stringResource(R.string.profile_screen), color = iconColor)
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
