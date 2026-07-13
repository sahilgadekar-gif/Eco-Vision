package com.vasundhara.app.ui.screens.ai

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vasundhara.app.data.model.ChatMessage
import com.vasundhara.app.ui.components.VasundharaBackground
import com.vasundhara.app.ui.theme.*

private val quickPrompts = listOf("Reduce electricity?", "Best recycling tips", "Low carbon diet", "River health tips", "Green commute")

@Composable
fun AiChatScreen(vm: AiViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    LaunchedEffect(state.messages.size) { if (state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size - 1) }

    VasundharaBackground {
        Column(Modifier.fillMaxSize()) {
            // Header
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(44.dp).clip(CircleShape).background(Brush.radialGradient(listOf(EcoGreen, EcoGreenDark))), Alignment.Center) { Icon(Icons.Filled.AutoAwesome, null, tint = PureBlack, modifier = Modifier.size(22.dp)) }
                Column(Modifier.weight(1f)) {
                    Text("Vasundhara AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(EcoGreen))
                        Text("Online · Multilingual", style = MaterialTheme.typography.labelSmall.copy(color = EcoGreen))
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            // Quick prompts
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickPrompts) { p ->
                    SuggestionChip(onClick = { vm.sendQuick(p) }, label = { Text(p, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = MaterialTheme.colorScheme.outline))
                }
            }

            // Messages
            LazyColumn(state = listState, modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.messages, key = { it.id }) { msg ->
                    AnimatedVisibility(true, enter = if (msg.isUser) slideInHorizontally { it } + fadeIn() else slideInHorizontally { -it } + fadeIn()) {
                        MessageBubble(msg)
                    }
                }
                if (state.isTyping) item { TypingBubble() }
            }

            // Input
            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.input, onValueChange = vm::updateInput,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about sustainability...", style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoGreen, unfocusedBorderColor = MaterialTheme.colorScheme.outline, cursorColor = EcoGreen, focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { vm.send() }),
                    maxLines = 3
                )
                Box(Modifier.size(48.dp).clip(CircleShape).background(if (state.input.isNotBlank() && !state.isTyping) EcoGreen else MaterialTheme.colorScheme.surfaceVariant).clickable(enabled = state.input.isNotBlank() && !state.isTyping) { vm.send() }, Alignment.Center) {
                    if (state.isTyping) CircularProgressIndicator(Modifier.size(20.dp), color = EcoGreen, strokeWidth = 2.dp)
                    else Icon(Icons.Filled.Send, null, tint = if (state.input.isNotBlank()) PureBlack else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start) {
        if (!msg.isUser) { Box(Modifier.size(32.dp).clip(CircleShape).background(Brush.radialGradient(listOf(EcoGreen, EcoGreenDark))), Alignment.Center) { Icon(Icons.Filled.AutoAwesome, null, tint = PureBlack, modifier = Modifier.size(16.dp)) }; Spacer(Modifier.width(8.dp)) }
        Box(Modifier.widthIn(max = 280.dp).clip(RoundedCornerShape(topStart = if (msg.isUser) 20.dp else 4.dp, topEnd = if (msg.isUser) 4.dp else 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)).background(if (msg.isUser) Brush.linearGradient(listOf(NavyAccent, NavyLight)) else Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface))).padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(msg.content, style = MaterialTheme.typography.bodyMedium)
        }
        if (msg.isUser) { Spacer(Modifier.width(8.dp)); Box(Modifier.size(32.dp).clip(CircleShape).background(NavyAccent), Alignment.Center) { Icon(Icons.Filled.Person, null, tint = PureWhite, modifier = Modifier.size(16.dp)) } }
    }
}

@Composable
private fun TypingBubble() {
    val t = rememberInfiniteTransition(label = "typing")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(32.dp).clip(CircleShape).background(Brush.radialGradient(listOf(EcoGreen, EcoGreenDark))), Alignment.Center) { Icon(Icons.Filled.AutoAwesome, null, tint = PureBlack, modifier = Modifier.size(16.dp)) }
        Box(Modifier.clip(RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(3) { i ->
                    val scale by t.animateFloat(0.6f, 1f, infiniteRepeatable(tween(600, delayMillis = i * 150, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "d$i")
                    Box(Modifier.size((6 * scale).dp).clip(CircleShape).background(EcoGreen.copy(alpha = 0.7f)))
                }
            }
        }
    }
}
