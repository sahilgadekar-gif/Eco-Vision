package com.vasundhara.app.ui.screens.river

import androidx.compose.animation.core.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.vasundhara.app.ui.components.*
import com.vasundhara.app.ui.theme.*
import com.vasundhara.app.data.model.*

@Composable
fun RiverMonitoringScreen(vm: RiverMonitoringViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var selectedHotspot by remember { mutableStateOf<RiverHotspot?>(null) }
    
    VasundharaBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            
            // Header
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("River Monitoring", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
                    Text("Report and track pollution hotspots", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                
                Button(
                    onClick = { vm.refreshHotspots() },
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
            Spacer(Modifier.height(20.dp))

            // Filter Chips
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip("All", state.filter == "All") { vm.updateFilter("All") }
                FilterChip("Pending", state.filter == "Pending Cleanup") { vm.updateFilter("Pending Cleanup") }
                FilterChip("In Progress", state.filter == "In Progress") { vm.updateFilter("In Progress") }
                FilterChip("Cleaned", state.filter == "Cleaned") { vm.updateFilter("Cleaned") }
            }
            Spacer(Modifier.height(16.dp))

            // Hotspots List
            state.hotspots.forEach { hotspot ->
                HotspotCard(
                    hotspot = hotspot,
                    onClick = { selectedHotspot = hotspot },
                    onUserClick = { vm.getUserProfile(hotspot.reporterId) }
                )
                Spacer(Modifier.height(12.dp))
            }
            
            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
    
    // Hotspot Detail Dialog
    selectedHotspot?.let { hotspot ->
        HotspotDetailDialog(
            hotspot = hotspot,
            onDismiss = { selectedHotspot = null },
            onStatusUpdate = { status -> 
                vm.updateHotspotStatus(hotspot.id, status)
                selectedHotspot = null 
            }
        )
    }
    
    // User Profile Dialog
    state.selectedUser?.let { user ->
        UserProfileDialog(
            user = user,
            onDismiss = { vm.clearSelectedUser() }
        )
    }
}

@Composable
private fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
            selectedLabelColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            unselectedContainerColor = MaterialTheme.colorScheme.surface,
            unselectedLabelColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun HotspotCard(
    hotspot: RiverHotspot,
    onClick: () -> Unit,
    onUserClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    Card(
        Modifier.fillMaxWidth().clickable { onClick() },
        RoundedCornerShape(16.dp),
        CardDefaults.cardColors(
            containerColor = if (isDark) Color.Black else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Image
            AsyncImage(
                model = hotspot.imageUrl,
                contentDescription = hotspot.location,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Content
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            hotspot.location,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            hotspot.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            ),
                            maxLines = 2
                        )
                    }
                    
                    // Status Badge
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (hotspot.status) {
                                    "Pending Cleanup" -> Color.Red
                                    "In Progress" -> Color(0xFFFF9800)
                                    "Cleaned" -> Color(0xFF4CAF50)
                                    else -> Color.Gray
                                }.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            hotspot.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when (hotspot.status) {
                                    "Pending Cleanup" -> Color.Red
                                    "In Progress" -> Color(0xFFFF9800)
                                    "Cleaned" -> Color(0xFF4CAF50)
                                    else -> Color.Gray
                                },
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                
                // Reporter and Severity
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier.clickable { onUserClick() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isDark) Color.LightGray else Color.DarkGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                "Reported by ${hotspot.reporterName}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color.White else Color.Black
                                )
                            )
                            Text(
                                "ID: ${hotspot.reporterId}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            )
                        }
                    }
                    
                    // Severity
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            when (hotspot.severity) {
                                "High" -> Icons.Default.Warning
                                "Medium" -> Icons.Default.Info
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = when (hotspot.severity) {
                                "High" -> Color.Red
                                "Medium" -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            hotspot.severity,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = when (hotspot.severity) {
                                    "High" -> Color.Red
                                    "Medium" -> Color(0xFFFF9800)
                                    else -> Color(0xFF4CAF50)
                                },
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                
                // Municipal Notification Status
                if (hotspot.severity == "High" && hotspot.municipalNotified) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "Municipal Corporation Notified",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HotspotDetailDialog(
    hotspot: RiverHotspot,
    onDismiss: () -> Unit,
    onStatusUpdate: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = { 
            Text(
                hotspot.location,
                color = if (isDark) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Image
                AsyncImage(
                    model = hotspot.imageUrl,
                    contentDescription = hotspot.location,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Description
                Text(
                    "Description:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = if (isDark) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    hotspot.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isDark) Color.LightGray else Color.DarkGray
                    )
                )
                
                // Details
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Reporter:", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Text(
                            hotspot.reporterName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                    }
                    Column {
                        Text("Severity:", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Text(
                            hotspot.severity,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = when (hotspot.severity) {
                                    "High" -> Color.Red
                                    "Medium" -> Color(0xFFFF9800)
                                    else -> Color(0xFF4CAF50)
                                },
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                
                // Municipal Alert
                if (hotspot.severity == "High") {
                    Box(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Red.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Column(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                                Text(
                                    "🚨 URGENT ATTENTION REQUIRED 🚨",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                "Municipal Corporation has been notified for immediate cleanup action.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Red)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (hotspot.status != "Cleaned") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onStatusUpdate("In Progress") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mark In Progress")
                    }
                    
                    Button(
                        onClick = { onStatusUpdate("Cleaned") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mark Cleaned")
                    }
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color.White else Color.Black,
                        contentColor = if (isDark) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close")
                }
            }
        },
        containerColor = if (isDark) Color.Black else Color.White
    )
}

@Composable
private fun UserProfileDialog(
    user: UserProfile,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "User Profile",
                color = if (isDark) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Profile Image
                if (user.profileImageUrl != null) {
                    AsyncImage(
                        model = user.profileImageUrl,
                        contentDescription = user.name,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        Modifier.size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user.name.first().toString(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                
                // User Details
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Name", user.name)
                    DetailRow("Email", user.email)
                    DetailRow("Phone", user.phone)
                    DetailRow("Address", user.address)
                    DetailRow("Member Since", user.joinDate)
                    DetailRow("Total Reports", user.totalReports.toString())
                    DetailRow("Verified Reports", user.verifiedReports.toString())
                    DetailRow("Citizen Score", "${user.citizenScore} points")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color.White else Color.Black,
                    contentColor = if (isDark) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        },
        containerColor = if (isDark) Color.Black else Color.White
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    val isDark = isSystemInDarkTheme()
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isDark) Color.LightGray else Color.DarkGray
            )
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isDark) Color.White else Color.Black,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
