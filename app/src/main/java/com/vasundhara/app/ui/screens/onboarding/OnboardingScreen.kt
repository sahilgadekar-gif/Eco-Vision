package com.vasundhara.app.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vasundhara.app.ui.components.VasundharaBackground
import com.vasundhara.app.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardPage(val icon: ImageVector, val title: String, val subtitle: String, val accent: Color)

val onboardPages = listOf(
    OnboardPage(Icons.Filled.Park, "Welcome to Vasundhara", "Your personal AI environmental intelligence platform for a sustainable future.", EcoGreen),
    OnboardPage(Icons.Filled.Analytics, "Track Your Impact", "Monitor CO₂ emissions, river health, and waste — all in one place with real-time insights.", InfoBlue),
    OnboardPage(Icons.Filled.AutoAwesome, "AI-Powered Guidance", "Get personalized eco-tips, waste detection, and smart recommendations powered by AI.", WarningAmber),
    OnboardPage(Icons.Filled.EmojiEvents, "Earn & Compete", "Complete daily challenges, earn badges, and climb the leaderboard as an Eco Champion.", EcoGreen),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val pager = rememberPagerState { onboardPages.size }
    val scope = rememberCoroutineScope()

    VasundharaBackground {
        Column(Modifier.fillMaxSize()) {
            HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { page ->
                OnboardPage(onboardPages[page])
            }

            // Dots + Buttons
            Column(Modifier.padding(horizontal = 28.dp, vertical = 32.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    repeat(onboardPages.size) { i ->
                        val selected = pager.currentPage == i
                        Box(
                            Modifier.padding(horizontal = 4.dp)
                                .clip(CircleShape)
                                .background(if (selected) EcoGreen else MaterialTheme.colorScheme.surfaceVariant)
                                .size(if (selected) 24.dp else 8.dp, 8.dp)
                        )
                    }
                }
                if (pager.currentPage < onboardPages.size - 1) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = onDone) { Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Button(
                            onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreen, contentColor = PureBlack)
                        ) { Text("Next", style = MaterialTheme.typography.labelLarge) }
                    }
                } else {
                    Button(
                        onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen, contentColor = PureBlack)
                    ) { Text("Get Started", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)) }
                }
            }
        }
    }
}

@Composable
private fun OnboardPage(page: OnboardPage) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(140.dp).clip(RoundedCornerShape(40.dp))
                .background(Brush.radialGradient(listOf(page.accent.copy(alpha = 0.3f), page.accent.copy(alpha = 0.05f)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(page.icon, null, tint = page.accent, modifier = Modifier.size(72.dp))
        }
        Spacer(Modifier.height(40.dp))
        Text(page.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, textAlign = TextAlign.Center))
        Spacer(Modifier.height(16.dp))
        Text(page.subtitle, style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}
