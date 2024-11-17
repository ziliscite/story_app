package com.submission.storyapp.domain.models

import com.google.gson.annotations.SerializedName

data class Auth(
    @SerializedName("name")
    val name: String,

    @SerializedName("token")
    val token: String,

    @SerializedName("userId")
    val userId: String
)
