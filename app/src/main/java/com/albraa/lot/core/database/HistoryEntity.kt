package com.albraa.lot.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mode: String, // TEXT, IMAGE, SPEECH
    val sourceLangCode: String,
    val sourceLangName: String,
    val targetLangCode: String,
    val targetLangName: String,
    val sourceText: String,
    val translatedText: String,
    val imageUri: String? = null,
    val audioUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
