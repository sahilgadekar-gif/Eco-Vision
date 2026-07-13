package com.vasundhara.app.ui.screens.carbon

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vasundhara.app.ui.components.*
import com.vasundhara.app.ui.theme.*
import com.vasundhara.app.data.model.*

@Composable
fun CarbonTrackerScreen(vm: CarbonViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val co2 = state.input.calculateCo2()
    var showProfile by remember { mutableStateOf(false) }
    var showLeaderboard by remember { mutableStateOf(false) }
    
    VasundharaBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            
            // Header with Rank and Three Dots Menu
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Carbon Tracker", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
                    Text("Calculate your daily footprint", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                
                // Rank Display with Three Dots Menu
                Box {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { showProfile = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "#${state.profile?.rank?.currentRank ?: "--"}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    
                    DropdownMenu(
                        expanded = showProfile,
                        onDismissRequest = { showProfile = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("View Profile") },
                            onClick = { showProfile = false },
                            leadingIcon = { Icon(Icons.Default.Person, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Leaderboard") },
                            onClick = { showLeaderboard = true; showProfile = false },
                            leadingIcon = { Icon(Icons.Default.Leaderboard, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Badges") },
                            onClick = { showProfile = false },
                            leadingIcon = { Icon(Icons.Default.EmojiEvents, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Goals") },
                            onClick = { showProfile = false },
                            leadingIcon = { Icon(Icons.Default.Flag, null) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            // Live CO₂ Hero - Black and White Theme
            val animCo2 by animateFloatAsState(co2, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "co2")
            val co2Color = when { 
                co2 < 10f -> if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
                co2 < 30f -> if (isSystemInDarkTheme()) Color.Gray else Color.Black
                else -> if (isSystemInDarkTheme()) Color.White else Color.Black
            }
            
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(
                if (isSystemInDarkTheme()) Color.Black else Color.White
            ).padding(24.dp)) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Today's Footprint", style = MaterialTheme.typography.labelLarge.copy(
                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
                    ))
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("%.1f".format(animCo2), style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black, 
                            color = co2Color
                        ))
                        Text(" kg CO₂", style = MaterialTheme.typography.titleLarge.copy(
                            color = if (isSystemInDarkTheme()) Color.Gray else Color.DarkGray
                        ), modifier = Modifier.padding(bottom = 6.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(when { 
                        co2 < 10f -> "🌿 Low Impact — Great job!"
                        co2 < 30f -> "⚠️ Moderate — Room to improve"
                        else -> "🔴 High Impact — Take action"
                    }, style = MaterialTheme.typography.bodySmall.copy(color = co2Color))
                }
            }
            Spacer(Modifier.height(20.dp))

            // Enhanced Sliders - Black and White Theme
            Card(
                Modifier.fillMaxWidth(), 
                RoundedCornerShape(24.dp), 
                CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                )
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    SectionHeader("Daily Activities", "Adjust to calculate")
                    CarbonSlider("Travel", state.input.travelKm, vm::updateTravel, 0f..200f, "km", Icons.Filled.DirectionsCar, co2Color, state.input.travelKm * 0.21f)
                    CarbonSlider("Electricity", state.input.electricityKwh, vm::updateElectricity, 0f..100f, "kWh", Icons.Filled.Bolt, co2Color, state.input.electricityKwh * 0.82f)
                    CarbonSlider("Meat Meals", state.input.foodMeatServings, vm::updateFood, 0f..10f, "servings", Icons.Filled.Restaurant, co2Color, state.input.foodMeatServings * 3.3f)
                    CarbonSlider("Water", state.input.waterLitres, vm::updateWater, 0f..500f, "L", Icons.Filled.Water, co2Color, state.input.waterLitres * 0.0003f)
                    CarbonSlider("Waste", state.input.wasteKg, vm::updateWaste, 0f..50f, "kg", Icons.Filled.Delete, co2Color, state.input.wasteKg * 0.5f)
                    CarbonSlider("Shopping", state.input.shoppingItems.toFloat(), { vm::updateShopping(it.toInt()) }, 0f..20f, "items", Icons.Filled.ShoppingCart, co2Color, state.input.shoppingItems * 2.0f)
                    CarbonSlider("Digital", state.input.digitalHours, vm::updateDigital, 0f..24f, "hours", Icons.Filled.Computer, co2Color, state.input.digitalHours * 0.05f)
                }
            }
            Spacer(Modifier.height(20.dp))

            // Breakdown - Black and White Theme
            if (co2 > 0f) {
                Card(
                    Modifier.fillMaxWidth(), 
                    RoundedCornerShape(24.dp), 
                    CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    )
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader("Emission Breakdown")
                        EmissionBar("Travel", state.input.travelKm * 0.21f, co2, co2Color)
                        EmissionBar("Electricity", state.input.electricityKwh * 0.82f, co2, co2Color)
                        EmissionBar("Food", state.input.foodMeatServings * 3.3f, co2, co2Color)
                        EmissionBar("Water", state.input.waterLitres * 0.0003f, co2, co2Color)
                        EmissionBar("Waste", state.input.wasteKg * 0.5f, co2, co2Color)
                        EmissionBar("Shopping", state.input.shoppingItems * 2.0f, co2, co2Color)
                        EmissionBar("Digital", state.input.digitalHours * 0.05f, co2, co2Color)
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // History - Black and White Theme
            if (state.history.isNotEmpty()) {
                Card(
                    Modifier.fillMaxWidth(), 
                    RoundedCornerShape(24.dp), 
                    CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    )
                ) {
                    Column(Modifier.padding(20.dp)) {
                        SectionHeader("8-Week History", "kg CO₂ per week")
                        Spacer(Modifier.height(16.dp))
                        VasundharaLineChart(
                            state.history, 
                            Modifier.fillMaxWidth().height(130.dp),
                            lineColor = co2Color
                        )
                    }
                }
            }
            
            // Comparison Section - Black and White Theme
            state.profile?.comparisons?.let { comparisons ->
                Spacer(Modifier.height(20.dp))
                Card(
                    Modifier.fillMaxWidth(), 
                    RoundedCornerShape(24.dp), 
                    CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    )
                ) {
                    Column(Modifier.padding(20.dp)) {
                        SectionHeader("How You Compare", "kg CO₂ per day")
                        Spacer(Modifier.height(16.dp))
                        ComparisonBar("You", co2, co2Color)
                        ComparisonBar("City Average", comparisons.cityAverage, Color.Gray)
                        ComparisonBar("National Average", comparisons.nationalAverage, Color.DarkGray)
                        ComparisonBar("Global Average", comparisons.globalAverage, Color.LightGray)
                    }
                }
            }
            
            // Save Button
            if (co2 > 0f) {
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { vm.saveCarbonLog() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save Today's Carbon Footprint", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
    
    // Profile Dialog
    if (showProfile) {
        CarbonProfileDialog(
            profile = state.profile,
            onDismiss = { showProfile = false }
        )
    }
    
    // Leaderboard Dialog
    if (showLeaderboard) {
        CarbonLeaderboardDialog(
            leaderboard = state.leaderboard,
            onDismiss = { showLeaderboard = false }
        )
    }
}

@Composable
private fun CarbonSlider(
    label: String, 
    value: Float, 
    onChange: (Float) -> Unit, 
    range: ClosedFloatingPointRange<Float>, 
    unit: String, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    color: Color, 
    co2: Float
) {
    val isDark = isSystemInDarkTheme()
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
                Text(label, style = MaterialTheme.typography.titleSmall, color = if (isDark) Color.White else Color.Black)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("%.0f $unit".format(value), style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isDark) Color.LightGray else Color.DarkGray
                ))
                EcoBadge("%.1f kg".format(co2), color)
            }
        }
        Slider(
            value = value, 
            onValueChange = onChange, 
            valueRange = range, 
            colors = SliderDefaults.colors(
                thumbColor = color, 
                activeTrackColor = color, 
                inactiveTrackColor = if (isDark) Color.DarkGray else Color.LightGray
            )
        )
    }
}

@Composable
private fun EmissionBar(label: String, value: Float, total: Float, color: Color) {
    val isDark = isSystemInDarkTheme()
    val frac = if (total > 0f) value / total else 0f
    val anim by animateFloatAsState(frac, tween(800, easing = FastOutSlowInEasing), label = "bar")
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall.copy(
                color = if (isDark) Color.LightGray else Color.DarkGray
            ))
            Text("%.1f kg (%.0f%%)".format(value, frac * 100), style = MaterialTheme.typography.bodySmall.copy(color = color))
        }
        Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(
            if (isDark) Color.DarkGray else Color.LightGray
        )) {
            Box(Modifier.fillMaxWidth(anim).fillMaxHeight().clip(RoundedCornerShape(4.dp)).background(color))
        }
    }
}

