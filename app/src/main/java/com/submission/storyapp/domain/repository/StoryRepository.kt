package com.submission.storyapp.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.utils.ResponseWrapper
import java.io.File

interface StoryRepository {
    fun getStories(): LiveData<PagingData<Story>>
    fun getStoriesWithLocation(): LiveData<ResponseWrapper<List<Story>>>
    fun postStories(file: File?, description: String, lat: Double?, lon: Double?): LiveData<ResponseWrapper<String>>
}
