package com.albraa.lot.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.albraa.lot.R
import com.albraa.lot.ui.components.ConsoleTopBar
import com.albraa.lot.ui.history.HistoryScreen
import com.albraa.lot.ui.history.HistoryViewModel
import com.albraa.lot.ui.image.ImageTranslationScreen
import com.albraa.lot.ui.image.ImageTranslationViewModel
import com.albraa.lot.ui.settings.SettingsScreen
import com.albraa.lot.ui.settings.SettingsViewModel
import com.albraa.lot.ui.speech.SpeechTranslationScreen
import com.albraa.lot.ui.speech.SpeechTranslationViewModel
import com.albraa.lot.ui.text.TextTranslationScreen
import com.albraa.lot.ui.text.TextTranslationViewModel
import com.albraa.lot.ui.theme.ConsoleBorder
import com.albraa.lot.ui.theme.PhosphorCyan

enum class MainConsoleTab {
    TEXT,
    IMAGE,
    SPEECH,
    HISTORY,
    SETTINGS
}

@Composable
fun MainScreen() {
    var currentTab by remember { mutableStateOf(MainConsoleTab.TEXT) }

    val textViewModel: TextTranslationViewModel = viewModel()
    val imageViewModel: ImageTranslationViewModel = viewModel()
    val speechViewModel: SpeechTranslationViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    Scaffold(
        topBar = {
            ConsoleTopBar()
        },
        bottomBar = {
            ConsoleBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainConsoleTab.TEXT -> TextTranslationScreen(viewModel = textViewModel)
                MainConsoleTab.IMAGE -> ImageTranslationScreen(viewModel = imageViewModel)
                MainConsoleTab.SPEECH -> SpeechTranslationScreen(viewModel = speechViewModel)
                MainConsoleTab.HISTORY -> HistoryScreen(viewModel = historyViewModel)
                MainConsoleTab.SETTINGS -> SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}

@Composable
private fun ConsoleBottomNavigationBar(
    currentTab: MainConsoleTab,
    onTabSelected: (MainConsoleTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, ConsoleBorder)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConsoleNavButton(
                icon = Icons.Default.TextFields,
                label = stringResource(R.string.mode_text),
                isSelected = currentTab == MainConsoleTab.TEXT,
                onClick = { onTabSelected(MainConsoleTab.TEXT) }
            )
            ConsoleNavButton(
                icon = Icons.Default.Image,
                label = stringResource(R.string.mode_image),
                isSelected = currentTab == MainConsoleTab.IMAGE,
                onClick = { onTabSelected(MainConsoleTab.IMAGE) }
            )
            ConsoleNavButton(
                icon = Icons.Default.Mic,
                label = stringResource(R.string.mode_speech),
                isSelected = currentTab == MainConsoleTab.SPEECH,
                onClick = { onTabSelected(MainConsoleTab.SPEECH) }
            )
            ConsoleNavButton(
                icon = Icons.Default.History,
                label = stringResource(R.string.mode_history),
                isSelected = currentTab == MainConsoleTab.HISTORY,
                onClick = { onTabSelected(MainConsoleTab.HISTORY) }
            )
            ConsoleNavButton(
                icon = Icons.Default.Settings,
                label = stringResource(R.string.mode_settings),
                isSelected = currentTab == MainConsoleTab.SETTINGS,
                onClick = { onTabSelected(MainConsoleTab.SETTINGS) }
            )
        }
    }
}

@Composable
private fun ConsoleNavButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(22.dp),
            tint = if (isSelected) PhosphorCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) PhosphorCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )
    }
}
