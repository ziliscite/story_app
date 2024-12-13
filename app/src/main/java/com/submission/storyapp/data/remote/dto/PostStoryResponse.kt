package com.submission.storyapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PostStoryResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String
)
