#!/usr/bin/env python3
"""
Dynamic Language Capability Registry Generator for LOT (Locally Offline Translation).
Derives language metadata, directionality, writing systems, and modal capabilities
(Translation, OCR, ASR, TTS, Default Voices) based on Tencent Hunyuan Hy-MT2 specification.
"""

import json
import os

HY_MT2_LANGUAGES = [
    {
        "code": "ar",
        "englishName": "Arabic",
        "nativeName": "العربية",
        "direction": "RTL",
        "script": "Arabic",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "en",
        "englishName": "English",
        "nativeName": "English",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "zh",
        "englishName": "Chinese (Simplified)",
        "nativeName": "简体中文",
        "direction": "LTR",
        "script": "Hanzi",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "es",
        "englishName": "Spanish",
        "nativeName": "Español",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "fr",
        "englishName": "French",
        "nativeName": "Français",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "de",
        "englishName": "German",
        "nativeName": "Deutsch",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "ru",
        "englishName": "Russian",
        "nativeName": "Русский",
        "direction": "LTR",
        "script": "Cyrillic",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "ja",
        "englishName": "Japanese",
        "nativeName": "日本語",
        "direction": "LTR",
        "script": "Kanji / Kana",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "ko",
        "englishName": "Korean",
        "nativeName": "한국어",
        "direction": "LTR",
        "script": "Hangul",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "fa",
        "englishName": "Persian (Farsi)",
        "nativeName": "فارسی",
        "direction": "RTL",
        "script": "Perso-Arabic",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "ur",
        "englishName": "Urdu",
        "nativeName": "اردو",
        "direction": "RTL",
        "script": "Perso-Arabic",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "he",
        "englishName": "Hebrew",
        "nativeName": "עברית",
        "direction": "RTL",
        "script": "Hebrew",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "it",
        "englishName": "Italian",
        "nativeName": "Italiano",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "pt",
        "englishName": "Portuguese",
        "nativeName": "Português",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "tr",
        "englishName": "Turkish",
        "nativeName": "Türkçe",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "hi",
        "englishName": "Hindi",
        "nativeName": "हिन्दी",
        "direction": "LTR",
        "script": "Devanagari",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "vi",
        "englishName": "Vietnamese",
        "nativeName": "Tiếng Việt",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "th",
        "englishName": "Thai",
        "nativeName": "ไทย",
        "direction": "LTR",
        "script": "Thai",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "id",
        "englishName": "Indonesian",
        "nativeName": "Bahasa Indonesia",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "ms",
        "englishName": "Malay",
        "nativeName": "Bahasa Melayu",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "nl",
        "englishName": "Dutch",
        "nativeName": "Nederlands",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "pl",
        "englishName": "Polish",
        "nativeName": "Polski",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male", "Female"]
    },
    {
        "code": "uk",
        "englishName": "Ukrainian",
        "nativeName": "Українська",
        "direction": "LTR",
        "script": "Cyrillic",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "cs",
        "englishName": "Czech",
        "nativeName": "Čeština",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "sv",
        "englishName": "Swedish",
        "nativeName": "Svenska",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "el",
        "englishName": "Greek",
        "nativeName": "Ελληνικά",
        "direction": "LTR",
        "script": "Greek",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "hu",
        "englishName": "Hungarian",
        "nativeName": "Magyar",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "ro",
        "englishName": "Romanian",
        "nativeName": "Română",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "da",
        "englishName": "Danish",
        "nativeName": "Dansk",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "fi",
        "englishName": "Finnish",
        "nativeName": "Suomi",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "no",
        "englishName": "Norwegian",
        "nativeName": "Norsk",
        "direction": "LTR",
        "script": "Latin",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": True,
        "ttsSupported": True,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": ["Male"]
    },
    {
        "code": "bo",
        "englishName": "Tibetan",
        "nativeName": "བོད་སྐད་",
        "direction": "LTR",
        "script": "Tibetan",
        "translationSupported": True,
        "ocrSupported": False,
        "asrSupported": False,
        "ttsSupported": False,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": []
    },
    {
        "code": "ug",
        "englishName": "Uyghur",
        "nativeName": "ئۇيغۇرچە",
        "direction": "RTL",
        "script": "Uyghur Arabic",
        "translationSupported": True,
        "ocrSupported": True,
        "asrSupported": False,
        "ttsSupported": False,
        "defaultVoiceGender": "Male",
        "availableVoiceGenders": []
    }
]

registry_data = {
    "engine": "Tencent Hunyuan Hy-MT2",
    "version": "2.0-compact",
    "architecture": "HunYuanDenseV1-1.8B / MoE-30B-A3B",
    "totalLanguages": len(HY_MT2_LANGUAGES),
    "languages": HY_MT2_LANGUAGES
}

def main():
    target_dir = os.path.join("app", "src", "main", "assets", "registry")
    os.makedirs(target_dir, exist_ok=True)
    target_path = os.path.join(target_dir, "language_capabilities.json")
    with open(target_path, "w", encoding="utf-8") as f:
        json.dump(registry_data, f, ensure_ascii=False, indent=2)
    print(f"Generated language capabilities registry at {target_path} with {len(HY_MT2_LANGUAGES)} languages.")

if __name__ == "__main__":
    main()
