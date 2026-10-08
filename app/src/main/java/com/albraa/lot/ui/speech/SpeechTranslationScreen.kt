package com.albraa.lot.ui.speech

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.albraa.lot.R
import com.albraa.lot.core.model.TextDirection
import com.albraa.lot.ui.components.AudioWaveform
import com.albraa.lot.ui.components.LanguageSelectorModal
import com.albraa.lot.ui.text.LanguageConsoleSelector
import com.albraa.lot.ui.theme.ConsoleBorder
import com.albraa.lot.ui.theme.InstrumentEmerald
import com.albraa.lot.ui.theme.InstrumentRed
import com.albraa.lot.ui.theme.PhosphorCyan

@Composable
fun SpeechTranslationScreen(
    viewModel: SpeechTranslationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val durationSeconds by viewModel.durationSeconds.collectAsState()
    val amplitudes by viewModel.amplitudes.collectAsState()
    val session by viewModel.session.collectAsState()

    var showSourceSelector by remember { mutableStateOf(false) }
    var showTargetSelector by remember { mutableStateOf(false) }
    var hasMicPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) {
            viewModel.startRecording()
        } else {
            Toast.makeText(context, context.getString(R.string.speech_permission_required), Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Language Selector
        LanguageConsoleSelector(
            sourceLang = sourceLang,
            targetLang = targetLang,
            onSelectSource = { showSourceSelector = true },
            onSelectTarget = { showTargetSelector = true },
            onSwap = {
                val s = sourceLang
                viewModel.setSourceLang(targetLang)
                viewModel.setTargetLang(s)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Central Recording Console Deck
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = if (isRecording) 1.5.dp else 1.dp,
                    color = if (isRecording) InstrumentRed else ConsoleBorder,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Timer
                val minutes = durationSeconds / 60
                val seconds = durationSeconds % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        color = if (isRecording) InstrumentRed else MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Waveform Display
                if (isRecording) {
                    AudioWaveform(amplitudes = amplitudes)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ConsoleBorder.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (session.isProcessing) stringResource(R.string.speech_transcribing)
                            else stringResource(R.string.speech_tap_to_record),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Record Button
                if (!isRecording) {
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PhosphorCyan,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Start Recording",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.cancelRecording() },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ConsoleBorder)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = { viewModel.endRecording() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = InstrumentRed,
                                contentColor = Color.White
                            ),
                            shape = CircleShape,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "End Recording",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Results Section (Source Transcript & Translation)
        AnimatedVisibility(visible = session.sourceTranscript.isNotBlank() || session.isProcessing) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (session.isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PhosphorCyan)
                    }
                } else {
                    // Source Transcript Card
                    SpeechTranscriptCard(
                        title = stringResource(R.string.speech_source_transcript),
                        text = session.sourceTranscript,
                        isRtl = sourceLang.direction == TextDirection.RTL,
                        onSpeak = { viewModel.speakSource() },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("LOT Source Transcript", session.sourceTranscript))
                            Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
                        }
                    )

                    // Target Translation Card
                    SpeechTranscriptCard(
                        title = stringResource(R.string.speech_translation_result),
                        text = session.translatedText,
                        isRtl = targetLang.direction == TextDirection.RTL,
                        titleColor = InstrumentEmerald,
                        onSpeak = { viewModel.speakTarget() },
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("LOT Translated Speech", session.translatedText))
                            Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showSourceSelector) {
        LanguageSelectorModal(
            title = stringResource(R.string.source_language),
            currentSelectedLanguage = sourceLang,
            allLanguages = viewModel.allLanguages,
            onLanguageSelected = { viewModel.setSourceLang(it) },
            onDismiss = { showSourceSelector = false }
        )
    }

    if (showTargetSelector) {
        LanguageSelectorModal(
            title = stringResource(R.string.target_language),
            currentSelectedLanguage = targetLang,
            allLanguages = viewModel.allLanguages,
            onLanguageSelected = { viewModel.setTargetLang(it) },
            onDismiss = { showTargetSelector = false }
        )
    }
}

@Composable
private fun SpeechTranscriptCard(
    title: String,
    text: String,
    isRtl: Boolean,
    titleColor: Color = PhosphorCyan,
    onSpeak: () -> Unit,
    onCopy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, ConsoleBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = titleColor
                    )
                )
                Row {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak",
                            tint = PhosphorCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
