package com.albraa.lot

import com.albraa.lot.core.model.Language
import com.albraa.lot.core.model.TextDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguageRegistryTest {

    @Test
    fun testLanguageDirectionalityRules() {
        val arabic = Language(
            code = "ar",
            englishName = "Arabic",
            nativeName = "العربية",
            direction = TextDirection.RTL,
            script = "Arabic"
        )
        val hebrew = Language(
            code = "he",
            englishName = "Hebrew",
            nativeName = "עברית",
            direction = TextDirection.RTL,
            script = "Hebrew"
        )
        val english = Language(
            code = "en",
            englishName = "English",
            nativeName = "English",
            direction = TextDirection.LTR,
            script = "Latin"
        )

        assertTrue("Arabic must be RTL", arabic.isRtl)
        assertTrue("Hebrew must be RTL", hebrew.isRtl)
        assertFalse("English must be LTR", english.isRtl)
    }

    @Test
    fun testDefaultVoiceIsMale() {
        val arabic = Language(
            code = "ar",
            englishName = "Arabic",
            nativeName = "العربية",
            direction = TextDirection.RTL,
            script = "Arabic",
            defaultVoiceGender = "Male"
        )
        assertEquals("Default voice gender must be Male", "Male", arabic.defaultVoiceGender)
    }

    @Test
    fun testLanguageDisplayName() {
        val arabic = Language(
            code = "ar",
            englishName = "Arabic",
            nativeName = "العربية",
            direction = TextDirection.RTL,
            script = "Arabic"
        )
        assertEquals("العربية (Arabic)", arabic.displayName)

        val english = Language(
            code = "en",
            englishName = "English",
            nativeName = "English",
            direction = TextDirection.LTR,
            script = "Latin"
        )
        assertEquals("English", english.displayName)
    }
}
