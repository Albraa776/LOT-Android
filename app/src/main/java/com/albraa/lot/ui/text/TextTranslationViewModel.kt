package com.albraa.lot.ui.text

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albraa.lot.LOTApplication
import com.albraa.lot.core.database.HistoryEntity
import com.albraa.lot.core.model.Language
import com.albraa.lot.core.model.TranslationJob
import com.albraa.lot.core.model.TranslationMode
import com.albraa.lot.core.model.TranslationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TextTranslationViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LOTApplication
    private val registry = app.languageRegistry
    private val engine = app.translationEngine
    private val tts = app.ttsEngine
    private val db = app.database.historyDao()
    private val settings = app.settingsRepository

    private val _sourceLang = MutableStateFlow(registry.getByCode("en"))
    val sourceLang: StateFlow<Language> = _sourceLang.asStateFlow()

    private val _targetLang = MutableStateFlow(registry.getByCode("ar"))
    val targetLang: StateFlow<Language> = _targetLang.asStateFlow()

    private val _sourceText = MutableStateFlow("")
    val sourceText: StateFlow<String> = _sourceText.asStateFlow()

    private val _translatedText = MutableStateFlow("")
    val translatedText: StateFlow<String> = _translatedText.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentHistoryId: Long = 0

    val allLanguages: List<Language> = registry.getAllLanguages()

    fun updateSourceText(text: String) {
        _sourceText.value = text
        if (text.isBlank()) {
            _translatedText.value = ""
            _isFavorite.value = false
        }
    }

    fun setSourceLang(lang: Language) {
        _sourceLang.value = lang
        if (_sourceText.value.isNotBlank()) translate()
    }

    fun setTargetLang(lang: Language) {
        _targetLang.value = lang
        if (_sourceText.value.isNotBlank()) translate()
    }

    fun swapLanguages() {
        val prevSource = _sourceLang.value
        val prevTarget = _targetLang.value
        val prevTranslated = _translatedText.value

        _sourceLang.value = prevTarget
        _targetLang.value = prevSource

        if (prevTranslated.isNotBlank()) {
            _sourceText.value = prevTranslated
            _translatedText.value = ""
            translate()
        }
    }

    fun clear() {
        _sourceText.value = ""
        _translatedText.value = ""
        _isFavorite.value = false
    }

    fun translate() {
        val text = _sourceText.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _isTranslating.value = true
            val job = TranslationJob(
                sourceLanguage = _sourceLang.value,
                targetLanguage = _targetLang.value,
                sourceText = text,
                mode = TranslationMode.TEXT
            )

            when (val result = engine.translate(job)) {
                is TranslationResult.Success -> {
                    _translatedText.value = result.translatedText
                    _isTranslating.value = false

                    // Save to Room history
                    currentHistoryId = db.insert(
                        HistoryEntity(
                            mode = "TEXT",
                            sourceLangCode = _sourceLang.value.code,
                            sourceLangName = _sourceLang.value.englishName,
                            targetLangCode = _targetLang.value.code,
                            targetLangName = _targetLang.value.englishName,
                            sourceText = text,
                            translatedText = result.translatedText,
                            isFavorite = false
                        )
                    )
                }
                is TranslationResult.Error -> {
                    _translatedText.value = "[Error]: ${result.message}"
                    _isTranslating.value = false
                }
                else -> {
                    _isTranslating.value = false
                }
            }
        }
    }

    fun toggleFavorite() {
        val newFav = !_isFavorite.value
        _isFavorite.value = newFav
        if (currentHistoryId > 0) {
            viewModelScope.launch {
                db.setFavorite(currentHistoryId, newFav)
            }
        }
    }

    fun speakSource() {
        val text = _sourceText.value
        if (text.isNotBlank()) {
            tts.speak("src_${System.currentTimeMillis()}", text, _sourceLang.value.code)
        }
    }

    fun speakTarget() {
        val text = _translatedText.value
        if (text.isNotBlank()) {
            tts.speak("tgt_${System.currentTimeMillis()}", text, _targetLang.value.code)
        }
    }
}
