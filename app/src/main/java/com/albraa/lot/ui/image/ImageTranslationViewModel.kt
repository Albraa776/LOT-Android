package com.albraa.lot.ui.image

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.albraa.lot.LOTApplication
import com.albraa.lot.core.database.HistoryEntity
import com.albraa.lot.core.inpainting.VisualTextReplacer
import com.albraa.lot.core.model.ImageTranslationRegion
import com.albraa.lot.core.model.Language
import com.albraa.lot.core.model.TranslationJob
import com.albraa.lot.core.model.TranslationMode
import com.albraa.lot.core.model.TranslationResult
import com.albraa.lot.core.ocr.OfflineOcrEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

sealed class ImageProcessingState {
    object Idle : ImageProcessingState()
    object DetectingText : ImageProcessingState()
    object TranslatingText : ImageProcessingState()
    object InpaintingAndRendering : ImageProcessingState()
    object Done : ImageProcessingState()
    data class Error(val message: String) : ImageProcessingState()
}

class ImageTranslationViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LOTApplication
    private val registry = app.languageRegistry
    private val ocrEngine = OfflineOcrEngine(application)
    private val translationEngine = app.translationEngine
    private val visualReplacer = VisualTextReplacer()
    private val db = app.database.historyDao()

    private val _sourceLang = MutableStateFlow(registry.getByCode("en"))
    val sourceLang: StateFlow<Language> = _sourceLang.asStateFlow()

    private val _targetLang = MutableStateFlow(registry.getByCode("ar"))
    val targetLang: StateFlow<Language> = _targetLang.asStateFlow()

    private val _originalBitmap = MutableStateFlow<Bitmap?>(null)
    val originalBitmap: StateFlow<Bitmap?> = _originalBitmap.asStateFlow()

    private val _translatedBitmap = MutableStateFlow<Bitmap?>(null)
    val translatedBitmap: StateFlow<Bitmap?> = _translatedBitmap.asStateFlow()

    private val _regions = MutableStateFlow<List<ImageTranslationRegion>>(emptyList())
    val regions: StateFlow<List<ImageTranslationRegion>> = _regions.asStateFlow()

    private val _processingState = MutableStateFlow<ImageProcessingState>(ImageProcessingState.Idle)
    val processingState: StateFlow<ImageProcessingState> = _processingState.asStateFlow()

    private val _viewMode = MutableStateFlow("slider") // "slider", "original", "translated", "side_by_side"
    val viewMode: StateFlow<String> = _viewMode.asStateFlow()

    val allLanguages: List<Language> = registry.getAllLanguages()

    fun setSourceLang(lang: Language) {
        _sourceLang.value = lang
    }

    fun setTargetLang(lang: Language) {
        _targetLang.value = lang
    }

    fun setViewMode(mode: String) {
        _viewMode.value = mode
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            try {
                _processingState.value = ImageProcessingState.DetectingText
                val bitmap = loadOptimizedBitmap(uri) ?: throw IllegalStateException("Could not load image")
                _originalBitmap.value = bitmap
                _translatedBitmap.value = null

                // 1. Detect Text Regions via OCR
                val ocrRegions = ocrEngine.detectTextRegions(bitmap)
                if (ocrRegions.isEmpty()) {
                    _processingState.value = ImageProcessingState.Error("No text found in image")
                    return@launch
                }

                // 2. Translate Regions
                _processingState.value = ImageProcessingState.TranslatingText
                val regionList = mutableListOf<ImageTranslationRegion>()
                val fullSourceSb = StringBuilder()
                val fullTransSb = StringBuilder()

                for (ocr in ocrRegions) {
                    val job = TranslationJob(
                        sourceLanguage = _sourceLang.value,
                        targetLanguage = _targetLang.value,
                        sourceText = ocr.text,
                        mode = TranslationMode.IMAGE
                    )
                    val transRes = translationEngine.translate(job)
                    val translatedText = if (transRes is TranslationResult.Success) transRes.translatedText else ocr.text

                    regionList.add(
                        ImageTranslationRegion(
                            boundingBox = ocr.boundingBox,
                            sourceText = ocr.text,
                            translatedText = translatedText,
                            confidence = ocr.confidence
                        )
                    )
                    fullSourceSb.append(ocr.text).append("\n")
                    fullTransSb.append(translatedText).append("\n")
                }
                _regions.value = regionList

                // 3. Inpainting & Typography Visual Replacement
                _processingState.value = ImageProcessingState.InpaintingAndRendering
                val renderedBitmap = visualReplacer.replaceTextInImage(bitmap, regionList, _targetLang.value)
                _translatedBitmap.value = renderedBitmap
                _processingState.value = ImageProcessingState.Done

                // 4. Save to database history
                val savedFile = saveBitmapToCache(renderedBitmap)
                db.insert(
                    HistoryEntity(
                        mode = "IMAGE",
                        sourceLangCode = _sourceLang.value.code,
                        sourceLangName = _sourceLang.value.englishName,
                        targetLangCode = _targetLang.value.code,
                        targetLangName = _targetLang.value.englishName,
                        sourceText = fullSourceSb.toString().trim(),
                        translatedText = fullTransSb.toString().trim(),
                        imageUri = savedFile?.absolutePath
                    )
                )

            } catch (e: Exception) {
                _processingState.value = ImageProcessingState.Error(e.localizedMessage ?: "Processing error")
            }
        }
    }

    private suspend fun loadOptimizedBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        val resolver = app.contentResolver
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }

        // Downsample if image is overly large (> 2048px) to protect memory
        var sampleSize = 1
        val maxDim = 2048
        while (options.outWidth / sampleSize > maxDim || options.outHeight / sampleSize > maxDim) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, decodeOptions) }
    }

    private suspend fun saveBitmapToCache(bitmap: Bitmap): File? = withContext(Dispatchers.IO) {
        try {
            val dir = File(app.cacheDir, "translated_images").apply { if (!exists()) mkdirs() }
            val file = File(dir, "trans_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file
        } catch (e: Exception) {
            null
        }
    }
}
