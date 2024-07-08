package com.garbi.garbi_recolection.composables

import AppScaffold
import Container
import MapsViewModel
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.google.maps.android.compose.MapProperties
import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.MarkerInfoWindowContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Rect
import com.garbi.garbi_recolection.services.RetrofitClient
import com.garbi.garbi_recolection.ui.theme.*
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.garbi.garbi_recolection.services.DirectionsClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.Marker

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapsScreen(
    navController: NavController? = null,
    viewModel: MapsViewModel,
    fusedLocationClient: FusedLocationProviderClient
) {
    val context = LocalContext.current;

    val applicationInfo: ApplicationInfo = context.packageManager
        .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    val apiKey = applicationInfo.metaData.getString("com.google.android.geo.API_KEY")


    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-34.5950995, -58.39988160000001), 15f)
    }

    val containersState = remember { mutableStateOf<List<Container>>(emptyList()) }
    var routeAvailable by viewModel.routeAvailable;
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

    var userLat: Double = 0.0
    var userLng: Double = 0.0
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location : Location? ->
            println("locationnn ${location}")
            userLat = location!!.latitude
            userLng = location.longitude
        }

    LaunchedEffect(Unit) {
        val service = RetrofitClient.containerService
        try {
            val response = withContext(Dispatchers.IO) { service.getContainers() }
            containersState.value = response.documents
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    val showDialog = remember { mutableStateOf(false) }
    val polylinePoints = remember { mutableStateOf<List<LatLng>>(emptyList()) }


    if (showDialog.value) {
        AlertDialog(
            onAlertAccepted = {
                showDialog.value = false;
                routeAvailable = true;
            }
        )
    }

    LaunchedEffect(Unit) {
        delay(15_000) //Esto en realidad no tiene que estar con un delay. Se va a setear la variable en true cuando haya un recorrido disponible
        if (!routeAvailable){
            showDialog.value = true;
        }
    }

    LaunchedEffect(routeAvailable) {
        if (routeAvailable) {
            val directionsService = DirectionsClient.directionsService
            try {

                val latitude = userLat
                val longitude = userLng
                val userLocation = "${latitude}, ${longitude}"
                val destination = "-34.6286,-58.4355"
                val waypoints = "-34.5806,-58.4066|-34.5899,-58.4284"

                println("USER LOCATION ${userLocation}")

                val response = withContext(Dispatchers.IO) {
                    directionsService.getDirections(userLocation, destination, waypoints, apiKey!!)
                }
                if (response.routes.isNotEmpty()) {
                    val points = PolyUtil.decode(response.routes[0].overview_polyline.points)
                    polylinePoints.value = points.map { LatLng(it.latitude, it.longitude) }
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 18f)
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
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
                val zoom = cameraPositionState.position.zoom
                val iconSize = (10 + ((zoom - 10) * 3)).coerceIn(10f, 40f).toInt()



                if (polylinePoints.value.isNotEmpty()) {

                    val truckBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.camion_garbi_medium)
                    val truckIcon: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(truckBitmap)



                    Polyline(
                        points = polylinePoints.value,
                        color = Color.Blue
                    )
                    polylinePoints.value.firstOrNull()?.let { firstPoint ->
                        Marker(
                            state = MarkerState(position = firstPoint),
                            title = "Camión",
                            icon = truckIcon
                        )
                    }


                }

                if(containersState.value.isNotEmpty()) {
                    containersState.value.forEach { container ->
                        val containerIconState = remember { mutableStateOf<BitmapDescriptor?>(null) }

                        LaunchedEffect(container, iconSize) {
                            containerIconState.value = getContainerIcon(container, context, iconSize)
                        }

                        containerIconState.value?.let { containerIcon ->
                            MarkerInfoWindowContent(
                                state = MarkerState(position = LatLng(container.coordinates.lat, container.coordinates.lng)),
                                icon = containerIcon,
                                onInfoWindowClick = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val addr = container.address
                                        navController?.navigate("create_report/${container._id}/${addr.street}/${addr.number}/${addr.neighborhood}")
                                    }
                                }
                            ) {
                                MarkerInfoContent(container, navController)
                            }
                        }
                    }
                }


            }

            if (routeAvailable){

                ExtendedFloatingActionButton(
                    onClick = { routeAvailable = false;
                        polylinePoints.value = emptyList()
                    },
                    icon = { Icon(Icons.Filled.Clear, "Terminar ruta", tint = Green900) },
                    text = { Text(text = "Finalizar ruta", color = Green900) },
                    containerColor = White,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(10.dp).height(30.dp)
                )
            }
        }
    }
}



@Composable
fun MarkerInfoContent(container: Container, navController: NavController?) {
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
                color = White,
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
                text = stringResource(R.string.text_capacity) + "${container.capacity}%",
                fontWeight = FontWeight.Bold,
                color = DarkGray
            )
            Text(
                text = "${container.address.street} ${container.address.number} ",
                color = Gray
            )

            OutlinedButton(
                modifier = Modifier.padding(0.dp,3.dp,0.dp,0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Green900),
                onClick = {
                    navController?.navigate("reports")
                }
            ) {
                Text(
                    text = stringResource(R.string.create_report_button),
                    fontWeight = FontWeight.Bold,
                    color = Green900
                )
            }
        }
    }
}
suspend fun getContainerIcon(container: Container, context: Context, iconSize: Int): BitmapDescriptor {
    return withContext(Dispatchers.IO) {
        val resource = when {
            container.capacity > 60 -> R.mipmap.red_circle
            container.capacity in 40..60 -> R.mipmap.orange_circle
            else -> R.mipmap.green_circle
        }
        val originalBitmap = BitmapFactory.decodeResource(context.resources, resource)
        val resizedBitmap = resizeBitmap(originalBitmap, iconSize, iconSize)
        BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }
}
suspend fun resizeBitmap(originalBitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    return withContext(Dispatchers.IO) {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix().apply {
            postScale(scaleWidth, scaleHeight)
        }
        Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true)
    }
}

@Composable
fun AlertDialog(onAlertAccepted: () -> Unit) {

    androidx.compose.material3.AlertDialog(
        text = {
            Text(text = stringResource(R.string.text_new_route))
        },
        onDismissRequest = {},
        confirmButton = {
            androidx.compose.material.TextButton(
                onClick = { onAlertAccepted() }
            ) {
                Text(color = Green900, text = stringResource(R.string.button_start))
            }
        },
        containerColor = White
    )
}