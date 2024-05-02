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
import com.google.android.gms.maps.model.MapStyleOptions


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(navController: NavController? = null) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-34.5950995, -58.39988160000001), 15f)
    }

    val markers = remember {
        mutableStateOf(listOf(
            MarkerInfo(LatLng(-34.5950995, -58.39988160000001), "Contenedor 1", "Acá hay una descripción"),
            MarkerInfo(LatLng(-34.5951504, -58.39968), "Contenedor 2", "Acá hay una descripción"),
            MarkerInfo(LatLng(-34.5952227, -58.3993218), "Contenedor 3", "Acá hay una descripción"),
            MarkerInfo(LatLng(-34.595422, -58.39853549999999), "Contenedor 4", "Acá hay una descripción"),
            MarkerInfo(LatLng(-34.5955054, -58.39786999999999), "Contenedor 5", "Acá hay una descripción"),
            MarkerInfo(LatLng(-34.5955637, -58.3974045), "Contenedor 6", "Acá hay una descripción")
        ))
    }

    AppScaffold(navController = navController) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            GoogleMap(
                modifier = Modifier.fillMaxHeight(),
                cameraPositionState = cameraPositionState
            ) {

                val context = LocalContext.current
                markers.value.forEach { marker ->
                    val originalBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.contenedor_residuos)
                    val resizedBitmap = resizeBitmap(originalBitmap, 70, 70) // Ajusta las dimensiones según sea necesario
                    val customIcon: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizedBitmap)

                    Marker(
                        state = MarkerState(position = marker.position),
                        title = marker.title,
                        snippet = marker.snippet,
                        icon = customIcon
                    )
                }
            }
        }
    }


}
// Función para redimensionar un bitmap a las dimensiones especificadas
fun resizeBitmap(originalBitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val width = originalBitmap.width
    val height = originalBitmap.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height

    // Crear una matriz para aplicar la escala
    val matrix = Matrix().apply {
        postScale(scaleWidth, scaleHeight)
    }

    // Crear un bitmap redimensionado utilizando la matriz
    return Bitmap.createBitmap(originalBitmap, 0, 0, width, height, matrix, true)
}

data class MarkerInfo(
    val position: LatLng,
    val title: String,
    val snippet: String
)
