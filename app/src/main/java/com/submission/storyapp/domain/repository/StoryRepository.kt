package com.submission.storyapp.domain.repository

import androidx.lifecycle.LiveData
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.utils.ResponseWrapper
import java.io.File

interface StoryRepository {
    fun getStories(): LiveData<ResponseWrapper<List<Story>>>

    fun postStories(
        file: File?,
        description: String,
    ): LiveData<ResponseWrapper<String>>
}
