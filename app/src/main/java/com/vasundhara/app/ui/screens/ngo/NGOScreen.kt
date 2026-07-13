package com.vasundhara.app.ui.screens.ngo

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.compose.AsyncImage
import com.vasundhara.app.ui.components.VasundharaBackground
import com.vasundhara.app.ui.theme.*

@Composable
fun NGOScreen(vm: NGOViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    
    VasundharaBackground {
        Column(Modifier.fillMaxSize()) {
            // Header
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "NGO Partners",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                    )
                    Text(
                        "${state.ngos.size} active NGOs in Maharashtra",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                
                Button(
                    onClick = { vm.refreshNGOs() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Refresh")
                }
            }
            
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("NGOs") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("League") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Support") }
                )
            }
            
            // Content
            when (selectedTab) {
                0 -> NGOsListContent(state.ngos, vm::selectNGO, state.selectedNGO)
                1 -> LeagueContent(state.competitions, vm::selectCompetition, state.selectedCompetition)
                2 -> SupportContent(vm::selectSupport, state.selectedGuideline)
            }
        }
    }
}

@Composable
private fun NGOsListContent(
    ngos: List<NGO>,
    onNGOSelect: (NGO) -> Unit,
    selectedNGO: NGO?
) {
    val isDark = isSystemInDarkTheme()
    
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ngos) { ngo ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .clickable { onNGOSelect(ngo) },
                RoundedCornerShape(16.dp),
                CardDefaults.cardColors(
                    containerColor = if (isDark) Color.Black else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // NGO Logo
                        if (ngo.logoUrl != null) {
                            AsyncImage(
                                model = ngo.logoUrl,
                                contentDescription = ngo.name,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Box(
                                Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    ngo.name.first().toString(),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        
                        Column(Modifier.weight(1f)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    ngo.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                )
                                
                                if (ngo.verified) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            "Verified",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF4CAF50),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(4.dp))
                            
                            Text(
                                ngo.location,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            )
                            
                            Spacer(Modifier.height(8.dp))
                            
                            // Focus Areas
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ngo.focusAreas.take(3).forEach { area ->
                                    Box(
                                        Modifier.clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            area,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF4CAF50),
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            
                            // Stats
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "${ngo.totalProjects}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = if (isDark) Color.White else Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        "Projects",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isDark) Color.LightGray else Color.DarkGray
                                        )
                                    )
                                }
                                
                                Column {
                                    Text(
                                        "${ngo.volunteersCount}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = if (isDark) Color.White else Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        "Volunteers",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isDark) Color.LightGray else Color.DarkGray
                                        )
                                    )
                                }
                                
                                Column {
                                    Text(
                                        "${ngo.rating}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = Color(0xFFFF9800),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        "Rating",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isDark) Color.LightGray else Color.DarkGray
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // NGO Detail Dialog
    selectedNGO?.let { ngo ->
        NGODetailDialog(
            ngo = ngo,
            onDismiss = { onNGOSelect(NGO("", "", 0, "", "", "", 0.0, 0.0, "", "", "", "", "", emptyList(), 0, 0, 0, 0f, false)) }
        )
    }
}

@Composable
private fun LeagueContent(
    competitions: List<NGOCompetition>,
    onCompetitionSelect: (NGOCompetition) -> Unit,
    selectedCompetition: NGOCompetition?
) {
    val isDark = isSystemInDarkTheme()
    
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(competitions) { competition ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .clickable { onCompetitionSelect(competition) },
                RoundedCornerShape(16.dp),
                CardDefaults.cardColors(
                    containerColor = if (isDark) Color.Black else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                competition.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            )
                            Text(
                                competition.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            )
                        }
                        
                        Box(
                            Modifier.clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (competition.status) {
                                        "Active" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                        "Upcoming" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                        else -> Color.Gray.copy(alpha = 0.2f)
                                    }
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                competition.status,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = when (competition.status) {
                                        "Active" -> Color(0xFF4CAF50)
                                        "Upcoming" -> Color(0xFFFF9800)
                                        else -> Color.Gray
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    // Competition Info
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Start Date",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            )
                            Text(
                                competition.startDate,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color.White else Color.Black
                                )
                            )
                        }
                        
                        Column {
                            Text(
                                "End Date",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            )
                            Text(
                                competition.endDate,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color.White else Color.Black
                                )
                            )
                        }
                        
                        Column {
                            Text(
                                "Type",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            )
                            Text(
                                competition.type,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isDark) Color.White else Color.Black
                                )
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    // Top 3 Participants
                    Text(
                        "🏆 Top Performers",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    competition.currentLeaderboard.take(3).forEachIndexed { index, participant ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "${index + 1}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = when (index) {
                                            0 -> Color(0xFFFFD700) // Gold
                                            1 -> Color(0xFFC0C0C0) // Silver
                                            2 -> Color(0xFFCD7F32) // Bronze
                                            else -> Color.Gray
                                        }
                                    )
                                )
                                Column {
                                    Text(
                                        participant.name,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDark) Color.White else Color.Black
                                        )
                                    )
                                    Text(
                                        participant.territory,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isDark) Color.LightGray else Color.DarkGray
                                        )
                                    )
                                }
                            }
                            
                            Text(
                                "${"%.1f".format(participant.ecoScore)} pts",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        
                        if (index < 2) Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
    
    // Competition Detail Dialog
    selectedCompetition?.let { competition ->
        CompetitionDetailDialog(
            competition = competition,
            onDismiss = { onCompetitionSelect(NGOCompetition("", "", "", "", "", "", null, emptyList(), emptyList(), emptyList(), "", emptyList())) }
        )
    }
}

