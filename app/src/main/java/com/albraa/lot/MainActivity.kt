package com.albraa.lot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.albraa.lot.ui.MainScreen
import com.albraa.lot.ui.theme.LOTTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as LOTApplication

        setContent {
            val settings by app.settingsRepository.settingsFlow.collectAsState(
                initial = com.albraa.lot.core.model.AppSettings()
            )

            // Determine BiDi layout direction based on app language
            val layoutDirection = when (settings.appLanguage.lowercase()) {
                "ar" -> LayoutDirection.Rtl
                "en" -> LayoutDirection.Ltr
                else -> {
                    val defaultLocale = Locale.getDefault()
                    if (defaultLocale.language.equals("ar", ignoreCase = true)) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    }
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                LOTTheme(themePreference = settings.themeMode) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MainScreen()
                    }
                }
            }
        }
    }
}
