package com.submission.storyapp.domain.repository

import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.utils.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

interface StoryRepository {
    fun getStories(bearerToken: String): Flow<ResponseWrapper<List<Story>>>

    fun postStories(
        bearerToken: String,
        file: File,
        description: String,
    ): Flow<ResponseWrapper<String>>
}
