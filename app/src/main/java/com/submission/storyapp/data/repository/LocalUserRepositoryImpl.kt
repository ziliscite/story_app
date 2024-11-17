package com.submission.storyapp.data.repository

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.submission.storyapp.data.local.preferences.UserPreferenceKey
import com.submission.storyapp.data.local.preferences.dataStore
import com.submission.storyapp.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalUserRepositoryImpl @Inject constructor(
    private val context: Application
) : LocalUserRepository {

    override suspend fun saveSession(token: String) {
        try {
            context.dataStore.edit {
                it[UserPreferenceKey.TOKEN_KEY] = token
            }
            Log.d("LocalUserRepositoryImpl", "Session saved successfully: $token")
        } catch (e: Exception) {
            Log.e("LocalUserRepositoryImpl", "Failed to save session: ${e.message}")
        }
    }

    override fun getSession(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            val token = preferences[UserPreferenceKey.TOKEN_KEY]
            Log.d("LocalUserRepositoryImpl", "Session retrieved: $token")
            token
        }.catch { e ->
            Log.e("LocalUserRepositoryImpl", "Error reading session: ${e.message}")
            emit(null)
        }
    }

    override suspend fun clearSession() {
        try {
            context.dataStore.edit { it.clear() }
            Log.d("LocalUserRepositoryImpl", "Session cleared successfully.")
        } catch (e: Exception) {
            Log.e("LocalUserRepositoryImpl", "Failed to clear session: ${e.message}")
        }
    }
}

