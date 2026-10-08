package com.albraa.lot.core.model

data class AppSettings(
    val appLanguage: String = "system", // "en", "ar", or "system"
    val defaultSourceLang: String = "en",
    val defaultTargetLang: String = "ar",
    val defaultVoiceGender: String = "Male", // Master prompt specifies default is Male
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val themeMode: String = "dark", // Instrument dark by default
    val performanceProfile: String = "balanced", // "fast", "balanced", "quality"
    val isOfflineModeEnforced: Boolean = true
)
