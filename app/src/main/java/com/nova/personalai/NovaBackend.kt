package com.nova.personalai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * NovaBackend connects the Android app to a custom PHP Backend (api.php)
 * or cloud proxy with X-NOVA-TOKEN authorization.
 */
class NovaBackend(
    val baseUrl: String,
    val token: String
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun ask(
        message: String,
        mode: String = "chat",
        imageBase64: String = ""
    ): String = withContext(Dispatchers.IO) {
        require(message.isNotBlank()) {
            "প্রথমে প্রশ্ন লিখুন।"
        }
        val cleanUrl = baseUrl.trim()
        require(cleanUrl.startsWith("http://") || cleanUrl.startsWith("https://")) {
            "URL অবশ্যই http:// অথবা https:// দিয়ে শুরু হতে হবে।"
        }

        val targetUrl = if (cleanUrl.endsWith(".php") || cleanUrl.contains("exec") || cleanUrl.contains("?")) {
            cleanUrl
        } else {
            "${cleanUrl.trimEnd('/')}/api.php"
        }

        val payload = JSONObject().apply {
            put("message", message)
            put("prompt", message)
            put("mode", mode)
            put("token", token)
            if (imageBase64.isNotBlank()) {
                put("image", imageBase64)
                put("image_base64", imageBase64)
            }
        }.toString()

        val request = Request.Builder()
            .url(targetUrl)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("X-NOVA-TOKEN", token)
            .post(payload.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                throw IllegalStateException("PHP Backend HTTP error: ${response.code}\n${text.take(120)}")
            }

            val result = try {
                JSONObject(text)
            } catch (e: Exception) {
                throw IllegalStateException("PHP সার্ভার থেকে ইনভ্যালিড রেসপন্স: ${text.take(100)}")
            }

            if (result.has("error") && !result.isNull("error")) {
                val err = result.optString("error")
                if (err.isNotBlank()) {
                    throw IllegalStateException("PHP সার্ভার ত্রুটি: $err")
                }
            }

            val reply = result.optString("reply").ifBlank {
                result.optString("text").ifBlank {
                    result.optJSONObject("data")?.optString("reply") ?: ""
                }
            }

            if (reply.isBlank()) {
                throw IllegalStateException("PHP Backend থেকে কোনো উত্তর পাওয়া যায়নি।")
            }

            reply
        }
    }

    suspend fun testConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val response = ask("হ্যালো নোভা", "chat")
            true to "✓ সংযোগ সফল! সার্ভার রেসপন্স: ${response.take(60)}..."
        } catch (e: Exception) {
            val rawMsg = e.localizedMessage ?: "সংযোগ ব্যর্থ হয়েছে"
            val friendlyMsg = when {
                e is java.net.ConnectException || rawMsg.contains("Failed to connect") || rawMsg.contains("Connection refused") -> {
                    if (baseUrl.contains("127.0.0.1") || baseUrl.contains("localhost")) {
                        "⚠️ Termux লোকালহোস্টে সার্ভার কানেক্ট করা যায়নি। নিশ্চিত করুন Termux অ্যাপে 'php -S 0.0.0.0:8080' কমান্ডটি ব্যাকগ্রাউন্ডে চলছে।"
                    } else {
                        "⚠️ সার্ভারে কানেক্ট করা সম্ভব হয়নি: $rawMsg। সার্ভার চালু আছে কি না ও নেটওয়ার্ক চেক করুন।"
                    }
                }
                e is java.net.UnknownHostException -> {
                    "⚠️ ডোমেন পাওয়া যায়নি। সার্ভার URL স্পেলিং সঠিক কি না যাচাই করুন।"
                }
                e is java.net.SocketTimeoutException -> {
                    "⚠️ সার্ভার টাইমআউট: সার্ভার থেকে নির্দিষ্ট সময়ে উত্তর আসেনি।"
                }
                rawMsg.contains("Cleartext HTTP traffic") -> {
                    "⚠️ Cleartext HTTP নীতিগত সীমাবদ্ধতা। HTTPS URL ব্যবহার করুন।"
                }
                rawMsg.contains("GEMINI_API_KEY") -> {
                    "⚠️ PHP সার্ভারে GEMINI_API_KEY সেট করা হয়নি। 'export GEMINI_API_KEY=...' দিয়ে কী সেট করুন।"
                }
                rawMsg.contains("Unauthorized") || rawMsg.contains("X-NOVA-TOKEN") -> {
                    "⚠️ 401 Unauthorized: সিক্রেট টোকেন (X-NOVA-TOKEN) মেলেনি।"
                }
                rawMsg.contains("404") -> {
                    "⚠️ 404 Not Found: প্রদত্ত URL-এ api.php ফাইলটি পাওয়া যায়নি। ফাইল পাথ যাচাই করুন।"
                }
                else -> rawMsg
            }
            false to friendlyMsg
        }
    }
}
