package com.vasundhara.app.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vasundhara.app.data.model.*
import com.vasundhara.app.ui.components.*
import com.vasundhara.app.ui.theme.*

@Composable
fun ProfileScreen(onSettings: () -> Unit, vm: ProfileViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    VasundharaBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            AnimatedContent(state.isLoading, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "profile") { loading ->
                if (loading) Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ShimmerBox(Modifier.fillMaxWidth().height(200.dp), RoundedCornerShape(0.dp))
                    repeat(3) { ShimmerBox(Modifier.fillMaxWidth().height(70.dp), RoundedCornerShape(18.dp)) }
                } else state.user?.let { user ->
                    ProfileHero(user, onSettings)
                    Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Spacer(Modifier.height(4.dp))
                        // Level Progress
                        LevelCard(user)
                        // Stats
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatCard("CO₂ Saved", "%.0f".format(user.totalCO2Saved), "kg", { Icon(Icons.Filled.Eco, null, tint = EcoGreen, modifier = Modifier.size(18.dp)) }, Modifier.weight(1f))
                            StatCard("Streak", "${user.streakDays}", "days", { Icon(Icons.Filled.LocalFire, null, tint = WarningAmber, modifier = Modifier.size(18.dp)) }, Modifier.weight(1f), WarningAmber)
                        }
                        // Achievements
                        SectionHeader("Achievements", "${user.achievements.count { it.isUnlocked }}/${user.achievements.size} unlocked")
                        LazyVerticalGrid(GridCells.Fixed(4), Modifier.fillMaxWidth().heightIn(max = 400.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), userScrollEnabled = false) {
                            items(user.achievements) { a ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(if (a.isUnlocked) EcoGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant).border(1.dp, if (a.isUnlocked) EcoGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)), Alignment.Center) {
                                        Icon(Icons.Filled.EmojiEvents, null, tint = if (a.isUnlocked) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(26.dp))
                                    }
                                    Text(a.title, style = MaterialTheme.typography.labelSmall.copy(color = if (a.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)), maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHero(user: User, onSettings: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(220.dp).background(Brush.verticalGradient(listOf(NavyAccent, NavyBase, MaterialTheme.colorScheme.background)))) {
        Icon(Icons.Filled.Park, null, tint = PureWhite.copy(alpha = 0.04f), modifier = Modifier.size(200.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = (-20).dp))
        IconButton(onClick = onSettings, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) { Icon(Icons.Filled.Settings, null, tint = PureWhite) }
        Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(Modifier.size(80.dp).clip(CircleShape).background(Brush.radialGradient(listOf(EcoGreen, EcoGreenDark))).border(3.dp, EcoGreen, CircleShape), Alignment.Center) {
                Text(user.name.take(1).uppercase(), style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black, color = PureBlack))
            }
            Spacer(Modifier.height(10.dp))
            Text(user.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PureWhite))
            Text(user.email, style = MaterialTheme.typography.bodySmall.copy(color = PureWhite.copy(alpha = 0.7f)))
            Spacer(Modifier.height(8.dp))
            EcoBadge(user.rank + " 🌿", EcoGreen)
        }
    }
}

@Composable
private fun LevelCard(user: User) {
    val level = user.level
    val nextLevel = EcoLevel.values().getOrNull(level.ordinal + 1)
    val progress = if (nextLevel != null) (user.points - level.minPoints).toFloat() / (nextLevel.minPoints - level.minPoints) else 1f
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(level.label, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(level.color)))
                Text("${user.points} pts", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
            LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = Color(level.color), trackColor = MaterialTheme.colorScheme.surfaceVariant)
            nextLevel?.let { Text("${it.minPoints - user.points} pts to ${it.label}", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)) }
        }
    }
}
