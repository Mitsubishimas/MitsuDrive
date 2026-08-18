package com.mitsudrive.features.map.ui.components.map

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mitsudrive.core.location.model.DriveLocation
import com.mitsudrive.features.map.api.model.MapEvent
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun OsmMapView(
    currentLocation: DriveLocation?,
    events: List<MapEvent>,
    onEventClick: (MapEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Настройка osmdroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue = "MitsuDrive"
            isDebugMode = false
        }
    }
    
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            createMapView(ctx, currentLocation, events, onEventClick)
        },
        update = { mapView ->
            updateMapView(mapView, currentLocation, events, onEventClick)
        }
    )
}

private fun createMapView(
    context: Context,
    currentLocation: DriveLocation?,
    events: List<MapEvent>,
    onEventClick: (MapEvent) -> Unit
): MapView {
    val mapView = MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        minZoomLevel = 3.0
        maxZoomLevel = 19.0
        
        // Начальная позиция
        controller.setZoom(12.0)
        controller.setCenter(
            GeoPoint(
                currentLocation?.latitude ?: 55.7558,
                currentLocation?.longitude ?: 37.6173
            )
        )
    }
    
    // Добавляем оверлей местоположения
    val locationOverlay = MyLocationNewOverlay(
        GpsMyLocationProvider(context),
        mapView
    )
    locationOverlay.enableMyLocation()
    locationOverlay.enableFollowLocation()
    mapView.overlays.add(locationOverlay)
    
    // Добавляем маркеры событий
    addEventMarkers(mapView, events, onEventClick)
    
    return mapView
}

private fun updateMapView(
    mapView: MapView,
    currentLocation: DriveLocation?,
    events: List<MapEvent>,
    onEventClick: (MapEvent) -> Unit
) {
    // Удаляем старые маркеры событий
    mapView.overlays.removeAll { overlay ->
        overlay is Marker && overlay.id != "my_location"
    }
    
    // Добавляем новые маркеры
    addEventMarkers(mapView, events, onEventClick)
    
    mapView.invalidate()
}

private fun addEventMarkers(
    mapView: MapView,
    events: List<MapEvent>,
    onEventClick: (MapEvent) -> Unit
) {
    events.forEach { event ->
        val marker = Marker(mapView).apply {
            id = event.id
            position = GeoPoint(event.lat, event.lng)
            title = event.eventType.name
            snippet = event.description ?: ""
            
            setOnMarkerClickListener { clickedMarker, _ ->
                onEventClick(event)
                true
            }
        }
        
        mapView.overlays.add(marker)
    }
}
