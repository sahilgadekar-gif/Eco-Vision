package com.vasundhara.app.ui.screens.territory

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.vasundhara.app.data.model.Territory
import com.vasundhara.app.ui.components.VasundharaBackground
import com.vasundhara.app.ui.theme.*

@Composable
fun TerritoryScreen(vm: TerritoryViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(com.google.android.gms.maps.model.LatLng(19.9975, 73.7898), 11f)
    }
    
    VasundharaBackground {
        Column(Modifier.fillMaxSize()) {
            // Header
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Nashik Territories",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        "Total: ${state.totalTerritories} territories • ${state.totalUsers} users",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                
                Button(
                    onClick = { vm.refreshTerritories() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Refresh")
                }
            }
            
            // Map with Territories
            Box(Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapType = MapType.NORMAL
                    )
                ) {
                    // Add polygons for each territory
                    state.territories.forEach { territory ->
                        Polygon(
                            points = territory.area.map { 
                                com.google.android.gms.maps.model.LatLng(it.lat, it.lng) 
                            },
                            fillColor = Color(android.graphics.Color.parseColor(territory.color)).copy(alpha = 0.3f),
                            strokeColor = Color(android.graphics.Color.parseColor(territory.color)),
                            strokeWidth = 2f,
                            clickable = true,
                            onClick = {
                                vm.selectTerritory(territory)
                            }
                        )
                        
                        // Center marker for territory
                        Marker(
                            state = MarkerState(
                                position = com.google.android.gms.maps.model.LatLng(
                                    territory.centerLat, 
                                    territory.centerLng
                                )
                            ),
                            title = territory.name,
                            snippet = "Owner: ${territory.ownerName} • Trees: ${territory.totalTrees} • Population: ${territory.population}",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        )
                    }
                }
                
                // Territory List Overlay
                Card(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(200.dp),
                    RoundedCornerShape(12.dp),
                    CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    )
                ) {
                    Column(Modifier.fillMaxSize().padding(16.dp)) {
                        Text(
                            "Territory Owners",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Territory List
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.territories.take(4)) { territory ->
                                TerritoryItem(
                                    territory = territory,
                                    isSelected = state.selectedTerritory?.id == territory.id,
                                    onClick = { vm.selectTerritory(territory) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TerritoryItem(
    territory: Territory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) 
                    Color(android.graphics.Color.parseColor(territory.color)).copy(alpha = 0.2f)
                else 
                    if (isDark) Color.DarkGray else Color.LightGray
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                territory.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
            )
            Text(
                territory.ownerName,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDark) Color.LightGray else Color.DarkGray
                )
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${territory.totalTrees} trees",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Green,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                "${territory.population} people",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isDark) Color.LightGray else Color.DarkGray
                )
            )
        }
    }
}
