package com.vasundhara.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasundhara.app.data.local.UserPreferences
import com.vasundhara.app.data.model.AppLanguage
import com.vasundhara.app.data.model.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    val theme = prefs.theme.stateIn(viewModelScope, SharingStarted.Eagerly, AppTheme.SYSTEM)
    val language = prefs.language.stateIn(viewModelScope, SharingStarted.Eagerly, AppLanguage.ENGLISH)

    fun setTheme(t: AppTheme) = viewModelScope.launch { prefs.saveTheme(t) }
    fun setLanguage(l: AppLanguage) = viewModelScope.launch { prefs.saveLanguage(l) }
}
