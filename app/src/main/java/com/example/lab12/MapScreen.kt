package com.example.lab12

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.runtime.*
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
@Composable
fun MapScreen() {

    val arequipaLocation = LatLng(-16.4040102, -71.559611)

    val cameraPositionState = rememberCameraPositionState {
        position =
            com.google.android.gms.maps.model.CameraPosition
                .fromLatLngZoom(arequipaLocation, 12f)
    }


    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    var userLocation by remember {
        mutableStateOf<LatLng?>(null)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->

                if (location != null) {

                    android.util.Log.d(
                        "GPS",
                        "LAT=${location.latitude} LON=${location.longitude}"
                    )

                    userLocation = LatLng(
                        location.latitude,
                        location.longitude
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->

                if (location != null) {

                    android.util.Log.d(
                        "GPS",
                        "LAT=${location.latitude} LON=${location.longitude}"
                    )

                    userLocation = LatLng(
                        location.latitude,
                        location.longitude
                    )
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Movimiento automático a Yura
    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(
                LatLng(-16.2520984, -71.6836503),
                12f
            ),
            durationMs = 3000
        )
    }
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    it,
                    18f
                )
            )
        }
    }



    // Polígono Mall Aventura
    val mallAventuraPolygon = listOf(
        LatLng(-16.432292, -71.509145),
        LatLng(-16.432757, -71.509626),
        LatLng(-16.433013, -71.509310),
        LatLng(-16.432566, -71.508853)
    )

    // Polígono Parque Lambramani
    val parqueLambramaniPolygon = listOf(
        LatLng(-16.422704, -71.530830),
        LatLng(-16.422920, -71.531340),
        LatLng(-16.423264, -71.531110),
        LatLng(-16.423050, -71.530600)
    )

    // Polígono Plaza de Armas
    val plazaDeArmasPolygon = listOf(
        LatLng(-16.398866, -71.536961),
        LatLng(-16.398744, -71.536529),
        LatLng(-16.399178, -71.536289),
        LatLng(-16.399299, -71.536721)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType =
                    MapType.NORMAL
            ),
        ){

            // Marcador principal
            Marker(
                state = rememberMarkerState(position = arequipaLocation),
                title = "Arequipa, Perú"
            )

            // Marcadores adicionales
            val locations = listOf(
                LatLng(-16.433415, -71.5442652), // JLByR
                LatLng(-16.4205151, -71.4945209), // Paucarpata
                LatLng(-16.3524187, -71.5675994) // Zamácola
            )

            locations.forEach { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Punto de interés"
                )
            }

            // Plaza de Armas
            Polygon(
                points = plazaDeArmasPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.3f),
                strokeWidth = 5f
            )

            // Parque Lambramani
            Polygon(
                points = parqueLambramaniPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.3f),
                strokeWidth = 5f
            )

            // Mall Aventura
            Polygon(
                points = mallAventuraPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.3f),
                strokeWidth = 5f
            )
            val ubicacionPrueba = LatLng(-16.439404764734252, -71.52563478901887)

            Marker(
                state = rememberMarkerState(position = ubicacionPrueba),
                title = "MI GPS ACTUAL"
            )
            LaunchedEffect(Unit) {
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(
                        ubicacionPrueba,
                        17f
                    )
                )
            }
            userLocation?.let { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "MI GPS ACTUAL"
                )
            }
        }
    }
}