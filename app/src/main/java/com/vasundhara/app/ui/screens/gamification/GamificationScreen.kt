package com.vasundhara.app.ui.screens.gamification

import androidx.compose.animation.core.*
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
fun GamificationScreen(vm: GamificationViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var tab by remember { mutableIntStateOf(0) }

    VasundharaBackground {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(16.dp))
                Text("Challenges & Rewards", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
                Spacer(Modifier.height(16.dp))
                TabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.surface, contentColor = EcoGreen) {
                    listOf("Challenges", "Leaderboard", "Badges").forEachIndexed { i, t ->
                        Tab(selected = tab == i, onClick = { tab = i }, text = { Text(t, style = MaterialTheme.typography.labelLarge) })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            when (tab) {
                0 -> ChallengesTab(state.challenges, vm::complete)
                1 -> LeaderboardTab(state.leaderboard)
                2 -> BadgesTab(state.achievements)
            }
        }
    }
}

@Composable
private fun ChallengesTab(challenges: List<DailyChallenge>, onComplete: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        challenges.forEach { c ->
            Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(if (c.isCompleted) EcoGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant), Alignment.Center) {
                            Icon(if (c.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.EmojiEvents, null, tint = if (c.isCompleted) EcoGreen else WarningAmber, modifier = Modifier.size(22.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(c.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(c.description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                        EcoBadge("+${c.pointsReward}", if (c.isCompleted) EcoGreen else WarningAmber)
                    }
                    LinearProgressIndicator(progress = { c.progress }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)), color = EcoGreen, trackColor = MaterialTheme.colorScheme.surfaceVariant)
                    if (!c.isCompleted) {
                        Button(onClick = { onComplete(c.id) }, modifier = Modifier.fillMaxWidth().height(40.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = EcoGreen, contentColor = PureBlack)) {
                            Text("Mark Complete", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun LeaderboardTab(entries: List<LeaderboardEntry>) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        entries.forEach { e ->
            val bg = if (e.isCurrentUser) EcoGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
            val rankColor = when (e.rank) { 1 -> Color(0xFFFFD700); 2 -> Color(0xFFC0C0C0); 3 -> Color(0xFFCD7F32); else -> MaterialTheme.colorScheme.onSurfaceVariant }
            Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), CardDefaults.cardColors(bg)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("#${e.rank}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = rankColor), modifier = Modifier.width(36.dp))
                    Box(Modifier.size(40.dp).clip(CircleShape).background(Brush.radialGradient(listOf(EcoGreen, EcoGreenDark))), Alignment.Center) {
                        Text(e.name.take(1), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PureBlack))
                    }
                    Text(e.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (e.isCurrentUser) FontWeight.Bold else FontWeight.Normal), modifier = Modifier.weight(1f))
                    Text("${e.ecoScore}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EcoGreen))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun BadgesTab(achievements: List<Achievement>) {
    LazyVerticalGrid(GridCells.Fixed(3), Modifier.fillMaxSize().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
        items(achievements) { a ->
            Card(Modifier.aspectRatio(0.85f), RoundedCornerShape(18.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
                Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Box(Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(if (a.isUnlocked) EcoGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant).border(1.dp, if (a.isUnlocked) EcoGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)), Alignment.Center) {
                        Icon(Icons.Filled.EmojiEvents, null, tint = if (a.isUnlocked) EcoGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(a.title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, color = if (a.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)), maxLines = 1)
                    if (!a.isUnlocked) {
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(progress = { a.progress }, modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)), color = EcoGreen, trackColor = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        }
    }
}
