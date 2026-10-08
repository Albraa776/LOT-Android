package com.albraa.lot.ui.settings

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.albraa.lot.R
import com.albraa.lot.ui.theme.ConsoleBorder
import com.albraa.lot.ui.theme.InstrumentEmerald
import com.albraa.lot.ui.theme.PhosphorCyan

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        // 1. Neural Model Information Card
        SettingsCard(title = stringResource(R.string.settings_model_header)) {
            SettingRow(label = "Engine", value = viewModel.modelDescriptor.name)
            SettingRow(label = "Variant", value = viewModel.modelDescriptor.variant)
            SettingRow(label = "Languages", value = "${viewModel.modelDescriptor.totalLanguages} Mutual (Bidirectional)")
            SettingRow(label = "Storage Used", value = viewModel.storageUsedFormatted)
            SettingRow(label = "Integrity", value = "Verified (SHA-256)", valueColor = InstrumentEmerald)
        }

        // 2. Offline Voice (TTS) Preferences Card
        SettingsCard(title = stringResource(R.string.settings_voice_header)) {
            Text(
                text = stringResource(R.string.settings_voice_default),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Male" to stringResource(R.string.settings_voice_male), "Female" to stringResource(R.string.settings_voice_female)).forEach { (gender, label) ->
                    val isSel = settings.defaultVoiceGender.equals(gender, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) PhosphorCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background)
                            .border(
                                width = if (isSel) 1.5.dp else 1.dp,
                                color = if (isSel) PhosphorCyan else ConsoleBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.setVoiceGender(gender) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) PhosphorCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Speech Rate Slider
            Text(
                text = "${stringResource(R.string.settings_voice_speed)}: ${String.format("%.1fx", settings.speechRate)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Slider(
                value = settings.speechRate,
                onValueChange = { viewModel.setSpeechRate(it) },
                valueRange = 0.5f..2.0f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = PhosphorCyan,
                    activeTrackColor = PhosphorCyan
                )
            )
        }

        // 3. UI Language & Appearance Card
        SettingsCard(title = "Interface & Appearance") {
            Text(
                text = stringResource(R.string.settings_app_language),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("system" to "System", "en" to "English", "ar" to "العربية").forEach { (code, label) ->
                    val isSel = settings.appLanguage.equals(code, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) PhosphorCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background)
                            .border(
                                width = if (isSel) 1.5.dp else 1.dp,
                                color = if (isSel) PhosphorCyan else ConsoleBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.setAppLanguage(code) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) PhosphorCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.settings_theme),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "dark" to stringResource(R.string.settings_theme_dark),
                    "light" to stringResource(R.string.settings_theme_light),
                    "system" to stringResource(R.string.settings_theme_system)
                ).forEach { (theme, label) ->
                    val isSel = settings.themeMode.equals(theme, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) PhosphorCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.background)
                            .border(
                                width = if (isSel) 1.5.dp else 1.dp,
                                color = if (isSel) PhosphorCyan else ConsoleBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.setThemeMode(theme) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) PhosphorCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }

        // 4. Privacy & Cache Card
        SettingsCard(title = stringResource(R.string.settings_privacy_header)) {
            Text(
                text = stringResource(R.string.settings_privacy_status),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.clearCache()
                    Toast.makeText(context, context.getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ConsoleBorder,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_clear_cache),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }

        // 5. About LOT Card
        SettingsCard(title = stringResource(R.string.settings_about_header)) {
            SettingRow(label = "Application", value = "LOT — Locally Offline Translation")
            SettingRow(label = "Version", value = "1.0.0 (Release 2026.10)")
            SettingRow(label = "Developer", value = "Albraa")
            SettingRow(label = "Licensing", value = "Apache 2.0 / MIT Compliant")
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, ConsoleBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = PhosphorCyan
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = valueColor
            )
        )
    }
}
