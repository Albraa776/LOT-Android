package com.albraa.lot.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albraa.lot.LOTApplication
import com.albraa.lot.core.model.AppSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LOTApplication
    private val repo = app.settingsRepository
    private val modelManager = app.modelManager

    val settings: StateFlow<AppSettings> = repo.settingsFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppSettings()
    )

    val modelDescriptor = modelManager.descriptor
    val storageUsedFormatted: String = modelManager.getStorageUsedFormatted()

    fun setAppLanguage(lang: String) {
        viewModelScope.launch {
            repo.updateAppLanguage(lang)
        }
    }

    fun setVoiceGender(gender: String) {
        viewModelScope.launch {
            repo.updateVoiceGender(gender)
        }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            repo.updateSpeechRate(rate)
        }
    }

    fun setThemeMode(theme: String) {
        viewModelScope.launch {
            repo.updateThemeMode(theme)
        }
    }

    fun setPerformanceProfile(profile: String) {
        viewModelScope.launch {
            repo.updatePerformanceProfile(profile)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                val cacheDir = app.cacheDir
                cacheDir.deleteRecursively()
                cacheDir.mkdirs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
