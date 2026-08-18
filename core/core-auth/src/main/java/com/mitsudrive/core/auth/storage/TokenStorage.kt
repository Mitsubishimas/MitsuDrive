package com.mitsudrive.core.auth.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mitsudrive.core.auth.model.AuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_tokens"
)

class TokenStorage(private val context: Context) {
    
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val EXPIRES_AT = longPreferencesKey("expires_at")
    }
    
    val tokens: Flow<AuthTokens?> = context.authDataStore.data.map { prefs ->
        val accessToken = prefs[ACCESS_TOKEN]
        val refreshToken = prefs[REFRESH_TOKEN]
        val expiresAt = prefs[EXPIRES_AT]
        
        if (accessToken != null && refreshToken != null && expiresAt != null) {
            AuthTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresAt = expiresAt
            )
        } else {
            null
        }
    }
    
    suspend fun saveTokens(tokens: AuthTokens) {
        context.authDataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = tokens.accessToken
            prefs[REFRESH_TOKEN] = tokens.refreshToken
            prefs[EXPIRES_AT] = tokens.expiresAt
        }
    }
    
    suspend fun clearTokens() {
        context.authDataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
            prefs.remove(EXPIRES_AT)
        }
    }
    
    suspend fun getAccessToken(): String? {
        return context.authDataStore.data
            .map { prefs -> prefs[ACCESS_TOKEN] }
            .first()
    }
    
    suspend fun getRefreshToken(): String? {
        return context.authDataStore.data
            .map { prefs -> prefs[REFRESH_TOKEN] }
            .first()
    }
}
