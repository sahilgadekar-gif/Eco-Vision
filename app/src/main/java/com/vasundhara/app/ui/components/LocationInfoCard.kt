package com.vasundhara.app.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vasundhara.app.data.model.RiverLocationData

@Composable
fun LocationInfoCard(
    location: androidx.location.Location,
    riverLocationData: RiverLocationData?
) {
    val isDark = isSystemInDarkTheme()
    
    Card(
        Modifier.fillMaxWidth(),
        RoundedCornerShape(16.dp),
        CardDefaults.cardColors(
            containerColor = if (isDark) Color.Black else Color.White
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "📍 Your Location",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
            )
            
            // Coordinates
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Latitude",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDark) Color.LightGray else Color.DarkGray
                        )
                    )
                    Text(
                        "${location.latitude}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isDark) Color.White else Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                Column {
                    Text(
                        "Longitude",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDark) Color.LightGray else Color.DarkGray
                        )
                    )
                    Text(
                        "${location.longitude}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isDark) Color.White else Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            
            // River Distance
            riverLocationData?.let { riverData ->
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Water,
                        contentDescription = null,
                        tint = Color.Blue,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Distance to ${riverData.riverName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            )
                        )
                        Text(
                            "${"%.1f".format(riverData.distanceInMeters)} meters",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    
                    // Distance Alert
                    if (riverData.distanceInMeters <= 50) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (riverData.distanceInMeters <= 25) 
                                        Color.Red.copy(alpha = 0.2f)
                                    else 
                                        Color(0xFFFF9800).copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                if (riverData.distanceInMeters <= 25) "🚨 Very Close" else "⚠️ Near River",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (riverData.distanceInMeters <= 25) Color.Red else Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                
                // River Coordinates
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "River Latitude",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            )
                        )
                        Text(
                            "${riverData.riverLat}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                    }
                    
                    Column {
                        Text(
                            "River Longitude",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            )
                        )
                        Text(
                            "${riverData.riverLng}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                    }
                }
            }
        }
    }
}
