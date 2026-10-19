package com.example.data.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class GeminiApiClient(
    context: Context? = null
) {
    private val prefs: SharedPreferences? = try {
        context?.applicationContext?.getSharedPreferences("airnine_api_prefs", Context.MODE_PRIVATE)
    } catch (e: Exception) {
        null
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun getApiKey(): String {
        val customKey = prefs?.getString("custom_gemini_api_key", "")?.trim() ?: ""
        if (customKey.isNotBlank()) {
            return customKey
        }
        val buildKey = BuildConfig.GEMINI_API_KEY.trim()
        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") {
            return buildKey
        }
        return ""
    }

    fun setCustomApiKey(key: String) {
        prefs?.edit()?.putString("custom_gemini_api_key", key.trim())?.apply()
    }

    fun isApiKeyConfigured(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun generateContent(
        prompt: String,
        model: String = "gemini-3.5-flash",
        systemInstruction: String? = null,
        history: List<Pair<String, String>> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()

        if (!isApiKeyConfigured()) {
            Log.w("AirNineApi", "generateContent aborted: Gemini API key is missing or unconfigured")
            return@withContext Result.failure(
                AirNineApiException.MissingApiKeyException()
            )
        }

        try {
            val rootJson = JSONObject()

            // System instruction
            if (!systemInstruction.isNullOrBlank()) {
                val sysInstObj = JSONObject()
                val sysParts = JSONArray()
                sysParts.put(JSONObject().put("text", systemInstruction))
                sysInstObj.put("parts", sysParts)
                rootJson.put("systemInstruction", sysInstObj)
            }

            // Multi-turn contents array with proper turn alternation
            val contentsArray = JSONArray()

            var lastRole: String? = null
            for ((role, text) in history) {
                if (text.isNotBlank()) {
                    val apiRole = if (role.equals("user", ignoreCase = true)) "user" else "model"
                    if (contentsArray.length() == 0 && apiRole != "user") {
                        // Skip leading model turn in history so multi-turn starts with user
                        continue
                    }
                    if (apiRole == lastRole) {
                        // Merge consecutive turns with the same role into one turn to prevent 400 Bad Request
                        val prevTurn = contentsArray.getJSONObject(contentsArray.length() - 1)
                        val prevParts = prevTurn.getJSONArray("parts")
                        prevParts.put(JSONObject().put("text", text))
                    } else {
                        val contentObj = JSONObject()
                        contentObj.put("role", apiRole)
                        val parts = JSONArray()
                        parts.put(JSONObject().put("text", text))
                        contentObj.put("parts", parts)
                        contentsArray.put(contentObj)
                        lastRole = apiRole
                    }
                }
            }

            // Current user turn
            if (lastRole == "user" && contentsArray.length() > 0) {
                val lastTurn = contentsArray.getJSONObject(contentsArray.length() - 1)
                lastTurn.getJSONArray("parts").put(JSONObject().put("text", prompt))
            } else {
                val userContent = JSONObject()
                userContent.put("role", "user")
                val userParts = JSONArray()
                userParts.put(JSONObject().put("text", prompt))
                userContent.put("parts", userParts)
                contentsArray.put(userContent)
            }

            rootJson.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            rootJson.put("generationConfig", genConfig)

            // Normalize model identifier to official Gemini API models
            val targetModel = when (model.lowercase().trim()) {
                "gemini-3.8-flash", "gemini-flash" -> "gemini-3.5-flash"
                "gemini flash", "flash" -> "gemini-flash-latest"
                "gemini-2.5-pro", "gemini pro", "gemini-pro" -> "gemini-3.1-pro-preview"
                "gemini-2.5-flash-lite", "gemini lite", "flash lite" -> "gemini-3.1-flash-lite-preview"
                else -> model.trim()
            }

            // Pass key in URL parameter and x-goog-api-key header for maximum compatibility
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$targetModel:generateContent?key=$apiKey"
            Log.d("AirNineApi", "Request starting: POST models/$targetModel:generateContent (prompt length: ${prompt.length}, history turns: ${contentsArray.length()})")

            val body = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .header("x-goog-api-key", apiKey)
                .post(body)
                .build()

            val startTime = System.currentTimeMillis()
            val response = client.newCall(request).execute()
            val latencyMs = System.currentTimeMillis() - startTime
            val responseBody = response.body?.string() ?: ""

            Log.d("AirNineApi", "Response received: HTTP ${response.code} (latency: ${latencyMs}ms, bytes: ${responseBody.length})")

            if (!response.isSuccessful) {
                val parsedErrorMsg = try {
                    val errJson = JSONObject(responseBody).optJSONObject("error")
                    errJson?.optString("message") ?: ""
                } catch (e: Exception) {
                    ""
                }
                Log.w("AirNineApi", "API Error HTTP ${response.code}: $parsedErrorMsg (Body: $responseBody)")

                val exception = when (response.code) {
                    400 -> AirNineApiException.UnknownApiException("Bad Request: $parsedErrorMsg")
                    401, 403 -> AirNineApiException.InvalidCredentialsException()
                    404 -> AirNineApiException.ModelUnavailableException(
                        "Model $targetModel is unavailable. Please select another model in the Model Hub."
                    )
                    429 -> AirNineApiException.QuotaExceededException()
                    in 500..599 -> AirNineApiException.ServerException()
                    else -> AirNineApiException.UnknownApiException("HTTP ${response.code}: $parsedErrorMsg")
                }
                return@withContext Result.failure(exception)
            }

            val parsedJson = JSONObject(responseBody)

            // Check for promptFeedback block
            val promptFeedback = parsedJson.optJSONObject("promptFeedback")
            val blockReason = promptFeedback?.optString("blockReason")
            if (!blockReason.isNullOrBlank()) {
                Log.w("AirNineApi", "Prompt was blocked by safety policy: $blockReason")
                return@withContext Result.failure(
                    AirNineApiException.UnknownApiException("Prompt blocked by safety filters: $blockReason")
                )
            }

            val candidates = parsedJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val textBuilder = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        textBuilder.append(part.optString("text", ""))
                    }
                    val fullText = textBuilder.toString().trim()
                    if (fullText.isNotEmpty()) {
                        Log.d("AirNineApi", "Response parsed successfully (characters: ${fullText.length})")
                        return@withContext Result.success(fullText)
                    }
                }
                val finishReason = firstCandidate.optString("finishReason")
                if (finishReason.isNotBlank() && finishReason != "STOP") {
                    Log.w("AirNineApi", "Generation ended with non-STOP finishReason: $finishReason")
                    return@withContext Result.failure(
                        AirNineApiException.UnknownApiException("Generation ended with reason: $finishReason")
                    )
                }
            }

            Log.w("AirNineApi", "Empty or unparseable candidate structure received: $responseBody")
            Result.failure(AirNineApiException.EmptyResponseException())
        } catch (e: UnknownHostException) {
            Log.e("AirNineApi", "Network offline / DNS resolution failed", e)
            Result.failure(AirNineApiException.NetworkUnavailableException())
        } catch (e: SocketTimeoutException) {
            Log.e("AirNineApi", "Socket timeout after 60s", e)
            Result.failure(AirNineApiException.RequestTimeoutException())
        } catch (e: IOException) {
            Log.e("AirNineApi", "I/O connection error", e)
            Result.failure(AirNineApiException.NetworkUnavailableException("I/O error communicating with Gemini API."))
        } catch (e: Exception) {
            Log.e("AirNineApi", "Unexpected error in generateContent", e)
            Result.failure(AirNineApiException.UnknownApiException(e.message ?: "Unexpected failure"))
        }
    }
}
