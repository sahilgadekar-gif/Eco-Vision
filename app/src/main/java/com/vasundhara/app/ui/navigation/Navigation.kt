package com.vasundhara.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.vasundhara.app.ui.screens.ai.AiChatScreen
import com.vasundhara.app.ui.screens.auth.AuthScreen
import com.vasundhara.app.ui.screens.carbon.CarbonTrackerScreen
import com.vasundhara.app.ui.screens.dashboard.DashboardScreen
import com.vasundhara.app.ui.screens.ecomap.EcoMapScreen
import com.vasundhara.app.ui.screens.gamification.GamificationScreen
import com.vasundhara.app.ui.screens.onboarding.OnboardingScreen
import com.vasundhara.app.ui.screens.profile.ProfileScreen
import com.vasundhara.app.ui.screens.rhi.RhiScreen
import com.vasundhara.app.ui.screens.river.RiverMonitoringScreen
import com.vasundhara.app.ui.screens.settings.SettingsScreen
import com.vasundhara.app.ui.screens.trees.TreesPlantedScreen
import com.vasundhara.app.ui.screens.territory.TerritoryScreen
import com.vasundhara.app.ui.screens.ngo.NGOScreen
import com.vasundhara.app.ui.screens.waste.WasteDetectionScreen
import com.vasundhara.app.ui.theme.*

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Auth       : Screen("auth")
    object Dashboard  : Screen("dashboard")
    object Waste      : Screen("waste")
    object Carbon     : Screen("carbon")
    object Trees      : Screen("trees")
    object Territory  : Screen("territory")
    object River      : Screen("river")
    object NGO        : Screen("ngo")
    object Rhi        : Screen("rhi")
    object AiChat     : Screen("ai_chat")
    object Gamification: Screen("gamification")
    object EcoMap     : Screen("eco_map")
    object Profile    : Screen("profile")
    object Settings   : Screen("settings")
}

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard,   "Home",    Icons.Filled.Home),
    BottomNavItem(Screen.Waste,       "Scan",    Icons.Filled.CameraAlt),
    BottomNavItem(Screen.Carbon,      "Track",   Icons.Filled.BarChart),
    BottomNavItem(Screen.Trees,       "Trees",   Icons.Filled.Park),
    BottomNavItem(Screen.Territory,   "Territory", Icons.Filled.Map),
    BottomNavItem(Screen.NGO,         "NGOs",    Icons.Filled.Groups),
    BottomNavItem(Screen.Profile,     "Profile", Icons.Filled.Person),
)

@Composable
fun VasundharaNavHost(startDestination: String = Screen.Auth.route) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route
    val showBar = currentRoute !in listOf(Screen.Auth.route, Screen.Onboarding.route)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    bottomNavItems.forEach { item ->
                        val selected = entry?.destination?.hierarchy?.any { it.route == item.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(item.screen.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true; restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, item.label) },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EcoGreen, selectedTextColor = EcoGreen,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = EcoGreen.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { pad ->
        NavHost(
            navController = nav, startDestination = startDestination,
            modifier = Modifier.padding(pad),
            enterTransition = { fadeIn(tween(280)) + slideInHorizontally(tween(280)) { it / 10 } },
            exitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 10 } },
            popEnterTransition = { fadeIn(tween(280)) + slideInHorizontally(tween(280)) { -it / 10 } },
            popExitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { it / 10 } }
        ) {
            composable(Screen.Onboarding.route) { OnboardingScreen(onDone = { nav.navigate(Screen.Auth.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } }) }
            composable(Screen.Auth.route) { AuthScreen(onAuthenticated = { nav.navigate(Screen.Dashboard.route) { popUpTo(Screen.Auth.route) { inclusive = true } } }, onNewUser = { nav.navigate(Screen.Onboarding.route) { popUpTo(Screen.Auth.route) { inclusive = true } } }) }
            composable(Screen.Dashboard.route) { DashboardScreen(onNavigate = { nav.navigate(it) }) }
            composable(Screen.Waste.route) { WasteDetectionScreen() }
            composable(Screen.Carbon.route) { CarbonTrackerScreen() }
            composable(Screen.Trees.route) { TreesPlantedScreen() }
            composable(Screen.Territory.route) { TerritoryScreen() }
            composable(Screen.River.route) { RiverMonitoringScreen() }
            composable(Screen.NGO.route) { NGOScreen() }
            composable(Screen.Rhi.route) { RhiScreen() }
            composable(Screen.AiChat.route) { AiChatScreen() }
            composable(Screen.Gamification.route) { GamificationScreen() }
            composable(Screen.EcoMap.route) { EcoMapScreen() }
            composable(Screen.Profile.route) { ProfileScreen(onSettings = { nav.navigate(Screen.Settings.route) }) }
            composable(Screen.Settings.route) { SettingsScreen(onBack = { nav.popBackStack() }) }
        }
    }
}
