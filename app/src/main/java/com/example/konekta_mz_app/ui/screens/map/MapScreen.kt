package com.example.konekta_mz_app.ui.screens.map

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.konekta_mz_app.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onOfferClick: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Empregos") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                OsmMapView(
                    context = context,
                    offers = state.offers,
                    userLat = state.userLatitude,
                    userLng = state.userLongitude,
                    zoomLevel = state.zoomLevel,
                    onOfferClick = onOfferClick
                )
            }
        }
    }
}

@Composable
fun OsmMapView(
    context: Context,
    offers: List<com.example.konekta_mz_app.data.local.entity.JobOffer>,
    userLat: Double,
    userLng: Double,
    zoomLevel: Double,
    onOfferClick: (Long) -> Unit
) {
    DisposableEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        onDispose { }
    }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(zoomLevel)
                controller.setCenter(GeoPoint(userLat, userLng))

                // Add markers for each offer
                offers.forEach { offer ->
                    if (offer.latitude != 0.0 && offer.longitude != 0.0) {
                        val marker = Marker(this)
                        marker.position = GeoPoint(offer.latitude, offer.longitude)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = offer.title
                        marker.snippet = "${offer.employerName}\n${offer.location}"
                        marker.setOnMarkerClickListener { _, _ ->
                            onOfferClick(offer.id)
                            true
                        }
                        overlays.add(marker)
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
