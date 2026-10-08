package com.albraa.lot.ui.speech

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albraa.lot.LOTApplication
import com.albraa.lot.core.asr.AudioRecorder
import com.albraa.lot.core.asr.OfflineSpeechRecognizer
import com.albraa.lot.core.database.HistoryEntity
import com.albraa.lot.core.model.Language
import com.albraa.lot.core.model.SpeechSession
import com.albraa.lot.core.model.TranslationJob
import com.albraa.lot.core.model.TranslationMode
import com.albraa.lot.core.model.TranslationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class SpeechTranslationViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LOTApplication
    private val registry = app.languageRegistry
    private val translationEngine = app.translationEngine
    private val recorder = AudioRecorder(application)
    private val recognizer = OfflineSpeechRecognizer(application)
    private val tts = app.ttsEngine
    private val db = app.database.historyDao()

    private val _sourceLang = MutableStateFlow(registry.getByCode("en"))
    val sourceLang: StateFlow<Language> = _sourceLang.asStateFlow()

    private val _targetLang = MutableStateFlow(registry.getByCode("ar"))
    val targetLang: StateFlow<Language> = _targetLang.asStateFlow()

    val isRecording: StateFlow<Boolean> = recorder.isRecording
    val durationSeconds: StateFlow<Int> = recorder.durationSeconds
    val amplitudes: StateFlow<List<Float>> = recorder.amplitudes

    private val _session = MutableStateFlow(SpeechSession())
    val session: StateFlow<SpeechSession> = _session.asStateFlow()

    val allLanguages: List<Language> = registry.getAllLanguages()

    fun setSourceLang(lang: Language) {
        _sourceLang.value = lang
    }

    fun setTargetLang(lang: Language) {
        _targetLang.value = lang
    }

    fun startRecording() {
        val file = recorder.startRecording()
        _session.value = SpeechSession(audioFile = file, isRecording = true)
    }

    fun endRecording() {
        val audioFile = recorder.stopRecording() ?: return
        _session.value = _session.value.copy(
            isRecording = false,
            isProcessing = true,
            durationSeconds = recorder.durationSeconds.value
        )

        viewModelScope.launch {
            try {
                // 1. Transcribe source speech offline
                val transcriptResult = recognizer.transcribeAudio(audioFile, _sourceLang.value.code)
                val transcript = transcriptResult.getOrNull() ?: ""

                // 2. Translate transcript to target language
                val job = TranslationJob(
                    sourceLanguage = _sourceLang.value,
                    targetLanguage = _targetLang.value,
                    sourceText = transcript,
                    mode = TranslationMode.SPEECH
                )
                val transResult = translationEngine.translate(job)
                val translated = if (transResult is TranslationResult.Success) transResult.translatedText else transcript

                _session.value = _session.value.copy(
                    sourceTranscript = transcript,
                    translatedText = translated,
                    isProcessing = false
                )

                // 3. Save to database history
                db.insert(
                    HistoryEntity(
                        mode = "SPEECH",
                        sourceLangCode = _sourceLang.value.code,
                        sourceLangName = _sourceLang.value.englishName,
                        targetLangCode = _targetLang.value.code,
                        targetLangName = _targetLang.value.englishName,
                        sourceText = transcript,
                        translatedText = translated,
                        audioUri = audioFile.absolutePath
                    )
                )

            } catch (e: Exception) {
                _session.value = _session.value.copy(isProcessing = false)
            }
        }
    }

    fun cancelRecording() {
        recorder.cancelRecording()
        _session.value = SpeechSession()
    }

    fun speakSource() {
        val text = _session.value.sourceTranscript
        if (text.isNotBlank()) {
            tts.speak("speech_src_${System.currentTimeMillis()}", text, _sourceLang.value.code)
        }
    }

    fun speakTarget() {
        val text = _session.value.translatedText
        if (text.isNotBlank()) {
            tts.speak("speech_tgt_${System.currentTimeMillis()}", text, _targetLang.value.code)
        }
    }

    override fun onCleared() {
        super.onCleared()
        recorder.cancelRecording()
    }
}
