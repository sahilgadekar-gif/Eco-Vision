package com.vasundhara.app.ui.screens.waste

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vasundhara.app.data.model.WasteResult
import com.vasundhara.app.ui.components.*
import com.vasundhara.app.ui.theme.*

@Composable
fun WasteDetectionScreen(vm: WasteViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    VasundharaBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Waste Detection", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
                    Text("AI-powered waste classification", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
                EcoBadge("ResNet50", EcoGreen)
            }
            Spacer(Modifier.height(24.dp))
            
            // Location and River Distance Info
            if (state.currentLocation != null) {
                LocationInfoCard(
                    location = state.currentLocation!!,
                    riverLocationData = state.riverLocationData
                )
                Spacer(Modifier.height(16.dp))
            }
            
            CameraCard(state.hasImage, state.isAnalyzing, vm::capture, vm::upload, vm::reset)
            Spacer(Modifier.height(16.dp))
            AnimatedVisibility(state.isAnalyzing, enter = fadeIn(), exit = fadeOut()) {
                Card(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(Modifier.size(28.dp), color = EcoGreen, strokeWidth = 3.dp)
                        Column { Text("Analyzing waste...", style = MaterialTheme.typography.titleSmall); Text("ResNet50 model processing", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)) }
                    }
                }
            }
            AnimatedVisibility(state.result != null, enter = slideInVertically(tween(500)) { it } + fadeIn(tween(500)), exit = fadeOut()) {
                state.result?.let { WasteResultCard(it) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CameraCard(hasImage: Boolean, isAnalyzing: Boolean, onCapture: () -> Unit, onUpload: () -> Unit, onReset: () -> Unit) {
    val scanAnim = rememberInfiniteTransition(label = "scan")
    val scanY by scanAnim.animateFloat(0f, 1f, infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "scanY")
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(28.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Box(Modifier.fillMaxWidth().height(260.dp), Alignment.Center) {
            if (!hasImage) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(160.dp).clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surfaceVariant), Alignment.Center) {
                        Canvas(Modifier.fillMaxSize()) {
                            val c = 30.dp.toPx(); val sw = 3.dp.toPx(); val p = 12.dp.toPx()
                            listOf(Offset(p, p) to Offset(p + c, p), Offset(p, p) to Offset(p, p + c), Offset(size.width - p - c, p) to Offset(size.width - p, p), Offset(size.width - p, p) to Offset(size.width - p, p + c), Offset(p, size.height - p - c) to Offset(p, size.height - p), Offset(p, size.height - p) to Offset(p + c, size.height - p), Offset(size.width - p - c, size.height - p) to Offset(size.width - p, size.height - p), Offset(size.width - p, size.height - p - c) to Offset(size.width - p, size.height - p)).forEach { (a, b) -> drawLine(EcoGreen, a, b, sw, cap = StrokeCap.Round) }
                            drawLine(EcoGreen.copy(alpha = 0.6f), Offset(p + 4, p + (size.height - 2 * p) * scanY), Offset(size.width - p - 4, p + (size.height - 2 * p) * scanY), 1.5.dp.toPx())
                        }
                        Icon(Icons.Filled.CameraAlt, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(40.dp))
                    }
                    Text("Point camera at waste item", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            } else {
                Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(NavyLight, NavyBase))), Alignment.Center) {
                    Icon(Icons.Filled.Image, null, tint = EcoGreen.copy(alpha = 0.5f), modifier = Modifier.size(80.dp))
                    if (!isAnalyzing) IconButton(onReset, Modifier.align(Alignment.TopEnd).padding(8.dp)) { Icon(Icons.Filled.Close, null, tint = PureWhite) }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onCapture, Modifier.weight(1f), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = EcoGreen, contentColor = PureBlack), enabled = !isAnalyzing) {
                Icon(Icons.Filled.CameraAlt, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Capture")
            }
            OutlinedButton(onUpload, Modifier.weight(1f), shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), enabled = !isAnalyzing) {
                Icon(Icons.Filled.Upload, null, tint = EcoGreen, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Upload", color = EcoGreen)
            }
        }
    }
}

@Composable
private fun WasteResultCard(r: WasteResult) {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(EcoGreen.copy(alpha = 0.15f)), Alignment.Center) { Icon(Icons.Filled.Recycling, null, tint = EcoGreen, modifier = Modifier.size(24.dp)) }
                Column(Modifier.weight(1f)) {
                    Text(r.category, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Confidence:", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        LinearProgressIndicator(progress = { r.confidence }, modifier = Modifier.width(80.dp).height(4.dp).clip(RoundedCornerShape(2.dp)), color = EcoGreen, trackColor = MaterialTheme.colorScheme.surfaceVariant)
                        Text("${(r.confidence * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(color = EcoGreen))
                    }
                }
            }
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(EcoGreen.copy(alpha = 0.08f)).padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Eco, null, tint = EcoGreen, modifier = Modifier.size(14.dp)); Text(r.ecoImpact, style = MaterialTheme.typography.bodySmall.copy(color = EcoGreen))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            SectionHeader("Disposal Steps")
            r.disposalSteps.forEachIndexed { i, step ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                    Box(Modifier.size(22.dp).clip(CircleShape).background(NavyAccent), Alignment.Center) { Text("${i + 1}", style = MaterialTheme.typography.labelSmall.copy(color = PureWhite, fontWeight = FontWeight.Bold)) }
                    Text(step, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            SectionHeader("Recycling Tips")
            r.recyclingTips.forEach { tip ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Lightbulb, null, tint = WarningAmber, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                    Text(tip, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                }
            }
        }
    }
}
