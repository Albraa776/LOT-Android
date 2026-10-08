package com.albraa.lot.core.asr

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AudioRecorder(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var currentFile: File? = null
    private var amplitudeJob: Job? = null

    private val _amplitudes = MutableStateFlow<List<Float>>(emptyList())
    val amplitudes: StateFlow<List<Float>> = _amplitudes.asStateFlow()

    private val _durationSeconds = MutableStateFlow(0)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    fun startRecording(): File? {
        try {
            val audioDir = File(context.cacheDir, "audio_sessions").apply { if (!exists()) mkdirs() }
            val outputFile = File(audioDir, "rec_${System.currentTimeMillis()}.m4a")
            currentFile = outputFile

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            _isRecording.value = true
            _durationSeconds.value = 0
            _amplitudes.value = emptyList()

            // Start polling amplitude and timer
            amplitudeJob = CoroutineScope(Dispatchers.Default).launch {
                var seconds = 0
                var counter = 0
                val ampList = mutableListOf<Float>()

                while (isActive && _isRecording.value) {
                    delay(100)
                    counter++
                    val maxAmp = try {
                        recorder?.maxAmplitude ?: 0
                    } catch (e: Exception) {
                        0
                    }
                    val normalized = (maxAmp / 32767f).coerceIn(0.05f, 1.0f)
                    ampList.add(normalized)
                    if (ampList.size > 50) ampList.removeAt(0)
                    _amplitudes.value = ampList.toList()

                    if (counter % 10 == 0) {
                        seconds++
                        _durationSeconds.value = seconds
                    }
                }
            }

            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            cleanup()
            return null
        }
    }

    fun stopRecording(): File? {
        amplitudeJob?.cancel()
        amplitudeJob = null
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recorder = null
        _isRecording.value = false
        return currentFile
    }

    fun cancelRecording() {
        stopRecording()
        currentFile?.delete()
        currentFile = null
        cleanup()
    }

    private fun cleanup() {
        _isRecording.value = false
        _durationSeconds.value = 0
        _amplitudes.value = emptyList()
    }
}
