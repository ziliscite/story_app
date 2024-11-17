package com.submission.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.submission.storyapp.data.remote.responses.SignInResponse
import com.submission.storyapp.data.remote.responses.SignUpResponse
import com.submission.storyapp.data.remote.retrofit.AuthService
import com.submission.storyapp.domain.repository.AuthRepository
import com.submission.storyapp.utils.ResponseWrapper
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
): AuthRepository {
    override fun signIn(
        email: String, password: String
    ): LiveData<ResponseWrapper<SignInResponse>> = liveData {
        emit(ResponseWrapper.Loading)
        try {
            val response = authService.login(email, password)
            emit(ResponseWrapper.Success(response))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun signUp(
        name: String, email: String, password: String
    ): LiveData<ResponseWrapper<SignUpResponse>> = liveData {
        emit(ResponseWrapper.Loading)
        try {
            val response = authService.register(name, email, password)
            emit(ResponseWrapper.Success(response))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
