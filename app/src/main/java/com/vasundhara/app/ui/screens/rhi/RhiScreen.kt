package com.vasundhara.app.ui.screens.rhi

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vasundhara.app.data.model.RhiStatus
import com.vasundhara.app.ui.components.*
import com.vasundhara.app.ui.theme.*

@Composable
fun RhiScreen(vm: RhiViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    VasundharaBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            Text("River Health Index", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
            Text("RHI = f(pollution, waste, industry, carbon)", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(Modifier.height(24.dp))

            // Gauge
            Card(Modifier.fillMaxWidth(), RoundedCornerShape(28.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    state.result?.let { rhi ->
                        val color = Color(rhi.status.colorHex)
                        GaugeMeter(rhi.score, size = 200.dp)
                        Spacer(Modifier.height(8.dp))
                        EcoBadge(rhi.status.label, color)
                        Spacer(Modifier.height(16.dp))
                        // Trend
                        SectionHeader("Historical Trend")
                        Spacer(Modifier.height(8.dp))
                        VasundharaLineChart(rhi.trend, Modifier.fillMaxWidth().height(100.dp), color)
                    } ?: run {
                        GaugeMeter(state.inputs.let { i -> (100f - (i.waterPollution * 0.3f + i.wasteDumped * 0.25f + i.industrial * 0.25f + i.carbon * 0.2f)).coerceIn(0f, 100f) }, size = 200.dp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Input Sliders
            Card(Modifier.fillMaxWidth(), RoundedCornerShape(24.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionHeader("Input Parameters", "Adjust to calculate RHI")
                    RhiSlider("Water Pollution", state.inputs.waterPollution, { vm.update(it, null, null, null) }, RhiCritical)
                    RhiSlider("Waste Dumped", state.inputs.wasteDumped, { vm.update(null, it, null, null) }, WarningAmber)
                    RhiSlider("Industrial Impact", state.inputs.industrial, { vm.update(null, null, it, null) }, InfoBlue)
                    RhiSlider("Carbon in River", state.inputs.carbon, { vm.update(null, null, null, it) }, EcoGreen)
                    Button(
                        onClick = vm::calculate, modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreen, contentColor = PureBlack),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) CircularProgressIndicator(Modifier.size(18.dp), color = PureBlack, strokeWidth = 2.dp)
                        else Text("Calculate RHI", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // Suggestions
            state.result?.let { rhi ->
                Spacer(Modifier.height(20.dp))
                SectionHeader("Improvement Suggestions")
                Spacer(Modifier.height(8.dp))
                rhi.suggestions.forEach { tip ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Lightbulb, null, tint = WarningAmber, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                        Text(tip, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RhiSlider(label: String, value: Float, onChange: (Float) -> Unit, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            EcoBadge("%.0f%%".format(value), color)
        }
        Slider(value = value, onValueChange = onChange, valueRange = 0f..100f,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color, inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant))
    }
}
