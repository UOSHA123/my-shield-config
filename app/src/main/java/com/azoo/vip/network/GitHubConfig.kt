package com.azoo.vip.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GitHubConfig {
    private const val TAG = "GitHubConfig"
    private const val CONFIG_URL = "https://raw.githubusercontent.com/UOSHA123/my-shield-config/main/config.json"
    private val client = OkHttpClient()

    suspend fun fetchRemoteCommands(): Map<String, String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(CONFIG_URL).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext emptyMap()
                    val jsonData = response.body?.string() ?: return@withContext emptyMap()
                    val jsonObject = JSONObject(jsonData)
                    val commands = mutableMapOf<String, String>()
                    val keys = jsonObject.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        commands[key] = jsonObject.getString(key)
                    }
                    Log.i(TAG, "≈ Remote Commands Fetched Successfully")
                    commands
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch remote config", e)
                emptyMap()
            }
        }
    }
}
