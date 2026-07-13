package com.vasundhara.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vasundhara.app.data.model.AppTheme
import com.vasundhara.app.ui.navigation.VasundharaNavHost
import com.vasundhara.app.ui.screens.settings.SettingsViewModel
import com.vasundhara.app.ui.theme.VasundharaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm: SettingsViewModel = hiltViewModel()
            val theme by settingsVm.theme.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (theme) {
                AppTheme.DARK   -> true
                AppTheme.LIGHT  -> false
                AppTheme.SYSTEM -> systemDark
            }
            VasundharaTheme(darkTheme = isDark) {
                VasundharaNavHost()
            }
        }
    }
}
