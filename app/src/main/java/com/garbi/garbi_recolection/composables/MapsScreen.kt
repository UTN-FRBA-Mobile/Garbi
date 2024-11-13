package com.garbi.garbi_recolection.composables

import AppScaffold
import Container
import ContainerClusterItem
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
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Rect
import com.garbi.garbi_recolection.services.RetrofitClient
import com.garbi.garbi_recolection.ui.theme.*
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.garbi.garbi_recolection.services.Step
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.clustering.Clustering

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapsScreen(
    navController: NavController? = null,
    viewModel: MapsViewModel,
    fusedLocationClient: FusedLocationProviderClient
) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-34.5950995, -58.39988160000001), 15f)
    }

    val containersState = remember { mutableStateOf<List<Container>>(emptyList()) }
    val containersClusterState = remember { mutableStateOf<List<ContainerClusterItem>>(emptyList()) }

    val routeAvailable by viewModel.routeAvailable
    val routeModal by viewModel.routeModal
    var route by viewModel.route
    val routeId by viewModel.routeId

    val currentStepIndex by viewModel.currentStepIndex
    val previousDistanceToEnd by viewModel.previousDistanceToEnd

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

    var userLat by remember { mutableStateOf(0.0) }
    var userLng by remember { mutableStateOf(0.0) }
    var userBearing by remember { mutableStateOf(0f) }
    val centerNavigation = remember { mutableStateOf(false) }

    val locationRequest = LocationRequest.create().apply {
        interval = 2000 // Intervalo en milisegundos para las actualizaciones
        fastestInterval = 2000 // Intervalo más rápido en milisegundos
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Alta precisión
    }
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                userLat = location.latitude
                userLng = location.longitude
                userBearing = location.bearing
                Log.v("Ubicacion","La ubicación del usuario es lng: ${userLng} lat: ${userLat} bearing: ${userBearing}")

            }
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    suspend fun fetchContainers() {
        val service = RetrofitClient.containerService
        try {
            val response = withContext(Dispatchers.IO) { service.getContainers() }
            Log.v("containers", "response ${response} body ${response.body()}")

            if (response.isSuccessful) {
                containersState.value = response.body()?.result ?: emptyList()
                containersClusterState.value = containersState.value.map { container ->
                    ContainerClusterItem(container)
                }
                Log.v("containers state", containersState.value.toString())
                Log.v("containers cluster state", containersClusterState.value.toString())
            } else {
                Toast.makeText(context, "Error cargando los contenedores", Toast.LENGTH_LONG).show()
            }
            response.body()?.toString()?.let { Log.v("Containers", it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /*
    LaunchedEffect(Unit) {
        Log.v("CONTAINERS", "Containers all")
        fetchContainers()
    }*/

    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    val showDialog = remember { mutableStateOf(false) }
    //val polylinePoints = remember { mutableStateOf<List<LatLng>>(emptyList()) }
    val polylinePoints = remember { mutableStateOf<List<List<LatLng>>>(emptyList()) }


    var loadingRoute by remember { mutableStateOf(false) }
    if (showDialog.value) {
        AlertDialog(
            onAlertAccepted = {
                showDialog.value = false;
                viewModel.updateRouteModal(context,false)
                viewModel.updateRouteAvailable(context,true)
                loadingRoute = true
            }
        )
    }

    LaunchedEffect(routeModal) {
        if (routeModal){
            showDialog.value = true;
        }
    }

    var steps by remember { mutableStateOf(emptyList<Step>()) }
    var currentInstruction by remember { mutableStateOf("Cargando instrucciones...") }


    LaunchedEffect(route){
        if (route != null){
            Log.v("CONTAINERS", "Containers de la ruta route")
            containersState.value = route!!.containers
            containersClusterState.value = containersState.value.map { container ->
                ContainerClusterItem(container)
            }
            Log.v("containers state", containersState.value.toString())
            Log.v("containers cluster state", containersClusterState.value.toString())
        }else{
            Log.v("CONTAINERS", "Containers all")
            fetchContainers()
        }
    }


    LaunchedEffect(routeAvailable,routeId) {
        if (routeAvailable) {
            Log.v("route","route available!! route ${routeId} ${route}")
            try {

                if(route == null){
                    Log.v("ROUTE","Buscando rutaa")
                    val service = RetrofitClient.routeService
                    val response = withContext(Dispatchers.IO) { service.getRoute(routeId) }
                    if (response.isSuccessful) {
                        Log.v("ROUTE","Ruta cargada ${response.body()?.toRoute()}")
                        route = response.body()?.toRoute()
                        Log.v("ROUTE","Containers ${route?.containers}")
                        viewModel.updateRoute(context, response.body()?.toRoute())
                        loadingRoute = false


                        val responseStart = withContext(Dispatchers.IO) { service.startRoute(routeId) }
                        Log.v("ROUTE", "responseStart ${responseStart.code()} ${responseStart.body()}")
                    } else {
                        println("code: ${response.code()}")
                        println("errorbody: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "No se pudo cargar la ruta", Toast.LENGTH_LONG).show()
                        viewModel.updateRouteAvailable(context,false)
                        viewModel.updateRouteId(context,"")
                        loadingRoute = false
                    }
                }
                Log.v("ROUTE","route COMUN ${route}")
                steps = route?.legs?.flatMap { it.steps } ?: emptyList()
                currentInstruction = steps.getOrNull(currentStepIndex + 1)?.html_instructions?.replace(Regex("<[/]?b>"), "")
                    ?: "Instrucción no disponible"
                /*
                val points = PolyUtil.decode(route!!.overview_polyline.points)
                polylinePoints.value = points.map { LatLng(it.latitude, it.longitude) }*/

                polylinePoints.value = route!!.polylines.map { polyline ->
                    PolyUtil.decode(polyline.points).map { LatLng(it.latitude, it.longitude) }
                }

                centerNavigation.value = true

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else{
            if ((routeId != "") and !routeModal){
                Log.v("ROUTE", " finish routeAvailable ${routeAvailable} routeId ${routeId}")
                //es porque pusimos route available en false pero routeId sigue teniendo contenido
                val service = RetrofitClient.routeService
                val responseFinish = withContext(Dispatchers.IO) { service.finishRoute(routeId) }
                Log.v("ROUTE", "responseFinish ${responseFinish.code()} ${responseFinish.body()}")
                viewModel.updateRouteId(context,"")
            }
        }
    }

    LaunchedEffect(userLat, userLng, userBearing, routeAvailable, centerNavigation.value) {
        if (routeAvailable and centerNavigation.value) {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(userLat, userLng))
                        .zoom(20f)
                        .bearing(userBearing)
                        .tilt(45f)
                        .build()
                )
            )
        } else {
            if (!routeAvailable and centerNavigation.value){
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(LatLng(userLat, userLng))
                            .zoom(15f)
                            .bearing(0f)
                            .tilt(0f)
                            .build()
                    )
                )

            }
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            centerNavigation.value = false
        }
    }

    LaunchedEffect(userLat, userLng, routeAvailable) {
        if (routeAvailable && steps.isNotEmpty() && userLat != 0.0 && userLng != 0.0) {
            val currentStep = steps.getOrNull(currentStepIndex)
            currentStep?.let {
                val endLocation = LatLng(it.end_location.lat, it.end_location.lng)
                Log.v("ROUTE", "step ${currentStepIndex} endLocation ${endLocation}")
                val userLocation = LatLng(userLat, userLng)
                val distanceToEnd = SphericalUtil.computeDistanceBetween(userLocation, endLocation)
                Log.v("ROUTE", "distanceToEnd ${distanceToEnd} previousDistanceToEnd ${previousDistanceToEnd}")
                if ((distanceToEnd < 15) or (distanceToEnd > (previousDistanceToEnd + 3))) {
                    Log.v("ROUTE", "avanzando un paso")
                    viewModel.updateCurrentStepIndex(context,(currentStepIndex+1).coerceAtMost(steps.size - 1))
                    viewModel.updatePreviousDistanceToEnd(context,Double.POSITIVE_INFINITY)
                }else{
                    viewModel.updatePreviousDistanceToEnd(context,distanceToEnd)
                }

                currentInstruction = steps.getOrNull(currentStepIndex + 1)?.html_instructions?.replace(Regex("<div.*"), "")
                    ?.replace(Regex("<[^>]*>"), "")
                    ?: "Instrucción no disponible"
                Log.v("ROUTE"," ${currentStepIndex} currentInstruction ${currentInstruction} distanceToEnd ${distanceToEnd} end ${endLocation} ")
            }
        }
    }

    val showConfirmEndRouteDialog = remember { mutableStateOf(false) }

    if (showConfirmEndRouteDialog.value) {
        ConfirmEndRouteDialog(
            onConfirm = {
                showConfirmEndRouteDialog.value = false
                Log.v("route","route terminada route ${route} ")

                viewModel.updateRouteAvailable(context,false)

                viewModel.updateRoute(context,null)
                polylinePoints.value = emptyList()
                centerNavigation.value = false
                viewModel.updateCurrentStepIndex(context,0);
                viewModel.updatePreviousDistanceToEnd(context,Double.POSITIVE_INFINITY)

            },
            onDismiss = {
                showConfirmEndRouteDialog.value = false
            }
        )
    }


    val showCreateReportButton = remember { mutableStateOf(false) }
    val showCreateReportButtonContainer = remember { mutableStateOf<Container?>(null) }

    AppScaffold(navController = navController, topBarVisible = false) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            if (loadingRoute) {
                Log.v("route", "loading route")
                LoaderScreen()

            } else {
                if (routeAvailable && route != null) {

                    Box(
                        modifier = Modifier
                            .background(Green900)
                            .height(100.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val arrow = if (currentInstruction.contains(
                                    stringResource(id = R.string.left),
                                    ignoreCase = true
                                )
                            ) painterResource(R.drawable.arrow_left) else (if (currentInstruction.contains(
                                    stringResource(id = R.string.right), ignoreCase = true
                                )
                            ) painterResource(R.drawable.arrow_right) else painterResource(R.drawable.arrow_upward))
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                            ) {
                                Icon(
                                    painter = arrow,
                                    contentDescription = "Flecha de dirección",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .align(Alignment.Center)
                                        .aspectRatio(1f)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                color = Color.White,
                                text = currentInstruction, fontSize = 20.sp,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxHeight(),
                        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                        cameraPositionState = cameraPositionState,
                        onMapClick = {
                            showCreateReportButton.value = false
                        }

                    ) {
                        val zoom = cameraPositionState.position.zoom
                        val iconSize = (10 + ((zoom - 10) * 3)).coerceIn(10f, 40f).toInt()

                        if (polylinePoints.value.isNotEmpty()) {
                            polylinePoints.value.forEach { polyline ->
                                Polyline(
                                    points = polyline,
                                    color = Color.Blue,
                                    width = 25f
                                )
                            }
                        }

                        if (containersState.value.isNotEmpty()) {
                            Clustering(
                                items = containersClusterState.value,
                                clusterItemContent = {
                                    IconMarker(it.getContainer())
                                },
                                onClusterItemClick = {
                                    showCreateReportButton.value = true
                                    showCreateReportButtonContainer.value = it.getContainer()
                                    false
                                }
                            )
                        }

                    }

                    if (showCreateReportButton.value) {
                        val buttonPadding = when {
                            !routeAvailable -> 10.dp
                            routeAvailable && !centerNavigation.value -> 90.dp
                            else -> 50.dp
                        }
                        ExtendedFloatingActionButton(
                            onClick = {
                                navController?.navigate("create_report/${showCreateReportButtonContainer.value?.id}/${showCreateReportButtonContainer.value?.address?.street}/${showCreateReportButtonContainer.value?.address?.number}/${showCreateReportButtonContainer.value?.address?.neighborhood}")
                            },
                            icon = {
                                Icon(
                                    Icons.Filled.AddCircle,
                                    "Hacer un reporte",
                                    tint = Green900
                                )
                            },
                            text = { Text(text = "Hacer un reporte", color = Green900) },
                            containerColor = White,
                            modifier = Modifier
                                .align(Alignment.BottomCenter) // Funciona porque está dentro de un Box
                                .padding(buttonPadding)
                                .height(30.dp)
                        )
                    }
                    if (routeAvailable && route != null) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                showConfirmEndRouteDialog.value = true
                            },
                            icon = { Icon(Icons.Filled.Clear, "Terminar ruta", tint = Green900) },
                            text = { Text(text = "Finalizar ruta", color = Green900) },
                            containerColor = White,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(10.dp)
                                .height(30.dp)
                        )

                        if (!centerNavigation.value) {
                            ExtendedFloatingActionButton(
                                onClick = {
                                    centerNavigation.value = true
                                },
                                icon = {
                                    Icon(
                                        Icons.Filled.LocationOn,
                                        "Centrar ruta",
                                        tint = Green900
                                    )
                                },
                                text = { Text(text = "Centrar ruta", color = Green900) },
                                containerColor = White,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(50.dp)
                                    .height(30.dp)
                            )

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IconMarker(container: Container) {
    val color = when {
        container.capacity > 60 -> RedRejected
        container.capacity in 40..60 -> Orange600
        else -> GreenResolved
    }
    val painter: Painter = painterResource(id = R.mipmap.circle)
    Icon(
        painter = painter,
        tint = color,
        contentDescription = "Container Icon",
        modifier = Modifier.size(24.dp)
    )
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

@Composable
fun ConfirmEndRouteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismiss,
            text = { androidx.compose.material.Text(text = stringResource(id = R.string.end_route_modal)) },
            confirmButton = {
                androidx.compose.material.TextButton(
                    onClick = {onConfirm()}
                ) {
                    Text(color = Green900, text = stringResource(R.string.dialog_confirm))
                }
            },
            dismissButton = {
                androidx.compose.material.TextButton(
                    onClick = {onDismiss()}
                ) {
                    Text(color = Gray, text = stringResource(R.string.dialog_dismiss))
                }
            },
        )
}
