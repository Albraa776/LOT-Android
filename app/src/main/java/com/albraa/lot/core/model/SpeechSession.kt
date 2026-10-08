package com.albraa.lot.core.model

import java.io.File

data class SpeechSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val audioFile: File? = null,
    val durationSeconds: Int = 0,
    val waveformAmplitudes: List<Float> = emptyList(),
    val sourceTranscript: String = "",
    val translatedText: String = "",
    val isRecording: Boolean = false,
    val isProcessing: Boolean = false
)
