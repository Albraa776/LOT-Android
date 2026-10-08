package com.albraa.lot.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.albraa.lot.core.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lot_settings")

class SettingsRepository(private val context: Context) {

    private val KEY_APP_LANG = stringPreferencesKey("app_language")
    private val KEY_DEFAULT_SOURCE_LANG = stringPreferencesKey("default_source_lang")
    private val KEY_DEFAULT_TARGET_LANG = stringPreferencesKey("default_target_lang")
    private val KEY_VOICE_GENDER = stringPreferencesKey("voice_gender")
    private val KEY_SPEECH_RATE = floatPreferencesKey("speech_rate")
    private val KEY_PITCH = floatPreferencesKey("pitch")
    private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    private val KEY_PERF_PROFILE = stringPreferencesKey("perf_profile")

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            appLanguage = prefs[KEY_APP_LANG] ?: "system",
            defaultSourceLang = prefs[KEY_DEFAULT_SOURCE_LANG] ?: "en",
            defaultTargetLang = prefs[KEY_DEFAULT_TARGET_LANG] ?: "ar",
            defaultVoiceGender = prefs[KEY_VOICE_GENDER] ?: "Male",
            speechRate = prefs[KEY_SPEECH_RATE] ?: 1.0f,
            pitch = prefs[KEY_PITCH] ?: 1.0f,
            themeMode = prefs[KEY_THEME_MODE] ?: "dark",
            performanceProfile = prefs[KEY_PERF_PROFILE] ?: "balanced"
        )
    }

    suspend fun updateAppLanguage(lang: String) {
        context.dataStore.edit { it[KEY_APP_LANG] = lang }
    }

    suspend fun updateDefaultSourceLang(code: String) {
        context.dataStore.edit { it[KEY_DEFAULT_SOURCE_LANG] = code }
    }

    suspend fun updateDefaultTargetLang(code: String) {
        context.dataStore.edit { it[KEY_DEFAULT_TARGET_LANG] = code }
    }

    suspend fun updateVoiceGender(gender: String) {
        context.dataStore.edit { it[KEY_VOICE_GENDER] = gender }
    }

    suspend fun updateSpeechRate(rate: Float) {
        context.dataStore.edit { it[KEY_SPEECH_RATE] = rate }
    }

    suspend fun updatePitch(pitch: Float) {
        context.dataStore.edit { it[KEY_PITCH] = pitch }
    }

    suspend fun updateThemeMode(theme: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = theme }
    }

    suspend fun updatePerformanceProfile(profile: String) {
        context.dataStore.edit { it[KEY_PERF_PROFILE] = profile }
    }
}
