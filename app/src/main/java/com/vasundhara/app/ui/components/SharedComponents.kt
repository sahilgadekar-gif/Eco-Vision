package com.vasundhara.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.vasundhara.app.data.model.TrendPoint
import com.vasundhara.app.ui.theme.*

// ── Gradient Background ───────────────────────────────────────────────────────
@Composable
fun VasundharaBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val isDark = LocalDarkTheme.current
    Box(
        modifier = modifier.fillMaxSize().background(
            if (isDark) Brush.verticalGradient(listOf(DarkBg, NavyBase, DarkBg))
            else Brush.verticalGradient(listOf(LightBg, LightSurfaceVar, LightBg))
        ),
        content = content
    )
}

// ── Eco Score Ring ────────────────────────────────────────────────────────────
@Composable
fun EcoScoreRing(score: Int, modifier: Modifier = Modifier, size: Dp = 160.dp, strokeWidth: Dp = 12.dp) {
    val progress by animateFloatAsState(score / 100f, tween(1400, easing = FastOutSlowInEasing), label = "ring")
    val animScore by animateIntAsState(score, tween(1400), label = "score")
    val isDark = LocalDarkTheme.current

    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val d = this.size.minDimension - sw
            val tl = Offset((this.size.width - d) / 2f, (this.size.height - d) / 2f)
            val sz = Size(d, d)
            drawArc(if (isDark) NavyLight else LightOutline, -90f, 360f, false, tl, sz, style = Stroke(sw, cap = StrokeCap.Round))
            drawArc(
                Brush.sweepGradient(listOf(EcoGreenDark, EcoGreen, EcoGreenDim), Offset(this.size.width / 2f, this.size.height / 2f)),
                -90f, 360f * progress, false, tl, sz, style = Stroke(sw, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$animScore", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black, color = EcoGreen))
            Text("ECO SCORE", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp))
        }
    }
}

// ── Line Chart ────────────────────────────────────────────────────────────────
@Composable
fun VasundharaLineChart(data: List<TrendPoint>, modifier: Modifier = Modifier, lineColor: Color = ChartLine) {
    val progress by animateFloatAsState(1f, tween(1200, easing = FastOutSlowInEasing), label = "chart")
    val isDark = LocalDarkTheme.current
    Canvas(modifier) {
        if (data.size < 2) return@Canvas
        val maxV = data.maxOf { it.value }; val minV = data.minOf { it.value }
        val range = (maxV - minV).coerceAtLeast(1f)
        val stepX = size.width / (data.size - 1)
        val pts = data.mapIndexed { i, d ->
            Offset(i * stepX, size.height - ((d.value - minV) / range) * size.height * 0.85f - size.height * 0.075f)
        }
        val visible = pts.take((pts.size * progress).toInt().coerceAtLeast(1))
        repeat(4) { i -> drawLine(if (isDark) ChartGrid else ChartGridLight, Offset(0f, size.height * (i + 1) / 5f), Offset(size.width, size.height * (i + 1) / 5f), 1.dp.toPx()) }
        if (visible.size < 2) return@Canvas
        val fill = Path().apply { moveTo(visible.first().x, size.height); visible.forEach { lineTo(it.x, it.y) }; lineTo(visible.last().x, size.height); close() }
        drawPath(fill, Brush.verticalGradient(listOf(lineColor.copy(alpha = 0.3f), Color.Transparent)))
        val line = Path().apply {
            moveTo(visible.first().x, visible.first().y)
            for (i in 1 until visible.size) {
                val p = visible[i - 1]; val c = visible[i]; val cx = (p.x + c.x) / 2f
                cubicTo(cx, p.y, cx, c.y, c.x, c.y)
            }
        }
        drawPath(line, lineColor, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
        visible.forEach { drawCircle(lineColor, 4.dp.toPx(), it); drawCircle(if (isDark) DarkBg else LightBg, 2.dp.toPx(), it) }
    }
}

// ── Gauge Meter (RHI) ─────────────────────────────────────────────────────────
@Composable
fun GaugeMeter(score: Float, modifier: Modifier = Modifier, size: Dp = 200.dp) {
    val progress by animateFloatAsState(score / 100f, tween(1600, easing = FastOutSlowInEasing), label = "gauge")
    val color = when {
        score >= 70 -> RhiHealthy; score >= 40 -> RhiModerate; else -> RhiCritical
    }
    Box(modifier.size(size), contentAlignment = Alignment.BottomCenter) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = 18.dp.toPx(); val d = this.size.minDimension - sw
            val tl = Offset((this.size.width - d) / 2f, (this.size.height - d) / 2f)
            val sz = Size(d, d)
            drawArc(NavyLight, 150f, 240f, false, tl, sz, style = Stroke(sw, cap = StrokeCap.Round))
            drawArc(
                Brush.sweepGradient(listOf(RhiCritical, RhiModerate, RhiHealthy), Offset(this.size.width / 2f, this.size.height / 2f)),
                150f, 240f * progress, false, tl, sz, style = Stroke(sw, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("%.0f".format(score), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = color))
            Text("/100", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}

// ── Stat Card ─────────────────────────────────────────────────────────────────
@Composable
fun StatCard(label: String, value: String, unit: String, icon: @Composable () -> Unit, modifier: Modifier = Modifier, accent: Color = EcoGreen) {
    Card(modifier, RoundedCornerShape(20.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(36.dp).clip(CircleShape).background(accent.copy(alpha = 0.15f)), Alignment.Center) { icon() }
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                Text(unit, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant), modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

// ── Shimmer ───────────────────────────────────────────────────────────────────
@Composable
fun ShimmerBox(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(12.dp)) {
    val t = rememberInfiniteTransition(label = "shimmer")
    val x by t.animateFloat(initialValue = -1f, targetValue = 2f, animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)), label = "x")
    val surface = MaterialTheme.colorScheme.surface
    val surfaceVar = MaterialTheme.colorScheme.surfaceVariant
    Box(modifier.clip(shape).background(Brush.horizontalGradient(listOf(surface, surfaceVar, surface), x * 300f, x * 300f + 300f)))
}

// ── Eco Badge ─────────────────────────────────────────────────────────────────
@Composable
fun EcoBadge(text: String, color: Color = EcoGreen) {
    Box(Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.15f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
        Text(text, style = MaterialTheme.typography.labelSmall.copy(color = color, fontWeight = FontWeight.SemiBold))
    }
}

// ── Section Header ────────────────────────────────────────────────────────────
@Composable
fun SectionHeader(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}

// ── Vasundhara Text Field ─────────────────────────────────────────────────────
@Composable
fun VasundharaTextField(
    value: String, onValueChange: (String) -> Unit, label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, null, tint = EcoGreen) },
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EcoGreen, unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = EcoGreen, cursorColor = EcoGreen,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}
