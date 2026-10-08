package com.albraa.lot.core.tts

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class OfflineTtsEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    _currentUtteranceId.value = utteranceId
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentUtteranceId.value = null
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentUtteranceId.value = null
                }
            })
        }
    }

    fun speak(
        utteranceId: String,
        text: String,
        languageCode: String,
        voiceGender: String = "Male",
        speed: Float = 1.0f,
        pitch: Float = 1.0f
    ) {
        if (!isInitialized || tts == null || text.isBlank()) return

        val locale = Locale.forLanguageTag(languageCode)
        tts?.language = locale
        tts?.setSpeechRate(speed)
        tts?.setPitch(pitch)

        // Select voice gender (Male default as required)
        selectVoiceForGender(locale, voiceGender)

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    private fun selectVoiceForGender(locale: Locale, targetGender: String) {
        try {
            val voices = tts?.voices ?: return
            val matchingVoices = voices.filter { it.locale.language == locale.language }
            
            // Look for gender in voice name attributes
            val preferredVoice = matchingVoices.firstOrNull { voice ->
                val nameLower = voice.name.lowercase()
                if (targetGender.equals("Male", ignoreCase = true)) {
                    nameLower.contains("male") && !nameLower.contains("female")
                } else {
                    nameLower.contains("female")
                }
            } ?: matchingVoices.firstOrNull()

            if (preferredVoice != null) {
                tts?.voice = preferredVoice
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _currentUtteranceId.value = null
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
