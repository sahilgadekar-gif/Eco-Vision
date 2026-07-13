package com.vasundhara.app.ui.screens.ecomap

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.vasundhara.app.data.model.*
import com.vasundhara.app.ui.components.EcoBadge
import com.vasundhara.app.ui.components.VasundharaBackground
import com.vasundhara.app.ui.theme.*

private val mockMarkers = listOf(
    EcoMapMarker("1", "GreenCycle Hub", EcoMarkerType.RECYCLING, 18.5204, 73.8567, "Accepts plastic, glass, metal", "+91-9876543210"),
    EcoMapMarker("2", "Paryavaran NGO", EcoMarkerType.NGO, 18.5314, 73.8446, "River clean-up & awareness", "paryavaran@eco.in"),
    EcoMapMarker("3", "SolarPoint Station", EcoMarkerType.SOLAR, 18.5100, 73.8700, "Free solar charging", null),
    EcoMapMarker("4", "EV Charge Zone", EcoMarkerType.EV_CHARGING, 18.5250, 73.8600, "24/7 EV charging", null),
    EcoMapMarker("5", "City Compost Site", EcoMarkerType.COMPOST, 18.5180, 73.8520, "Organic waste drop-off", null),
)

private fun markerColor(type: EcoMarkerType): Color = when (type) {
    EcoMarkerType.RECYCLING -> EcoGreen; EcoMarkerType.NGO -> InfoBlue
    EcoMarkerType.SOLAR -> WarningAmber; EcoMarkerType.EV_CHARGING -> EcoGreenDim
    EcoMarkerType.COMPOST -> Color(0xFF8D6E63)
}

@Composable
fun EcoMapScreen() {
    var selectedType by remember { mutableStateOf<EcoMarkerType?>(null) }
    var selectedMarker by remember { mutableStateOf<EcoMapMarker?>(null) }
    val pune = LatLng(18.5204, 73.8567)
    val cameraState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(pune, 13f) }
    val filtered = if (selectedType == null) mockMarkers else mockMarkers.filter { it.type == selectedType }

    VasundharaBackground {
        Box(Modifier.fillMaxSize()) {
            GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraState, uiSettings = MapUiSettings(zoomControlsEnabled = false)) {
                filtered.forEach { m ->
                    Marker(
                        state = MarkerState(LatLng(m.lat, m.lng)),
                        title = m.name,
                        snippet = m.description,
                        onClick = { selectedMarker = m; false }
                    )
                }
            }

            // Filter chips
            LazyRow(Modifier.align(Alignment.TopCenter).padding(top = 16.dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = selectedType == null, onClick = { selectedType = null }, label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EcoGreen.copy(alpha = 0.9f), selectedLabelColor = PureBlack))
                }
                items(EcoMarkerType.values().toList()) { t ->
                    FilterChip(selected = selectedType == t, onClick = { selectedType = if (selectedType == t) null else t }, label = { Text(t.label) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = markerColor(t).copy(alpha = 0.9f), selectedLabelColor = PureBlack))
                }
            }

            // Bottom sheet marker detail
            selectedMarker?.let { m ->
                Card(
                    Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                    CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(markerColor(m.type).copy(alpha = 0.15f)), Alignment.Center) {
                                Icon(Icons.Filled.Place, null, tint = markerColor(m.type), modifier = Modifier.size(22.dp))
                            }
                            Column(Modifier.weight(1f)) {
                                Text(m.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                EcoBadge(m.type.label, markerColor(m.type))
                            }
                            IconButton(onClick = { selectedMarker = null }) { Icon(Icons.Filled.Close, null) }
                        }
                        Text(m.description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        m.contact?.let { Text(it, style = MaterialTheme.typography.bodySmall.copy(color = InfoBlue)) }
                    }
                }
            }
        }
    }
}
