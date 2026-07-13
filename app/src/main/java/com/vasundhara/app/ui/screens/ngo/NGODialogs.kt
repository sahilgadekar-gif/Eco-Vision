package com.vasundhara.app.ui.screens.ngo

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
import coil.compose.AsyncImage
import com.vasundhara.app.data.model.*
import com.vasundhara.app.ui.theme.*

@Composable
fun NGODetailDialog(
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
                    // Cover Image
                    if (ngo.coverImageUrl != null) {
                        AsyncImage(
                            model = ngo.coverImageUrl,
                            contentDescription = ngo.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    
                    // Basic Info
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Location",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    ngo.location,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                )
                            }
                            
                            Column {
                                Text(
                                    "Rating",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "${ngo.rating}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFFFF9800),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9800),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Description
                    Column {
                        Text(
                            "About",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            ngo.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (isDark) Color.LightGray else Color.DarkGray
                            )
                        )
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Focus Areas
                    Column {
                        Text(
                            "Focus Areas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ngo.focusAreas.forEach { area ->
                                Box(
                                    Modifier.clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
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
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Statistics
                    Column {
                        Text(
                            "Impact Statistics",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "${ngo.totalProjects}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = if (isDark) Color.White else Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    "Total Projects",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color.LightGray else Color.DarkGray
                                    )
                                )
                            }
                            
                            Column {
                                Text(
                                    "${ngo.activeProjects}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    "Active Projects",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color.LightGray else Color.DarkGray
                                    )
                                )
                            }
                            
                            Column {
                                Text(
                                    "${ngo.volunteersCount}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = Color(0xFF2196F3),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    "Volunteers",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color.LightGray else Color.DarkGray
                                    )
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Contact Info
                    Column {
                        Text(
                            "Contact Information",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF4CAF50))
                                Text(
                                    ngo.contactEmail,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                )
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF4CAF50))
                                Text(
                                    ngo.contactPhone,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                )
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF4CAF50))
                                Text(
                                    ngo.website,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Blue
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
fun CompetitionDetailDialog(
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
                    // Competition Info
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            competition.description,
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
                                    "Start Date",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    competition.startDate,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                )
                            }
                            
                            Column {
                                Text(
                                    "End Date",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    competition.endDate,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                )
                            }
                            
                            Column {
                                Text(
                                    "Type",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    competition.type,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isDark) Color.White else Color.Black
                                    )
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Rules
                    Column {
                        Text(
                            "📋 Competition Rules",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        competition.rules.forEachIndexed { index, rule ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    "${index + 1}.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    rule,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color.LightGray else Color.DarkGray
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Prizes
                    Column {
                        Text(
                            "🏆 Prizes & Rewards",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        competition.prizes.forEach { prize ->
                            Card(
                                Modifier.fillMaxWidth(),
                                RoundedCornerShape(8.dp),
                                CardDefaults.cardColors(
                                    containerColor = when (prize.rank) {
                                        1 -> Color(0xFFFFD700).copy(alpha = 0.1f)
                                        2 -> Color(0xFFC0C0C0).copy(alpha = 0.1f)
                                        3 -> Color(0xFFCD7F32).copy(alpha = 0.1f)
                                        else -> Color.Gray.copy(alpha = 0.1f)
                                    }
                                )
                            ) {
                                Row(
                                    Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            prize.prizeName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = when (prize.rank) {
                                                    1 -> Color(0xFFFFD700)
                                                    2 -> Color(0xFFC0C0C0)
                                                    3 -> Color(0xFFCD7F32)
                                                    else -> Color.Gray
                                                }
                                            )
                                        )
                                        Text(
                                            prize.description,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isDark) Color.LightGray else Color.DarkGray
                                            )
                                        )
                                    }
                                    
                                    Text(
                                        prize.prizeValue,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Current Leaderboard
                    Column {
                        Text(
                            "📊 Current Leaderboard",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        competition.currentLeaderboard.take(10).forEach { entry ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        "#${entry.rank}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = when (entry.rank) {
                                                1 -> Color(0xFFFFD700)
                                                2 -> Color(0xFFC0C0C0)
                                                3 -> Color(0xFFCD7F32)
                                                else -> if (isDark) Color.White else Color.Black
                                            }
                                        )
                                    )
                                    Column {
                                        Text(
                                            entry.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = if (isDark) Color.White else Color.Black,
                                                fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                        Text(
                                            entry.territory,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isDark) Color.LightGray else Color.DarkGray
                                            )
                                        )
                                    }
                                }
                                
                                Text(
                                    "${"%.1f".format(entry.ecoScore)} pts",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(Modifier.height(8.dp))
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
fun SupportGuidelineDialog(
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
                    // Description
                    Text(
                        guideline.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isDark) Color.LightGray else Color.DarkGray
                        )
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Quick Info
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "⏱️ Time Required",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                guideline.estimatedTime,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isDark) Color.White else Color.Black
                                )
                            )
                        }
                        
                        Column {
                            Text(
                                "📊 Difficulty",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Steps
                    Column {
                        Text(
                            "📋 Steps to Follow",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        guideline.steps.forEachIndexed { index, step ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    "${index + 1}.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    step,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color.LightGray else Color.DarkGray
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Requirements
                    Column {
                        Text(
                            "✅ Requirements",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        guideline.requirements.forEach { requirement ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    requirement,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color.LightGray else Color.DarkGray
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Benefits
                    Column {
                        Text(
                            "🎁 Benefits",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        guideline.benefits.forEach { benefit ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    benefit,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isDark) Color.LightGray else Color.DarkGray
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Contact Info
                    Column {
                        Text(
                            "📞 Contact Information",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF4CAF50))
                            Text(
                                guideline.contactInfo,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Blue
                                )
                            )
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
