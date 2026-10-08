package com.albraa.lot.core.tts

data class VoiceProfile(
    val name: String,
    val languageCode: String,
    val gender: String, // "Male" or "Female"
    val isDefault: Boolean = false,
    val isInstalled: Boolean = true
)