@Composable
private fun SupportContent(
    onSupportSelect: (NGOSupportGuideline) -> Unit,
    selectedGuideline: NGOSupportGuideline?
) {
    val isDark = isSystemInDarkTheme()
    
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                Modifier.fillMaxWidth(),
                RoundedCornerShape(16.dp),
                CardDefaults.cardColors(
                    containerColor = if (isDark) Color.Black else Color.White
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Support NGOs",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                    )
                    
                    Spacer(Modifier.height(12.dp))
                    
                    Text(
                        "Join hands with local NGOs to make a real difference in your community. Your support helps us clean rivers, plant trees, and create a sustainable future.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isDark) Color.LightGray else Color.DarkGray
                        )
                    )
                }
            }
        }
        
        // Support Guidelines
        items(getSupportGuidelines()) { guideline ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .clickable { onSupportSelect(guideline) },
                RoundedCornerShape(16.dp),
                CardDefaults.cardColors(
                    containerColor = if (isDark) Color.Black else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            guideline.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        
                        Box(
                            Modifier.clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (guideline.difficulty) {
                                        "Easy" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                        "Medium" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                        else -> Color.Red.copy(alpha = 0.2f)
                                    }
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                guideline.difficulty,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = when (guideline.difficulty) {
                                        "Easy" -> Color(0xFF4CAF50)
                                        "Medium" -> Color(0xFFFF9800)
                                        else -> Color.Red
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Text(
                        guideline.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isDark) Color.LightGray else Color.DarkGray
                        )
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "⏱️ ${guideline.estimatedTime}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            )
                        )
                        
                        Text(
                            "📋 ${guideline.steps.size} steps",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            )
                        )
                    }
                }
            }
        }
    }
    
    // Support Guideline Detail Dialog
    selectedGuideline?.let { guideline ->
        SupportGuidelineDialog(
            guideline = guideline,
            onDismiss = { onSupportSelect(NGOSupportGuideline("", "", "", emptyList(), emptyList(), emptyList(), "", "", "")) }
        )
    }
}

