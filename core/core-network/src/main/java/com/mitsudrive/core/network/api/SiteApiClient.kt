package com.mitsudrive.core.network.api

import com.mitsudrive.core.network.model.SiteApiResponse
import com.mitsudrive.core.network.model.SiteUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SiteApiClient {
    
    companion object {
        private const val BASE_URL = "https://mastermitsu.ru/api/"
    }
    
    // Регистрация
    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String,
        deviceId: String
    ): SiteApiResponse = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("name", name)
            put("phone", phone)
            put("device_id", deviceId)
        }
        
        val response = postRequest("app_register.php", json)
        parseResponse(response)
    }
    
    // Вход
    suspend fun login(
        email: String,
        password: String,
        deviceId: String
    ): SiteApiResponse = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("device_id", deviceId)
        }
        
        val response = postRequest("app_login.php", json)
        parseResponse(response)
    }
    
    // Проверка токена
    suspend fun checkToken(token: String): SiteApiResponse = withContext(Dispatchers.IO) {
        val url = URL(BASE_URL + "app_check_token.php?token=" + token)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        
        val response = conn.inputStream.bufferedReader().readText()
        parseResponse(response)
    }
    
    // Выход
    suspend fun logout(token: String): SiteApiResponse = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("token", token)
        }
        
        val response = postRequest("app_logout.php", json)
        parseResponse(response)
    }
    
    // Отправка SMS кода
    suspend fun sendSmsCode(phone: String): SiteApiResponse = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("phone", phone)
        }
        
        val response = postRequest("app_send_sms.php", json)
        parseResponse(response)
    }
    
    // Подтверждение SMS кода
    suspend fun verifySmsCode(
        phone: String,
        code: String
    ): SiteApiResponse = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("phone", phone)
            put("code", code)
        }
        
        val response = postRequest("app_verify_sms.php", json)
        parseResponse(response)
    }
    
    // Восстановление пароля
    suspend fun resetPassword(email: String): SiteApiResponse = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("email", email)
        }
        
        val response = postRequest("app_reset_password.php", json)
        parseResponse(response)
    }
    
    private fun postRequest(endpoint: String, json: JSONObject): String {
        val url = URL(BASE_URL + endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        
        OutputStreamWriter(conn.outputStream).use { writer ->
            writer.write(json.toString())
        }
        
        val responseCode = conn.responseCode
        val stream = if (responseCode in 200..299) {
            conn.inputStream
        } else {
            conn.errorStream ?: conn.inputStream
        }
        
        return BufferedReader(InputStreamReader(stream)).use { reader ->
            reader.readText()
        }
    }
    
    private fun parseResponse(jsonStr: String): SiteApiResponse {
        return try {
            val json = JSONObject(jsonStr)
            SiteApiResponse(
                status = json.optString("status", "error"),
                message = json.optString("message").takeIf { it.isNotEmpty() },
                userId = json.optLong("user_id", 0).takeIf { it > 0 },
                email = json.optString("email").takeIf { it.isNotEmpty() },
                name = json.optString("name").takeIf { it.isNotEmpty() },
                token = json.optString("token").takeIf { it.isNotEmpty() },
                user = json.optJSONObject("user")?.let { userJson ->
                    SiteUser(
                        id = userJson.optLong("id"),
                        email = userJson.optString("email"),
                        name = userJson.optString("name"),
                        phone = userJson.optString("phone"),
                        token = json.optString("token")
                    )
                }
            )
        } catch (e: Exception) {
            SiteApiResponse(
                status = "error",
                message = "Ошибка парсинга: ${e.message}"
            )
        }
    }
}
