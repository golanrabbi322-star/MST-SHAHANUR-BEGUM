package com.nova.personalai

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class BackendType(val id: String, val titleBn: String) {
    DIRECT("direct", "⚡ Direct Gemini 3.8 Flash (বিল্ট-ইন)"),
    PHP("php", "🐘 PHP Backend API (api.php)"),
    APPS_SCRIPT("apps_script", "📜 Google Apps Script")
}

object NovaApi {

    private const val PREFS_NAME = "nova_api_prefs"
    private const val KEY_BACKEND_TYPE = "backend_type"
    private const val KEY_PHP_URL = "php_url"
    private const val KEY_PHP_TOKEN = "php_token"
    private const val KEY_WEB_APP_URL = "web_app_url"
    private const val KEY_ACCESS_TOKEN = "access_token"

    var currentBackendType: BackendType = BackendType.DIRECT
    var phpUrl: String = "http://127.0.0.1:8080/api.php"
    var phpToken: String = "NOVA_SUPER_SECRET_2026"
    var webAppUrl: String = ""
    var accessToken: String = ""

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    fun init(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val typeId = prefs.getString(KEY_BACKEND_TYPE, BackendType.DIRECT.id) ?: BackendType.DIRECT.id
        currentBackendType = BackendType.values().find { it.id == typeId } ?: BackendType.DIRECT
        phpUrl = prefs.getString(KEY_PHP_URL, "http://127.0.0.1:8080/api.php") ?: "http://127.0.0.1:8080/api.php"
        phpToken = prefs.getString(KEY_PHP_TOKEN, "NOVA_SUPER_SECRET_2026") ?: "NOVA_SUPER_SECRET_2026"
        webAppUrl = prefs.getString(KEY_WEB_APP_URL, "") ?: ""
        accessToken = prefs.getString(KEY_ACCESS_TOKEN, "") ?: ""

        if (!prefs.contains(KEY_BACKEND_TYPE)) {
            prefs.edit()
                .putString(KEY_BACKEND_TYPE, currentBackendType.id)
                .putString(KEY_PHP_URL, phpUrl)
                .putString(KEY_PHP_TOKEN, phpToken)
                .apply()
        }
    }