private fun getSupportGuidelines(): List<NGOSupportGuideline> {
    return listOf(
        NGOSupportGuideline(
            id = "support_001",
            title = "River Cleanup Volunteer",
            description = "Join weekend river cleanup drives to remove plastic waste and restore river ecosystems.",
            steps = listOf(
                "Register as volunteer through our app",
                "Attend orientation session (2 hours)",
                "Participate in weekend cleanup drives",
                "Report waste collected and impact metrics",
                "Get certificate and recognition"
            ),
            requirements = listOf(
                "Age 18+ or with parental consent",
                "Basic fitness for outdoor activities",
                "Commitment of minimum 4 weekends",
                "Own safety equipment (gloves, mask)"
            ),
            benefits = listOf(
                "Certificate of participation",
                "Community service recognition",
                "Networking with environmental experts",
                "Skill development in waste management"
            ),
            contactInfo = "contact@godavaririvers.org",
            estimatedTime = "4 weekends (8 hours each)",
            difficulty = "Easy"
        ),
        NGOSupportGuideline(
            id = "support_002",
            title = "Tree Plantation Campaign",
            description = "Help us plant native tree species along riverbanks to prevent erosion and improve biodiversity.",
            steps = listOf(
                "Complete online training module",
                "Join plantation team in your area",
                "Learn proper planting techniques",
                "Monitor tree growth for 6 months",
                "Report survival rate and health metrics"
            ),
            requirements = listOf(
                "Basic knowledge of local flora",
                "Physical fitness for field work",
                "Smartphone for monitoring and reporting",
                "Transportation to plantation sites"
            ),
            benefits = listOf(
                "Environmental stewardship certificate",
                "Free saplings for personal planting",
                "Expert guidance from botanists",
                "Contribution to carbon offset goals"
            ),
            contactInfo = "trees@nashikgreen.org",
            estimatedTime = "6 months (2 hours per week)",
            difficulty = "Medium"
        ),
        NGOSupportGuideline(
            id = "support_003",
            title = "Water Quality Monitoring",
            description = "Become a citizen scientist and help monitor water quality parameters in local rivers and streams.",
            steps = listOf(
                "Complete water testing certification",
                "Get assigned monitoring locations",
                "Collect weekly water samples",
                "Test parameters (pH, DO, turbidity)",
                "Submit data through mobile app"
            ),
            requirements = listOf(
                "Science background preferred",
                "Training in water testing methods",
                "Access to testing equipment",
                "Regular availability for sampling"
            ),
            benefits = listOf(
                "Professional certification",
                "Research publication opportunities",
                "Contribution to environmental policy",
                "Expert network in water management"
            ),
            contactInfo = "water@nashikenviro.org",
            estimatedTime = "Ongoing (3 hours per week)",
            difficulty = "Hard"
        )
    )
}

// Add the dialog components here
@Composable
private fun NGODetailDialog(
    ngo: NGO,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
        title = { 
            Text(
                ngo.name,
                color = if (isDark) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Basic Info
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            ngo.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            )
                        )
                        
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Registration",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    ngo.registrationNumber,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                )
                            }
                            
                            Column {
                                Text(
                                    "Established",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    "${ngo.establishedYear}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color.White else Color.Black,
                    contentColor = if (isDark) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        },
        containerColor = if (isDark) Color.Black else Color.White
    )
}

@Composable
private fun CompetitionDetailDialog(
    competition: NGOCompetition,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
        title = { 
            Text(
                competition.name,
                color = if (isDark) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        competition.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isDark) Color.LightGray else Color.DarkGray
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color.White else Color.Black,
                    contentColor = if (isDark) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        },
        containerColor = if (isDark) Color.Black else Color.White
    )
}

@Composable
private fun SupportGuidelineDialog(
    guideline: NGOSupportGuideline,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.9f),
        title = { 
            Text(
                guideline.title,
                color = if (isDark) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        guideline.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isDark) Color.LightGray else Color.DarkGray
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color.White else Color.Black,
                    contentColor = if (isDark) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        },
        containerColor = if (isDark) Color.Black else Color.White
    )
}
