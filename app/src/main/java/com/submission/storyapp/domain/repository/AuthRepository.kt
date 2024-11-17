package com.submission.storyapp.domain.repository

import androidx.lifecycle.LiveData
import com.submission.storyapp.data.remote.responses.SignInResponse
import com.submission.storyapp.data.remote.responses.SignUpResponse
import com.submission.storyapp.utils.ResponseWrapper

interface AuthRepository {
    fun signIn(
        email: String, password: String
    ): LiveData<ResponseWrapper<SignInResponse>>

    fun signUp(
        name: String, email: String, password: String
    ): LiveData<ResponseWrapper<SignUpResponse>>
}
