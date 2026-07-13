package com.vasundhara.app.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vasundhara.app.data.model.*
import com.vasundhara.app.ui.components.*
import com.vasundhara.app.ui.navigation.Screen
import com.vasundhara.app.ui.theme.*

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit, vm: DashboardViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    VasundharaBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            DashboardHeader()
            Spacer(Modifier.height(24.dp))
            AnimatedContent(state.isLoading, transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) }, label = "dash") { loading ->
                if (loading) DashboardSkeleton()
                else state.stats?.let { DashboardContent(it, onNavigate) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DashboardHeader() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text("Good Morning 🌿", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            Text("Vasundhara", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = {}) { Icon(Icons.Filled.Notifications, null, tint = MaterialTheme.colorScheme.onSurface) }
        }
    }
}

@Composable
private fun DashboardContent(stats: DashboardStats, onNavigate: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Hero CO₂ Card
        HeroCo2Card(stats.co2SavedKg)

        // Score + Streak Row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(Modifier.weight(1f), RoundedCornerShape(24.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    EcoScoreRing(stats.ecoScore, size = 130.dp)
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Streak", "${stats.streakDays}", "days", { Icon(Icons.Filled.LocalFire, null, tint = WarningAmber, modifier = Modifier.size(18.dp)) }, Modifier.fillMaxWidth(), WarningAmber)
                StatCard("Points", "${stats.points}", "pts", { Icon(Icons.Filled.Stars, null, tint = InfoBlue, modifier = Modifier.size(18.dp)) }, Modifier.fillMaxWidth(), InfoBlue)
                EcoBadge(stats.rank)
            }
        }

        // RHI Quick Card
        RhiQuickCard(stats.rhiScore, onClick = { onNavigate(Screen.Rhi.route) })

        // Weekly Trend
        Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(20.dp)) {
                SectionHeader("Weekly Carbon Trend", "kg CO₂ per day")
                Spacer(Modifier.height(16.dp))
                VasundharaLineChart(stats.weeklyTrend, Modifier.fillMaxWidth().height(130.dp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    stats.weeklyTrend.forEach { Text(it.label.take(3), style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)) }
                }
            }
        }

        // Active Challenges
        if (stats.activeChallenges.isNotEmpty()) {
            SectionHeader("Today's Challenges")
            stats.activeChallenges.forEach { ChallengeCard(it) }
        }

        // Quick Actions
        SectionHeader("Quick Actions")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickAction("Scan Waste", Icons.Filled.CameraAlt, EcoGreen, Modifier.weight(1f)) { onNavigate(Screen.Waste.route) }
            QuickAction("Eco Map", Icons.Filled.Map, InfoBlue, Modifier.weight(1f)) { onNavigate(Screen.EcoMap.route) }
            QuickAction("Leaderboard", Icons.Filled.EmojiEvents, WarningAmber, Modifier.weight(1f)) { onNavigate(Screen.Gamification.route) }
        }
    }
}

@Composable
private fun HeroCo2Card(co2: Float) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val anim by animateFloatAsState(if (visible) co2 else 0f, tween(1600, easing = FastOutSlowInEasing), label = "co2")
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(NavyAccent, NavyLight, EcoGreenDark.copy(alpha = 0.4f))))
            .padding(24.dp)
    ) {
        Column {
            Text("Total CO₂ Saved", style = MaterialTheme.typography.labelLarge.copy(color = PureWhite.copy(alpha = 0.7f), letterSpacing = 1.5.sp))
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("%.1f".format(anim), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black, color = PureWhite))
                Text(" kg", style = MaterialTheme.typography.headlineSmall.copy(color = EcoGreen, fontWeight = FontWeight.Bold), modifier = Modifier.padding(bottom = 6.dp))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.TrendingDown, null, tint = EcoGreen, modifier = Modifier.size(14.dp))
                Text("12% less than last month", style = MaterialTheme.typography.bodySmall.copy(color = EcoGreen))
            }
        }
        Icon(Icons.Filled.Park, null, tint = PureWhite.copy(alpha = 0.06f), modifier = Modifier.size(110.dp).align(Alignment.CenterEnd))
    }
}

@Composable
private fun RhiQuickCard(score: Float, onClick: () -> Unit) {
    val color = when { score >= 70 -> RhiHealthy; score >= 40 -> RhiModerate; else -> RhiCritical }
    val status = when { score >= 70 -> "Healthy"; score >= 40 -> "Moderate"; else -> "Critical" }
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(20.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(color.copy(alpha = 0.15f)), Alignment.Center) {
                Icon(Icons.Filled.Water, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("River Health Index", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                Text("Tap to view details", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("%.0f".format(score), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = color))
                EcoBadge(status, color)
            }
        }
    }
}

@Composable
private fun ChallengeCard(c: DailyChallenge) {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(if (c.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked, null, tint = if (c.isCompleted) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
            Column(Modifier.weight(1f)) {
                Text(c.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                LinearProgressIndicator(progress = { c.progress }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(4.dp).clip(RoundedCornerShape(2.dp)), color = EcoGreen, trackColor = MaterialTheme.colorScheme.surfaceVariant)
            }
            EcoBadge("+${c.pointsReward}pts", EcoGreen)
        }
    }
}

@Composable
private fun QuickAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier.clickable(onClick = onClick), RoundedCornerShape(18.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)), Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant), maxLines = 1)
        }
    }
}

@Composable
private fun DashboardSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        ShimmerBox(Modifier.fillMaxWidth().height(130.dp), RoundedCornerShape(28.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ShimmerBox(Modifier.weight(1f).height(170.dp), RoundedCornerShape(24.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ShimmerBox(Modifier.fillMaxWidth().height(75.dp), RoundedCornerShape(20.dp))
                ShimmerBox(Modifier.fillMaxWidth().height(75.dp), RoundedCornerShape(20.dp))
            }
        }
        ShimmerBox(Modifier.fillMaxWidth().height(190.dp), RoundedCornerShape(24.dp))
    }
}
