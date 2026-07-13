package com.vasundhara.app.ui.screens.trees

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
import com.vasundhara.app.data.model.TreeLocation
import com.vasundhara.app.ui.components.VasundharaBackground
import com.vasundhara.app.ui.theme.*

@Composable
fun TreesPlantedScreen(vm: TreesPlantedViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(com.google.android.gms.maps.model.LatLng(19.8762, 75.3433), 12f)
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
                        "Trees Planted",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        "Total: ${state.totalTrees} trees planted",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                
                Button(
                    onClick = { vm.refreshTrees() },
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
            
            // Map
            Box(Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapType = MapType.NORMAL
                    )
                ) {
                    // Add markers for each tree
                    state.trees.forEach { tree ->
                        Marker(
                            state = MarkerState(
                                position = com.google.android.gms.maps.model.LatLng(tree.lat, tree.lng)
                            ),
                            title = "${tree.treeType} planted by ${tree.plantedBy}",
                            snippet = "Planted on ${tree.plantedDate} • Height: ${tree.height}m • Health: ${tree.health}",
                            icon = BitmapDescriptorFactory.defaultMarker(
                                when (tree.health) {
                                    "Healthy" -> BitmapDescriptorFactory.HUE_GREEN
                                    "Growing" -> BitmapDescriptorFactory.HUE_YELLOW
                                    else -> BitmapDescriptorFactory.HUE_RED
                                }
                            )
                        )
                    }
                }
                
                // Stats Overlay
                Card(
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .width(200.dp),
                    RoundedCornerShape(12.dp),
                    CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    )
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Tree Statistics",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        )
                        
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Healthy",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
                                )
                            )
                            Text(
                                "${state.healthyTrees}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Green,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Growing",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
                                )
                            )
                            Text(
                                "${state.growingTrees}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Need Care",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
                                )
                            )
                            Text(
                                "${state.needCareTrees}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
