package com.submission.storyapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface LocalUserRepository {
    suspend fun saveSession(token: String)
    fun getSession(): Flow<String?>
    suspend fun clearSession()
}