    fun saveConfig(
        context: Context,
        backendType: BackendType,
        phpUrlInput: String,
        phpTokenInput: String,
        appsScriptUrlInput: String,
        appsScriptTokenInput: String
    ) {
        currentBackendType = backendType
        phpUrl = phpUrlInput.trim()
        phpToken = phpTokenInput.trim()
        webAppUrl = appsScriptUrlInput.trim()
        accessToken = appsScriptTokenInput.trim()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_BACKEND_TYPE, currentBackendType.id)
            .putString(KEY_PHP_URL, phpUrl)
            .putString(KEY_PHP_TOKEN, phpToken)
            .putString(KEY_WEB_APP_URL, webAppUrl)
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply()
    }

    // Backward-compatible signature
    fun saveConfig(context: Context, url: String, token: String) {
        webAppUrl = url.trim()
        accessToken = token.trim()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_WEB_APP_URL, webAppUrl)
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply()
    }

    val instructions = mapOf(
        "chat" to (
            "You are NOVA, a helpful multilingual AI assistant. " +
            "Prefer simple Bengali when the user writes in Bengali. " +
            "Be honest about your capabilities."
        ),
        "story" to (
            "Write an original story and production-ready video script. " +
            "Respond in the user's language. Include a title, narration, " +
            "scene-by-scene timing, visual descriptions, image prompts, " +
            "sound suggestions, and subtitle text. Do not claim to have " +
            "generated image or video files."
        ),
        "storyboard" to (
            "Convert the request into a practical storyboard. " +
            "Include scene number, duration, narration, visual prompt, " +
            "subtitle, camera movement, and sound suggestion."
        ),
        "marketing" to (
            "Act as a responsible digital marketing assistant. " +
            "Create original content, SEO plans and content calendars. " +
            "Do not claim to have published anything."
        ),
        "builder" to (
            "Act as a software development assistant. " +
            "Provide code, file paths, setup steps, and security notes. " +
            "Never expose API secrets in client-side code."
        )
    )

    suspend fun ask(
        prompt: String,
        mode: String = "chat",
        imageBase64: String = ""
    ): String = withContext(Dispatchers.IO) {
        require(prompt.isNotBlank() || imageBase64.isNotBlank()) {
            "Please enter a message or provide an image."
        }

        val effectivePrompt = if (prompt.isBlank() && imageBase64.isNotBlank()) {
            "এই স্ক্রিনটি মনোযোগ দিয়ে পর্যবেক্ষণ করো। এতে কী কী উপাদান, তথ্য বা ইন্টারফেস আছে তা বাংলায় স্পষ্ট ও বিশদভাবে বুঝিয়ে বলো এবং কোনো সমস্যা বা গুরুত্বপূর্ণ বিষয় থাকলে তা উল্লেখ করো।"
        } else {
            prompt
        }

        // 1. If PHP Backend is active and configured
        if (currentBackendType == BackendType.PHP && phpUrl.isNotBlank() && phpUrl.startsWith("http")) {
            try {
                return@withContext NovaBackend(phpUrl, phpToken).ask(effectivePrompt, mode, imageBase64)
            } catch (e: Exception) {
                android.util.Log.w("NovaApi", "PHP backend unavailable (${e.message}), falling back to direct engine")
            }
        }

        // 2. If Google Apps Script is active and configured
        if (currentBackendType == BackendType.APPS_SCRIPT && webAppUrl.isNotBlank() && webAppUrl.startsWith("http") && !webAppUrl.contains("YOUR_WEB_APP_URL")) {
            try {
                return@withContext callBackend(effectivePrompt, mode)
            } catch (e: Exception) {
                android.util.Log.w("NovaApi", "Apps Script unavailable (${e.message})")
            }
        }

        // 3. Direct Gemini 3.8 Flash (Built-in or user key)
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }.let {
            if (it.isNotBlank() && it != "MY_GEMINI_API_KEY") it
            else ""
        }

        if (apiKey.isNotBlank()) {
            try {
                return@withContext callDirectGemini(effectivePrompt, mode, apiKey, imageBase64)
            } catch (e: Exception) {
                android.util.Log.w("NovaApi", "Direct Gemini unavailable (${e.message})")
            }
        }

        // 4. Autonomous Local AI Engine Fallback
        return@withContext com.nova.personalai.ai.AIEngine().process(effectivePrompt)
    }

    private fun callBackend(prompt: String, mode: String): String {
        val payload = JSONObject()
            .put("token", accessToken)
            .put("prompt", prompt)
            .put("mode", mode)
            .toString()

        val requestBody = payload.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(webAppUrl)
            .post(requestBody)
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IllegalStateException("Backend HTTP error: ${response.code}")
            }

            val json = JSONObject(body)
            if (!json.optBoolean("ok")) {
                throw IllegalStateException(
                    json.optString("error", "AI request failed from backend")
                )
            }

            val text = json.optString("text")
            if (text.isBlank()) {
                return "AI থেকে কোনো লেখা পাওয়া যায়নি।"
            }
            return text
        }
    }

    private fun callDirectGemini(
        prompt: String,
        mode: String,
        apiKey: String,
        imageBase64: String = ""
    ): String {
        val instruction = instructions[mode] ?: instructions["chat"]!!
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            val partsArray = JSONArray().apply {
                if (imageBase64.isNotBlank()) {
                    put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", imageBase64)
                        })
                    })
                }
                put(JSONObject().apply { put("text", prompt) })
            }

            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", partsArray)
                })
            }
            put("contents", contents)

            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", instruction) })
                })
            })

            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 4096)
            })
        }

        val requestBody = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Gemini API error code: ${response.code}")
            }
            val body = response.body?.string().orEmpty()
            val json = JSONObject(body)
            val candidates = json.optJSONArray("candidates") ?: return "AI থেকে কোনো ফলাফল পাওয়া যায়নি।"
            if (candidates.length() == 0) return "AI থেকে কোনো ফলাফল পাওয়া যায়নি।"

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return "কোনো কনটেন্ট তৈরি হয়নি।"
            val parts = content.optJSONArray("parts") ?: return "কোনো পার্টস পাওয়া যায়নি।"

            val textList = mutableListOf<String>()
            for (i in 0 until parts.length()) {
                val p = parts.getJSONObject(i)
                textList.add(p.optString("text", ""))
            }

            val result = textList.joinToString("\n").trim()
            if (result.isBlank()) {
                return "AI থেকে কোনো লেখা পাওয়া যায়নি।"
            }
            return result
        }
    }

    /**
     * Complete, copy-paste ready PHP api.php script with gemini-3.8-flash
     */
    fun getSamplePhpCode(): String {
        val s = "$"
        return """
<?php
declare(strict_types=1);

header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store');

function respond(int ${s}status, array ${s}data): never {
    http_response_code(${s}status);
    echo json_encode(
        ${s}data,
        JSON_UNESCAPED_UNICODE | JSON_INVALID_UTF8_SUBSTITUTE
    );
    exit;
}

if (${s}_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Allow: POST');
    respond(405, ['error' => 'POST method required']);
}

${s}apiKey = getenv('GEMINI_API_KEY') ?: '';
${s}backendToken = getenv('NOVA_API_TOKEN') ?: 'NOVA_SUPER_SECRET_2026';

${s}providedToken = ${s}_SERVER['HTTP_X_NOVA_TOKEN'] ?? '';
${s}raw = file_get_contents('php://input');

if (${s}raw === false || strlen(${s}raw) > 30000) {
    respond(413, ['error' => 'Request too large']);
}

${s}body = json_decode(${s}raw, true) ?? [];
if (${s}providedToken === '' && isset(${s}body['token'])) {
    ${s}providedToken = (string)${s}body['token'];
}

if (${s}backendToken !== '' && !hash_equals(${s}backendToken, ${s}providedToken)) {
    respond(401, ['error' => 'Unauthorized: Invalid X-NOVA-TOKEN']);
}

if (${s}apiKey === '') {
    respond(500, ['error' => 'GEMINI_API_KEY is not configured on PHP server']);
}

${s}message = trim((string)(${s}body['message'] ?? ${s}body['prompt'] ?? ''));
if (${s}message === '' && empty(${s}body['image']) && empty(${s}body['image_base64'])) {
    respond(400, ['error' => 'Message or image is required']);
}
if (${s}message === '') {
    ${s}message = "এই স্ক্রিন বা ছবিটি বিশ্লেষণ করো এবং বাংলায় বিস্তারিত বলো।";
}

${s}parts = [];
if (!empty(${s}body['image']) || !empty(${s}body['image_base64'])) {
    ${s}rawImg = (string)(${s}body['image'] ?? ${s}body['image_base64']);
    ${s}parts[] = [
        'inlineData' => [
            'mimeType' => 'image/jpeg',
            'data' => ${s}rawImg
        ]
    ];
}
${s}parts[] = [
    'text' => "You are NOVA Personal AI. Reply helpfully in the user's language.\n\n" . ${s}message
];

${s}payload = [
    'contents' => [[
        'role' => 'user',
        'parts' => ${s}parts
    ]],
    'generationConfig' => [
        'temperature' => 0.7,
        'maxOutputTokens' => 3000
    ]
];

${s}url = 'https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=' . rawurlencode(${s}apiKey);

${s}ch = curl_init(${s}url);
curl_setopt_array(${s}ch, [
    CURLOPT_POST => true,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_CONNECTTIMEOUT => 15,
    CURLOPT_TIMEOUT => 60,
    CURLOPT_HTTPHEADER => ['Content-Type: application/json'],
    CURLOPT_POSTFIELDS => json_encode(${s}payload),
]);

${s}response = curl_exec(${s}ch);
${s}httpCode = (int)curl_getinfo(${s}ch, CURLINFO_HTTP_CODE);
${s}curlError = curl_error(${s}ch);
curl_close(${s}ch);

if (${s}response === false) {
    respond(502, ['error' => 'cURL Error: ' . ${s}curlError]);
}

${s}result = json_decode(${s}response, true);
if (${s}httpCode !== 200) {
    ${s}err = ${s}result['error']['message'] ?? "Gemini HTTP ${s}httpCode";
    respond(502, ['error' => ${s}err]);
}

${s}answer = ${s}result['candidates'][0]['content']['parts'][0]['text'] ?? '';
if (${s}answer === '') {
    respond(502, ['error' => 'No text returned from Gemini API']);
}

respond(200, ['reply' => ${s}answer]);
        """.trimIndent()
    }
}
