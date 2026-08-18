package com.mitsudrive.core.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mitsudrive.core.auth.model.UserSession
import com.mitsudrive.core.auth.storage.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_session"
)

class SessionManager(
    private val context: Context,
    private val tokenStorage: TokenStorage
) {
    
    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val AVATAR_URL = stringPreferencesKey("avatar_url")
        private val LAST_LOGIN = longPreferencesKey("last_login")
    }
    
    val session: Flow<UserSession?> = context.sessionDataStore.data.map { prefs ->
        val userId = prefs[USER_ID]
        val username = prefs[USERNAME]
        
        if (userId != null && username != null) {
            UserSession(
                userId = userId,
                username = username,
                avatarUrl = prefs[AVATAR_URL],
                isAuthenticated = true,
                lastLoginAt = prefs[LAST_LOGIN] ?: System.currentTimeMillis()
            )
        } else {
            null
        }
    }
    
    suspend fun saveSession(session: UserSession) {
        context.sessionDataStore.edit { prefs ->
            prefs[USER_ID] = session.userId
            prefs[USERNAME] = session.username
            prefs[AVATAR_URL] = session.avatarUrl ?: ""
            prefs[LAST_LOGIN] = session.lastLoginAt
        }
    }
    
    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs ->
            prefs.remove(USER_ID)
            prefs.remove(USERNAME)
            prefs.remove(AVATAR_URL)
            prefs.remove(LAST_LOGIN)
        }
        tokenStorage.clearTokens()
    }
    
    suspend fun isAuthenticated(): Boolean {
        val tokens = tokenStorage.tokens
        var hasTokens = false
        tokens.collect { authTokens ->
            hasTokens = authTokens != null && 
                authTokens.accessToken.isNotEmpty() &&
                authTokens.expiresAt > System.currentTimeMillis()
        }
        return hasTokens
    }
}
