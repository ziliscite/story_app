package com.submission.storyapp.domain.usecases.auth

import androidx.lifecycle.LiveData
import com.submission.storyapp.domain.models.Auth
import com.submission.storyapp.domain.repository.AuthRepository
import com.submission.storyapp.utils.ResponseWrapper

class SignIn(
    private val authRepository: AuthRepository
) {
    operator fun invoke(
        email: String, password: String
    ): LiveData<ResponseWrapper<Auth>> {
        return authRepository.signIn(email, password)
    }
}
