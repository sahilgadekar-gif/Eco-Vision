package com.vasundhara.app.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vasundhara.app.data.model.AppLanguage
import com.vasundhara.app.data.model.AppTheme
import com.vasundhara.app.ui.components.VasundharaBackground
import com.vasundhara.app.ui.theme.*

@Composable
fun SettingsScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val theme by vm.theme.collectAsStateWithLifecycle()
    val language by vm.language.collectAsStateWithLifecycle()

    VasundharaBackground {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) }
                Text("Settings", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(Modifier.height(24.dp))

            // Theme
            SettingsSection(title = "Appearance") {
                Text("Theme", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppTheme.values().forEach { t ->
                        val selected = theme == t
                        FilterChip(
                            selected = selected,
                            onClick = { vm.setTheme(t) },
                            label = { Text(t.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EcoGreen.copy(alpha = 0.2f),
                                selectedLabelColor = EcoGreen
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Language
            SettingsSection(title = "Language") {
                AppLanguage.values().forEach { lang ->
                    val selected = language == lang
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(if (selected) EcoGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
                            .clickable { vm.setLanguage(lang) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(lang.displayName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal))
                        if (selected) Icon(Icons.Filled.Check, null, tint = EcoGreen, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge.copy(color = EcoGreen), modifier = Modifier.padding(bottom = 12.dp))
            content()
        }
    }
}
