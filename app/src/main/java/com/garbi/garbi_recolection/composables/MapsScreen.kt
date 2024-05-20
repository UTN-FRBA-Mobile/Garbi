package com.garbi.garbi_recolection.composables

import AppScaffold
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.garbi.garbi_recolection.R
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.core.content.ContextCompat
import com.garbi.garbi_recolection.models.Container
import com.google.maps.android.compose.MapProperties
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerInfoWindowContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Rect
import com.garbi.garbi_recolection.ui.theme.Green900


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapsScreen(navController: NavController? = null) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-34.5950995, -58.39988160000001), 15f)
    }

    val containers = remember {
        mutableStateOf(
            listOf(
                Container(-58.39988160000001, -34.5950995, 85),
                Container(-58.39968, -34.5951504, 10),
                Container(-58.3993218, -34.5952227, 25),
                Container(-58.39853549999999, -34.595422, 60),
                Container(-58.39786999999999, -34.5955054, 95),
                Container(-58.3974045, -34.5955637, 50)
            )
        )
    }

    val context = LocalContext.current

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val permissionsGranted = permissions.values.all { it }
        hasLocationPermission = permissionsGranted
        navController?.navigate("home")
    }

    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    AppScaffold(navController = navController, topBarVisible = false) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            GoogleMap(
                modifier = Modifier.fillMaxHeight(),
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                cameraPositionState = cameraPositionState

            ) {

                containers.value.forEach { container ->

                    val containerIcon: BitmapDescriptor = if (container.capacity > 60) {
                        val originalBitmapRed =
                            BitmapFactory.decodeResource(context.resources, R.mipmap.red_circle)
                        val resizedBitmapRed = resizeBitmap(originalBitmapRed, 70, 70)
                        BitmapDescriptorFactory.fromBitmap(resizedBitmapRed)
                    } else {
                        val originalBitmapGreen = BitmapFactory.decodeResource(
                            context.resources,
                            R.mipmap.green_circle
                        )
                        val resizedBitmapGreen = resizeBitmap(originalBitmapGreen, 70, 70)
                        BitmapDescriptorFactory.fromBitmap(resizedBitmapGreen)
                    }
/* Marker de antes. La diferencia con el de abajo es que no tiene un boton.
                    MarkerInfoWindowContent(
                        state = MarkerState(position = LatLng(container.lat, container.lng)),
                        title = "Realizar reporte",
                        snippet = "Capacidad: ${container.capacity}%",
                        icon = containerIcon,
                        onInfoWindowClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                navController?.navigate("reports")
                            }
                        }
                    )
 */


                    //Marker customizado que tiene boton y forma con piquito. Es muy grande, habria que ajustar el tamaño
                    MarkerInfoWindowContent(
                        state = MarkerState(position = LatLng(container.lat, container.lng)),
                        title = "Realizar reporte",
                        snippet = "Capacidad: ${container.capacity}%",
                        icon = containerIcon,
                        onInfoWindowClick = {
                            CoroutineScope(Dispatchers.Main).launch {
                                navController?.navigate("reports")
                            }
                        }
                    ){
                        val bubbleShape: Shape = GenericShape { size, _ ->
                            val path = Path().apply {
                                moveTo(size.width * 0.5f, size.height)
                                lineTo(size.width * 0.4f, size.height * 0.75f)
                                lineTo(size.width * 0.1f, size.height * 0.75f)
                                arcTo(
                                    rect = Rect(size.width * 0.1f, size.height * 0.75f, size.width * 0.9f, size.height * 0.75f),
                                    startAngleDegrees = 90f,
                                    sweepAngleDegrees = 180f,
                                    forceMoveTo = false
                                )
                                lineTo(size.width * 0.6f, size.height)
                                close()
                            }
                            addPath(path)
                        }

                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(100.dp)
                                .background(
                                    color = Color.White,
                                    shape = bubbleShape
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Realizar reporte",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Capacidad: ${container.capacity}%",
                                    color = Color.Gray
                                )
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Green900),
                                    onClick = {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            navController?.navigate("reports")
                                        }
                                    }
                                ) {
                                    Text("Realizar un reporte")
                                }
                            }
                        }
                    }
                }

            }
        }
    }

}
fun resizeBitmap(originalBitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val width = originalBitmap.width
    val height = originalBitmap.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    val matrix = Matrix().apply {
        postScale(scaleWidth, scaleHeight)
    }
    return Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true)
}
