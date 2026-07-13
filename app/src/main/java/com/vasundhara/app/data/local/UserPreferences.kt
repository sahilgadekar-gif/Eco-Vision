package com.vasundhara.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.vasundhara.app.data.model.AppLanguage
import com.vasundhara.app.data.model.AppTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("vasundhara_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val TOKEN_KEY       = stringPreferencesKey("jwt_token")
        val USER_ID_KEY     = stringPreferencesKey("user_id")
        val THEME_KEY       = stringPreferencesKey("app_theme")
        val LANGUAGE_KEY    = stringPreferencesKey("app_language")
        val ONBOARDED_KEY   = booleanPreferencesKey("onboarded")
        val GOOGLE_ID_KEY   = stringPreferencesKey("google_id")
    }

    val token: Flow<String?>       = pref { it[TOKEN_KEY] }
    val userId: Flow<String?>      = pref { it[USER_ID_KEY] }
    val theme: Flow<AppTheme>      = pref { AppTheme.valueOf(it[THEME_KEY] ?: AppTheme.SYSTEM.name) }
    val language: Flow<AppLanguage>= pref { AppLanguage.values().find { l -> l.code == it[LANGUAGE_KEY] } ?: AppLanguage.ENGLISH }
    val isOnboarded: Flow<Boolean> = pref { it[ONBOARDED_KEY] ?: false }

    suspend fun saveToken(token: String)         = edit { it[TOKEN_KEY] = token }
    suspend fun saveUserId(id: String)           = edit { it[USER_ID_KEY] = id }
    suspend fun saveTheme(theme: AppTheme)       = edit { it[THEME_KEY] = theme.name }
    suspend fun saveLanguage(lang: AppLanguage)  = edit { it[LANGUAGE_KEY] = lang.code }
    suspend fun setOnboarded()                   = edit { it[ONBOARDED_KEY] = true }
    suspend fun clear()                          = context.dataStore.edit { it.clear() }

    private fun <T> pref(mapper: (Preferences) -> T): Flow<T> =
        context.dataStore.data.catch { emit(emptyPreferences()) }.map(mapper)

    private suspend fun edit(block: (MutablePreferences) -> Unit) =
        context.dataStore.edit(block)
}
