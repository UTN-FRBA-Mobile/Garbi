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
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.Marker
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.garbi.garbi_recolection.R
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.core.content.ContextCompat
import com.garbi.garbi_recolection.models.Container
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(navController: NavController? = null) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-34.5950995, -58.39988160000001), 15f)
    }

    val containers = remember {
        mutableStateOf(listOf(
            Container(-58.39988160000001,-34.5950995,85),
            Container(-58.39968,-34.5951504,10),
            Container(-58.3993218,-34.5952227,25),
            Container(-58.39853549999999,-34.595422,60),
            Container(-58.39786999999999,-34.5955054,95),
            Container(-58.3974045,-34.5955637,50)
            ))
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
    }

    Log.v("hasLocationPermission", "$hasLocationPermission")
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

                    var containerIcon: BitmapDescriptor
                    if (container.capacity>60) {
                        val originalBitmapRed = BitmapFactory.decodeResource(context.resources, R.mipmap.container_red)
                        val resizedBitmapRed = resizeBitmap(originalBitmapRed, 70, 70)
                         containerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmapRed)
                    }
                    else {
                        val originalBitmapGreen = BitmapFactory.decodeResource(context.resources, R.mipmap.container_green)
                        val resizedBitmapGreen = resizeBitmap(originalBitmapGreen, 70, 70)
                         containerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmapGreen)
                    }

                        Marker(
                        state = MarkerState(position = LatLng(container.lat,container.lng)),
                        title = "Contenedor",
                        snippet = "Capacidad: ${container.capacity}%",
                        icon = containerIcon
                    )
                }

                val cabjBitMap = BitmapFactory.decodeResource(context.resources, R.mipmap.cabj)
                val resizedBitmapCabj = resizeBitmap(cabjBitMap, 150, 150)
                val cabjContainer: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmapCabj)

                Marker(
                    state = MarkerState(position = LatLng(-34.63564126802834,-58.36469881790011)),
                    title = "El que nunca descendió",
                    snippet = "El que más copas ganó",
                    icon = cabjContainer
                )
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

