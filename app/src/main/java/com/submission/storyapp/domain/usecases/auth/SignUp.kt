package com.submission.storyapp.domain.usecases.auth

import androidx.lifecycle.LiveData
import com.submission.storyapp.domain.repository.AuthRepository
import com.submission.storyapp.utils.ResponseWrapper

class SignUp(
    private val authRepository: AuthRepository
) {
    operator fun invoke(
        name: String, email: String, password: String
    ): LiveData<ResponseWrapper<String>> {
        return authRepository.signUp(name, email, password)
    }
}
