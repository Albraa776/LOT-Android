package com.albraa.lot.core.model

enum class TranslationMode {
    TEXT,
    IMAGE,
    SPEECH
}

data class TranslationJob(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sourceLanguage: Language,
    val targetLanguage: Language,
    val sourceText: String,
    val mode: TranslationMode = TranslationMode.TEXT,
    val timestamp: Long = System.currentTimeMillis()
)
