package com.submission.storyapp.data.repository

import android.app.Application
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
        try { context.dataStore.edit {
            it[UserPreferenceKey.TOKEN_KEY] = token
        }} catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getSession(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[UserPreferenceKey.TOKEN_KEY]
        }.catch { e ->
            e.printStackTrace()
            emit(null)
        }
    }

    override suspend fun clearSession() {
        try { context.dataStore.edit {
            it.clear()
        }} catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