@Composable
private fun ComparisonBar(label: String, value: Float, color: Color) {
    val isDark = isSystemInDarkTheme()
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(
            color = if (isDark) Color.White else Color.Black,
            fontWeight = if (label == "You") FontWeight.Bold else FontWeight.Normal
        ))
        Text("%.1f kg".format(value), style = MaterialTheme.typography.bodyMedium.copy(
            color = color,
            fontWeight = if (label == "You") FontWeight.Bold else FontWeight.Normal
        ))
    }
}

@Composable
private fun CarbonProfileDialog(
    profile: PersonalCarbonProfile?,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text("Carbon Profile", color = if (isDark) Color.White else Color.Black)
        },
        text = {
            if (profile != null) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Rank: #${profile.rank.currentRank} - ${profile.rank.rankTitle}", color = if (isDark) Color.White else Color.Black)
                    Text("Percentile: ${"%.1f".format(profile.rank.percentile)}%", color = if (isDark) Color.White else Color.Black)
                    Text("Weekly Average: ${"%.1f".format(profile.weeklyAverage)} kg CO₂", color = if (isDark) Color.White else Color.Black)
                    
                    Text("Badges:", style = MaterialTheme.typography.titleSmall, color = if (isDark) Color.White else Color.Black)
                    profile.badges.take(3).forEach { badge ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(badge.icon, style = MaterialTheme.typography.bodyLarge)
                            Column {
                                Text(badge.title, style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color.White else Color.Black
                                ))
                                Text(badge.description, style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                ))
                            }
                        }
                    }
                }
            } else {
                Text("Loading profile...", color = if (isDark) Color.White else Color.Black)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = if (isDark) Color.White else Color.Black)
            }
        },
        containerColor = if (isDark) Color.Black else Color.White
    )
}

@Composable
private fun CarbonLeaderboardDialog(
    leaderboard: CarbonLeaderboard?,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text("Leaderboard", color = if (isDark) Color.White else Color.Black)
        },
        text = {
            if (leaderboard != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    leaderboard.global.take(10).forEach { entry ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("#${entry.rank}", style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color.Black
                                ))
                                Text(entry.name, style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isDark) Color.White else Color.Black
                                ))
                            }
                            Text("${"%.1f".format(entry.ecoScore)} kg", style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            ))
                        }
                    }
                }
            } else {
                Text("Loading leaderboard...", color = if (isDark) Color.White else Color.Black)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = if (isDark) Color.White else Color.Black)
            }
        },
        containerColor = if (isDark) Color.Black else Color.White
    )
}
