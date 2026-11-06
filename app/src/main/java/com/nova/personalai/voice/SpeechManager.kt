package com.nova.personalai.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class SpeechManager(
    private val context: Context
) {
    private var recognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    var isListening: Boolean = false
        private set

    fun startListening(
        languageCode: String = "bn-BD",
        onRmsChanged: ((Float) -> Unit)? = null,
        onPartialResult: ((String) -> Unit)? = null,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        mainHandler.post {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                onError("স্পিচ রিকগনিশন সার্ভিস ফোনে সক্রিয় নেই। নিচের কুইক বাটনে বা টাইপ করে কমান্ড দিন।")
                isListening = false
                return@post
            }

            destroy()

            try {
                recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                isListening = true

                recognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val recognizedText = matches[0]
                            onResult(recognizedText)
                        } else {
                            onError("কোনো কথা স্পষ্ট বোঝা যায়নি, আবার বলুন।")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            val partial = matches[0]
                            if (partial.isNotBlank()) {
                                onPartialResult?.invoke(partial)
                            }
                        }
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "মাইক্রোফোন অডিও রেকর্ডিং এরর"
                            SpeechRecognizer.ERROR_CLIENT -> "ক্লায়েন্ট সার্ভিস ব্যস্ত"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "অডিও রেকর্ডিং পারমিশন দিন"
                            SpeechRecognizer.ERROR_NETWORK -> "ইন্টারনেট সংযোগ চেক করুন"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "নেটওয়ার্ক রেসপন্স টাইমআউট"
                            SpeechRecognizer.ERROR_NO_MATCH -> "কথা বোঝা যায়নি, আবার বলুন (যেমন: 'YouTube খোলো')"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "ভয়েস সার্ভিস পুনরায় চালু হচ্ছে..."
                            SpeechRecognizer.ERROR_SERVER -> "স্পিচ সার্ভার এরর"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "কোনো কথা শোনা যায়নি"
                            else -> "ভয়েস কোড ($error)"
                        }
                        onError(errorMessage)
                    }

                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {
                        onRmsChanged?.invoke(rmsdB)
                    }
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isListening = false
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                    putExtra("android.speech.extra.DICTATION_MODE", true)
                }

                recognizer?.startListening(intent)
            } catch (e: Exception) {
                isListening = false
                onError("ভয়েস ইনপুট এরর: ${e.localizedMessage}")
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                recognizer?.stopListening()
            } catch (_: Exception) {}
            isListening = false
        }
    }

    fun destroy() {
        mainHandler.post {
            try {
                recognizer?.stopListening()
                recognizer?.cancel()
                recognizer?.destroy()
            } catch (_: Exception) {}
            recognizer = null
            isListening = false
        }
    }
}
