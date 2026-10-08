package com.albraa.lot.core.model

import android.content.Context
import org.json.JSONObject
import java.io.InputStreamReader

class LanguageRegistry(context: Context) {

    private val languages = mutableListOf<Language>()
    private val languageMap = mutableMapOf<String, Language>()

    var engineName: String = "Tencent Hunyuan Hy-MT2"
        private set

    var engineVersion: String = "2.0-compact"
        private set

    init {
        loadRegistry(context)
    }

    private fun loadRegistry(context: Context) {
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("registry/language_capabilities.json")
            val reader = InputStreamReader(inputStream, Charsets.UTF_8)
            val jsonString = reader.readText()
            reader.close()
            inputStream.close()

            val jsonObject = JSONObject(jsonString)
            engineName = jsonObject.optString("engine", engineName)
            engineVersion = jsonObject.optString("version", engineVersion)

            val langArray = jsonObject.getJSONArray("languages")
            for (i in 0 until langArray.length()) {
                val item = langArray.getJSONObject(i)
                val code = item.getString("code")
                val englishName = item.getString("englishName")
                val nativeName = item.getString("nativeName")
                val directionStr = item.getString("direction")
                val script = item.getString("script")
                val translation = item.optBoolean("translationSupported", true)
                val ocr = item.optBoolean("ocrSupported", true)
                val asr = item.optBoolean("asrSupported", true)
                val tts = item.optBoolean("ttsSupported", true)
                val defaultGender = item.optString("defaultVoiceGender", "Male")

                val gendersList = mutableListOf<String>()
                val gendersArray = item.optJSONArray("availableVoiceGenders")
                if (gendersArray != null) {
                    for (g in 0 until gendersArray.length()) {
                        gendersList.add(gendersArray.getString(g))
                    }
                } else {
                    gendersList.add("Male")
                }

                val direction = if (directionStr.equals("RTL", ignoreCase = true)) {
                    TextDirection.RTL
                } else {
                    TextDirection.LTR
                }

                val language = Language(
                    code = code,
                    englishName = englishName,
                    nativeName = nativeName,
                    direction = direction,
                    script = script,
                    translationSupported = translation,
                    ocrSupported = ocr,
                    asrSupported = asr,
                    ttsSupported = tts,
                    defaultVoiceGender = defaultGender,
                    availableVoiceGenders = gendersList
                )

                languages.add(language)
                languageMap[code.lowercase()] = language
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback safety defaults if file read fails
            addFallbackLanguages()
        }
    }

    private fun addFallbackLanguages() {
        val defaultList = listOf(
            Language("en", "English", "English", TextDirection.LTR, "Latin"),
            Language("ar", "Arabic", "العربية", TextDirection.RTL, "Arabic"),
            Language("zh", "Chinese", "简体中文", TextDirection.LTR, "Hanzi"),
            Language("es", "Spanish", "Español", TextDirection.LTR, "Latin"),
            Language("fr", "French", "Français", TextDirection.LTR, "Latin")
        )
        languages.clear()
        languageMap.clear()
        languages.addAll(defaultList)
        for (l in defaultList) {
            languageMap[l.code] = l
        }
    }

    fun getAllLanguages(): List<Language> = languages.toList()

    fun getByCode(code: String): Language {
        return languageMap[code.lowercase()] ?: Language(
            code = code,
            englishName = code.uppercase(),
            nativeName = code.uppercase(),
            direction = TextDirection.LTR,
            script = "Latin"
        )
    }

    fun search(query: String): List<Language> {
        if (query.isBlank()) return getAllLanguages()
        val lowerQuery = query.trim().lowercase()
        return languages.filter { lang ->
            lang.code.lowercase().contains(lowerQuery) ||
                    lang.englishName.lowercase().contains(lowerQuery) ||
                    lang.nativeName.lowercase().contains(lowerQuery) ||
                    lang.script.lowercase().contains(lowerQuery)
        }
    }
}
