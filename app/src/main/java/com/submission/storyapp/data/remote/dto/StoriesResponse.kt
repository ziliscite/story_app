package com.submission.storyapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.submission.storyapp.domain.models.Story

data class StoriesResponse(
    @SerializedName("error")
    val error: Boolean,

    @SerializedName("listStory")
    val listStory: List<Story>,

    @SerializedName("message")
    val message: String
)
