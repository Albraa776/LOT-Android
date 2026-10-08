package com.albraa.lot.core.model

sealed class TranslationResult {
    data class Success(
        val job: TranslationJob,
        val translatedText: String,
        val inferenceTimeMs: Long = 0,
        val tokensPerSecond: Float = 0f
    ) : TranslationResult()

    data class Progress(
        val partialText: String,
        val progressRatio: Float
    ) : TranslationResult()

    data class Error(
        val job: TranslationJob,
        val message: String,
        val cause: Throwable? = null
    ) : TranslationResult()
}
