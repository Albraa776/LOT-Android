package com.albraa.lot

import android.app.Application
import com.albraa.lot.core.database.LOTDatabase
import com.albraa.lot.core.datastore.SettingsRepository
import com.albraa.lot.core.engine.HyMt2TranslationEngine
import com.albraa.lot.core.engine.ModelManager
import com.albraa.lot.core.model.LanguageRegistry
import com.albraa.lot.core.tts.OfflineTtsEngine

class LOTApplication : Application() {

    lateinit var database: LOTDatabase
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var languageRegistry: LanguageRegistry
        private set

    lateinit var modelManager: ModelManager
        private set

    lateinit var translationEngine: HyMt2TranslationEngine
        private set

    lateinit var ttsEngine: OfflineTtsEngine
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Local Storage & Database
        database = LOTDatabase.getInstance(this)
        settingsRepository = SettingsRepository(this)

        // Initialize Dynamic Language Registry
        languageRegistry = LanguageRegistry(this)

        // Initialize Offline Translation Engine & Model Manager
        modelManager = ModelManager(this)
        translationEngine = HyMt2TranslationEngine(this, modelManager)

        // Initialize Offline TTS Engine
        ttsEngine = OfflineTtsEngine(this)
    }

    companion object {
        lateinit var instance: LOTApplication
            private set
    }
}
