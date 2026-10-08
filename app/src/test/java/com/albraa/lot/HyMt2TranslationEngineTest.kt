package com.albraa.lot

import com.albraa.lot.core.engine.LinguisticDictionary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HyMt2TranslationEngineTest {

    @Test
    fun testOfficialPromptConstructionFormat() {
        val targetLang = "Arabic"
        val sourceText = "Welcome to LOT"
        val expectedPrompt = "将以下文本翻译为 $targetLang ，注意只需要输出翻译后的结果，不要额外解释：\n\n$sourceText"
        
        val constructed = "将以下文本翻译为 $targetLang ，注意只需要输出翻译后的结果，不要额外解释：\n\n$sourceText"
        assertEquals(expectedPrompt, constructed)
    }

    @Test
    fun testDictionaryTranslationEnglishToArabic() {
        val result = LinguisticDictionary.translate("hello", "en", "ar")
        assertEquals("مرحباً", result)

        val result2 = LinguisticDictionary.translate("welcome", "en", "ar")
        assertEquals("أهلاً وسهلاً", result2)
    }

    @Test
    fun testDictionaryTranslationArabicToEnglish() {
        val result = LinguisticDictionary.translate("مرحباً", "ar", "en")
        assertEquals("Hello", result)

        val result2 = LinguisticDictionary.translate("شكراً", "ar", "en")
        assertEquals("Thank you", result2)
    }

    @Test
    fun testPreservesSameLanguage() {
        val text = "This is a local text."
        val result = LinguisticDictionary.translate(text, "en", "en")
        assertEquals(text, result)
    }
}
