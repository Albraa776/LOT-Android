package com.albraa.lot.core.model

enum class TextDirection {
    LTR,
    RTL
}

data class Language(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val direction: TextDirection,
    val script: String,
    val translationSupported: Boolean = true,
    val ocrSupported: Boolean = true,
    val asrSupported: Boolean = true,
    val ttsSupported: Boolean = true,
    val defaultVoiceGender: String = "Male",
    val availableVoiceGenders: List<String> = listOf("Male", "Female")
) {
    val isRtl: Boolean
        get() = direction == TextDirection.RTL

    val displayName: String
        get() = if (englishName == nativeName) englishName else "$nativeName ($englishName)"
}
