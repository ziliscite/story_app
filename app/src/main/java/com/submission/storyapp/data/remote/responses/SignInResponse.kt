package com.submission.storyapp.data.remote.responses

import com.google.gson.annotations.SerializedName
import com.submission.storyapp.domain.models.Auth

data class SignInResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("loginResult")
    val loginResult: Auth,

    @SerializedName("message")
    val message: String
)
