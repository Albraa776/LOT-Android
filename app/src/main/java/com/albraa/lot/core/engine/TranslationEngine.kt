package com.albraa.lot.core.engine

import com.albraa.lot.core.model.TranslationJob
import com.albraa.lot.core.model.TranslationResult
import kotlinx.coroutines.flow.Flow

interface TranslationEngine {
    val engineName: String
    val version: String

    suspend fun translate(job: TranslationJob): TranslationResult
    fun translateStream(job: TranslationJob): Flow<TranslationResult>
    fun isReady(): Boolean
}
