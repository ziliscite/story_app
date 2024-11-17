package com.submission.storyapp.data.remote.responses

import com.google.gson.annotations.SerializedName

data class PostStoryResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String
)
