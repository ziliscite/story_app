package com.submission.storyapp.domain.repository

import androidx.lifecycle.LiveData
import com.submission.storyapp.domain.models.Auth
import com.submission.storyapp.utils.ResponseWrapper

// Funny mistake to depend on the data layer instead of the domain layer
interface AuthRepository {
    fun signIn(
        email: String, password: String
    ): LiveData<ResponseWrapper<Auth>>

    fun signUp(
        name: String, email: String, password: String
    ): LiveData<ResponseWrapper<String>>
}
