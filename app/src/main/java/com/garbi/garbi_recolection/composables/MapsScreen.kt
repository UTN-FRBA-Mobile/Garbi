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
import android.os.Looper
import android.util.Log
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
import androidx.compose.ui.AbsoluteAlignment
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
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.garbi.garbi_recolection.services.DirectionsClient
import com.garbi.garbi_recolection.services.Step
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.CameraMoveStartedReason

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

    val routeAvailable by viewModel.routeAvailable
    val routeModal by viewModel.routeModal
    val routeWaypoints by viewModel.routeWaypoints
    val routeDestination by viewModel.routeDestination
    val routeStart by viewModel.routeStart
    val continueRouteModal by viewModel.continueRouteModal

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
    var centerNavigation = remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        val service = RetrofitClient.containerService
        try {
            val response = withContext(Dispatchers.IO) { service.getContainers() }
            containersState.value = response.documents
            Log.v("Containers", response.toString())
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
                viewModel.updateRouteModal(context,false)
                viewModel.updateRouteAvailable(context,true)
            }
        )
    }

    LaunchedEffect(routeModal) {
        if (routeModal){
            showDialog.value = true;
        }
    }

    var steps by remember { mutableStateOf(emptyList<Step>()) }
    // Variables para manejar el estado
    var currentStepIndex by remember { mutableStateOf(0) }
    var currentInstruction by remember { mutableStateOf("") }
    var previousDistanceToEnd by remember { mutableStateOf(Double.POSITIVE_INFINITY) }

    LaunchedEffect(routeAvailable) {
        if (routeAvailable) {
            val directionsService = DirectionsClient.directionsService
            try {

                val latitude = userLat
                val longitude = userLng
                val userLocation = "${latitude}, ${longitude}"
                val waypoints = routeWaypoints

                Log.v("ROUTE", "Generando ruta con userLocation ${userLocation} y waypoints ${waypoints}, termina en ${routeDestination}")

                val response = withContext(Dispatchers.IO) {
                    directionsService.getDirections(userLocation, routeDestination, waypoints, apiKey!!)
                }
                viewModel.updateRouteStart(context,userLocation)
                Log.v("ROUTE","response ${response}")
                steps = response.routes.firstOrNull()?.legs?.firstOrNull()?.steps!!
                currentInstruction = steps.getOrNull(1)?.html_instructions?.replace(Regex("<[/]?b>"), "")
                    ?: "Instrucción no disponible"
                Log.v("ROUTE","steps ${steps} currentInstruction ${currentInstruction}")
                if (response.routes.isNotEmpty()) {
                    val points = PolyUtil.decode(response.routes[0].overview_polyline.points)
                    polylinePoints.value = points.map { LatLng(it.latitude, it.longitude) }

                }
                centerNavigation.value = true


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(userLat, userLng, userBearing, routeAvailable, centerNavigation.value) {
        //centra la camara en modo navegación. ahora se hace con unos segundos de lag, funciona solo en celular. en el emulador no anda tan bien
        if (routeAvailable and centerNavigation.value) {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(userLat, userLng)) // Ubicación actual del usuario
                        .zoom(20f) // Nivel de zoom
                        .bearing(userBearing) // Dirección actual del usuario
                        .tilt(45f) // Vista en perspectiva
                        .build()
                )
            )
        } else {
            if (!routeAvailable and centerNavigation.value){
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(LatLng(userLat, userLng))
                            .zoom(15f) // Zoom estándar
                            .bearing(0f) // Sin rotación
                            .tilt(0f) // Vista plana
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
        Log.v("ROUTE","routeAvailable ${routeAvailable} steps ${steps}")
        if (routeAvailable && steps.isNotEmpty()) {
            val currentStep = steps.getOrNull(currentStepIndex)
            currentStep?.let {
                val endLocation = LatLng(it.end_location.lat, it.end_location.lng)
                val userLocation = LatLng(userLat, userLng)
                val distanceToEnd = SphericalUtil.computeDistanceBetween(userLocation, endLocation)

                //Avanza un step si la distancia al final es menor a 10 metros o si la distancia es mayor que la anterior con dif de mas de 3
                if ((distanceToEnd < 10) or (distanceToEnd > (previousDistanceToEnd + 3))) {
                    currentStepIndex = (currentStepIndex+1).coerceAtMost(steps.size - 1)
                    previousDistanceToEnd = Double.POSITIVE_INFINITY
                }else{
                    previousDistanceToEnd = distanceToEnd
                }
                currentInstruction = steps.getOrNull(currentStepIndex+1)?.html_instructions?.replace(Regex("<[^>]*>"), "")
                    ?: "Instrucción no disponible"
                Log.v("ROUTE"," ${currentStepIndex} distanceToEnd ${distanceToEnd} end ${endLocation} currentInstruction ${currentInstruction}")
            }
        }
    }

    val showContinueDialog = remember { mutableStateOf(false) }
    LaunchedEffect(continueRouteModal){
        if (continueRouteModal){
            showContinueDialog.value = true;
        }
    }

    if (showContinueDialog.value){
        ContinueRouteDialog(onAlertAccepted = {
            viewModel.updateRouteDestination(context,routeStart)
            viewModel.updateRouteWaypoints(context,"")
            viewModel.updateRouteAvailable(context,true)
            showContinueDialog.value = false
            viewModel.updateContinueRouteModal(context,false)
        },
            onAlertDismissed = {
                showContinueDialog.value = false
                viewModel.updateContinueRouteModal(context,false)

            }
        )
    }

    val showConfirmEndRouteDialog = remember { mutableStateOf(false) }

    if (showConfirmEndRouteDialog.value) {
        ConfirmEndRouteDialog(
            onConfirm = {
                showConfirmEndRouteDialog.value = false

                viewModel.updateRouteAvailable(context,false);
                polylinePoints.value = emptyList()
                centerNavigation.value = false
                currentStepIndex = 0
                previousDistanceToEnd = Double.POSITIVE_INFINITY

                if(!(routeWaypoints == "")){ //si ocurre esto es porque es no el camino de regreso al deposito

                    viewModel.updateContinueRouteModal(context,true)
                    showContinueDialog.value = true
                }
            },
            onDismiss = {
                showConfirmEndRouteDialog.value = false
            }
        )
    }
    AppScaffold(navController = navController, topBarVisible = false) {
        Column(
            modifier = Modifier.fillMaxSize()
        ){
            if (routeAvailable){

                Box (
                    modifier = Modifier
                        .background(Green900)
                        .height(100.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        val arrow = if (currentInstruction.contains(stringResource(id = R.string.left),ignoreCase = true)) painterResource(R.drawable.arrow_left) else (if(currentInstruction.contains(
                                stringResource(id = R.string.right),ignoreCase = true)) painterResource(R.drawable.arrow_right) else painterResource(R.drawable.arrow_upward))
                        Box(modifier = Modifier
                            .size(100.dp)){
                            Icon(
                                painter = arrow ,
                                contentDescription = "Flecha de dirección",
                                tint= Color.White,
                                modifier = Modifier
                                    .size(60.dp)
                                    .align(Alignment.Center)
                                    .aspectRatio(1f))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            color= Color.White,
                            text=currentInstruction, fontSize= 20.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
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
                    Polyline(
                        points = polylinePoints.value,
                        color = Color.Blue,
                        width = 25f

                    )
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

                if (!centerNavigation.value){
                    ExtendedFloatingActionButton(
                        onClick = {
                            centerNavigation.value = true
                        },
                        icon = { Icon(Icons.Filled.LocationOn, "Centrar ruta", tint = Green900) },
                        text = { Text(text = "Centrar ruta", color = Green900) },
                        containerColor = White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(50.dp)
                            .height(30.dp)
                    )

                }
            }
        }}
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


@Composable
fun ContinueRouteDialog(onAlertAccepted: () -> Unit, onAlertDismissed : () -> Unit) {

    androidx.compose.material3.AlertDialog(
        text = {
            Text(text = stringResource(R.string.continue_route))
        },
        onDismissRequest = {},
        confirmButton = {
            androidx.compose.material.TextButton(
                onClick = {onAlertAccepted()}
            ) {
                Text(color = Green900, text = stringResource(R.string.dialog_confirm))
            }
        },
        dismissButton ={
            androidx.compose.material.TextButton(
                onClick = { onAlertDismissed() }
            ) {
                Text(color = Green900, text = stringResource(R.string.dialog_dismiss))
            }
        } ,
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
