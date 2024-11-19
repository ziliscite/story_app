package com.submission.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.submission.storyapp.data.remote.retrofit.AuthService
import com.submission.storyapp.domain.models.Auth
import com.submission.storyapp.domain.repository.AuthRepository
import com.submission.storyapp.utils.ResponseWrapper
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
): AuthRepository {
    override fun signIn(
        email: String, password: String
    ): LiveData<ResponseWrapper<Auth>> = liveData {
        emit(ResponseWrapper.Loading)
        try {
            val response = authService.login(email, password)

            if (response.error) {
                emit(ResponseWrapper.Error(response.message))
                return@liveData
            }

            emit(ResponseWrapper.Success(response.loginResult))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun signUp(
        name: String, email: String, password: String
    ): LiveData<ResponseWrapper<String>> = liveData {
        emit(ResponseWrapper.Loading)
        try {
            val response = authService.register(name, email, password)

            if (response.error) {
                emit(ResponseWrapper.Error(response.message))
                return@liveData
            }

            emit(ResponseWrapper.Success(response.message))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
