package com.nova.personalai.voice

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TTSManager(
    context: Context,
    private val onInitComplete: ((Boolean) -> Unit)? = null
) {
    private var tts: TextToSpeech? = null
    var isInitialized = false
        private set
    private val mainHandler = Handler(Looper.getMainLooper())
    var isSpeaking = false
        private set

    var currentLanguageTag: String = "bn-BD"
    var speechRate: Float = 1.0f
    var speechPitch: Float = 1.05f

    var onSpeakingStateChanged: ((Boolean) -> Unit)? = null

    init {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isInitialized = true
                    try {
                        applyLanguage(currentLanguageTag)
                        tts?.setSpeechRate(speechRate)
                        tts?.setPitch(speechPitch)

                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                mainHandler.post {
                                    isSpeaking = true
                                    onSpeakingStateChanged?.invoke(true)
                                }
                            }

                            override fun onDone(utteranceId: String?) {
                                mainHandler.post {
                                    isSpeaking = false
                                    onSpeakingStateChanged?.invoke(false)
                                }
                            }

                            override fun onError(utteranceId: String?) {
                                mainHandler.post {
                                    isSpeaking = false
                                    onSpeakingStateChanged?.invoke(false)
                                }
                            }
                        })
                    } catch (_: Exception) {}
                    onInitComplete?.invoke(true)
                } else {
                    isInitialized = false
                    onInitComplete?.invoke(false)
                }
            }
        } catch (_: Exception) {
            isInitialized = false
            onInitComplete?.invoke(false)
        }
    }

    fun setVoiceSettings(rate: Float, pitch: Float, languageTag: String = currentLanguageTag) {
        speechRate = rate.coerceIn(0.5f, 2.0f)
        speechPitch = pitch.coerceIn(0.5f, 2.0f)
        currentLanguageTag = languageTag
        tts?.setSpeechRate(speechRate)
        tts?.setPitch(speechPitch)
        applyLanguage(currentLanguageTag)
    }

    private fun applyLanguage(tag: String) {
        val targetLocale = when (tag) {
            "bn-BD", "bn" -> {
                // Check if bn-BD is supported, else try general bn
                val locBnBD = Locale("bn", "BD")
                val resBD = tts?.isLanguageAvailable(locBnBD)
                if (resBD != null && resBD >= TextToSpeech.LANG_AVAILABLE) {
                    locBnBD
                } else {
                    val locBn = Locale("bn")
                    val resBn = tts?.isLanguageAvailable(locBn)
                    if (resBn != null && resBn >= TextToSpeech.LANG_AVAILABLE) locBn else Locale.forLanguageTag("bn-BD")
                }
            }
            "hi-IN", "hi" -> Locale.forLanguageTag("hi-IN")
            else -> Locale.US
        }

        try {
            val result = tts?.setLanguage(targetLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Try device default or US as fallback
                tts?.language = Locale.getDefault()
            }
        } catch (_: Exception) {
            tts?.language = Locale.US
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (!isInitialized || tts == null) {
            onDone?.invoke()
            return
        }

        // Clean up markdown/bullet points/code symbols for ultra-smooth voice narration
        val cleanSpeech = text
            .replace(Regex("```[a-zA-Z]*"), "")
            .replace(Regex("[*#_`~>•\\[\\]]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        if (cleanSpeech.isBlank()) {
            onDone?.invoke()
            return
        }

        tts?.speak(
            cleanSpeech,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "NOVA_VOICE_${System.currentTimeMillis()}"
        )
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
        isSpeaking = false
        onSpeakingStateChanged?.invoke(false)
    }

    fun destroy() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
        isInitialized = false
        isSpeaking = false
    }
}
