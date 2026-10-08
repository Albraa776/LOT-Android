package com.albraa.lot.ui.image

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.albraa.lot.R
import com.albraa.lot.ui.components.LanguageSelectorModal
import com.albraa.lot.ui.text.LanguageConsoleSelector
import com.albraa.lot.ui.theme.ConsoleBorder
import com.albraa.lot.ui.theme.InstrumentEmerald
import com.albraa.lot.ui.theme.PhosphorCyan
import java.io.File
import java.io.FileOutputStream

@Composable
fun ImageTranslationScreen(
    viewModel: ImageTranslationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val originalBitmap by viewModel.originalBitmap.collectAsState()
    val translatedBitmap by viewModel.translatedBitmap.collectAsState()
    val processingState by viewModel.processingState.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val regions by viewModel.regions.collectAsState()

    var showSourceSelector by remember { mutableStateOf(false) }
    var showTargetSelector by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
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

        Spacer(modifier = Modifier.height(16.dp))

        // Main Image Workspace Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, ConsoleBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (originalBitmap == null) {
                // Empty state prompt
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Select Photo",
                        tint = PhosphorCyan,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Visual Text Replacement Engine",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Select an image to detect, inpaint background, and replace text in-place offline.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PhosphorCyan,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.image_select_photo),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            } else {
                // Display processed or processing image
                when (processingState) {
                    is ImageProcessingState.DetectingText,
                    is ImageProcessingState.TranslatingText,
                    is ImageProcessingState.InpaintingAndRendering -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PhosphorCyan)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = when (processingState) {
                                    is ImageProcessingState.DetectingText -> "Detecting text regions via OCR…"
                                    is ImageProcessingState.TranslatingText -> "Translating with Hy-MT2 1.8B…"
                                    else -> "Inpainting background & typesetting…"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    color = PhosphorCyan
                                )
                            )
                        }
                    }
                    is ImageProcessingState.Done -> {
                        val trans = translatedBitmap
                        val orig = originalBitmap
                        if (trans != null && orig != null) {
                            when (viewMode) {
                                "slider" -> ImageComparisonSlider(
                                    original = orig,
                                    translated = trans,
                                    modifier = Modifier.fillMaxSize()
                                )
                                "original" -> Image(
                                    bitmap = orig.asImageBitmap(),
                                    contentDescription = "Original",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                                else -> Image(
                                    bitmap = trans.asImageBitmap(),
                                    contentDescription = "Translated",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                    is ImageProcessingState.Error -> {
                        val err = (processingState as ImageProcessingState.Error).message
                        Text(
                            text = "Error: $err",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {}
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Controls Bar (Mode toggles, Save, Share, Select new)
        if (translatedBitmap != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View Mode Toggles
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, ConsoleBorder, RoundedCornerShape(8.dp))
                ) {
                    listOf("slider" to "Split", "original" to "Orig", "translated" to "Trans").forEach { (mode, label) ->
                        val isSel = viewMode == mode
                        Box(
                            modifier = Modifier
                                .clickable { viewModel.setViewMode(mode) }
                                .background(if (isSel) PhosphorCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) PhosphorCyan else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }

                // Action buttons
                Row {
                    IconButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Select new",
                            tint = PhosphorCyan
                        )
                    }
                    IconButton(
                        onClick = {
                            translatedBitmap?.let { bmp ->
                                shareBitmap(context, bmp)
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
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

private fun shareBitmap(context: android.content.Context, bitmap: Bitmap) {
    try {
        val cachePath = File(context.cacheDir, "images").apply { mkdirs() }
        val stream = FileOutputStream("$cachePath/shared_translation.png")
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val imagePath = File(context.cacheDir, "images/shared_translation.png")
        val contentUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imagePath)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "image/png"
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Translated Image"))
    } catch (e: Exception) {
        Toast.makeText(context, "Share error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
