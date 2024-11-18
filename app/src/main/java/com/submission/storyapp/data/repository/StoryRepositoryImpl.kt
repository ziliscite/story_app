package com.submission.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.submission.storyapp.data.remote.responses.PostStoryResponse
import com.submission.storyapp.data.remote.retrofit.StoryService
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.utils.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyService: StoryService
): StoryRepository {
    override fun getStories(bearerToken: String): Flow<ResponseWrapper<List<Story>>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val response = storyService.getStories(bearerToken)

            if (response.error) {
                emit(ResponseWrapper.Error(response.message))
                return@flow
            }

            if (response.listStory.isEmpty()) {
                emit(ResponseWrapper.Error("No stories found"))
                return@flow
            }

            emit(ResponseWrapper.Success(response.listStory))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override suspend fun postStories(
        bearerToken: String, file: MultipartBody.Part, description: RequestBody
    ): Flow<ResponseWrapper<String>> = flow {
        emit(ResponseWrapper.Loading)
        try {
            val response = storyService.postStory(bearerToken, file, description)

            if (response.error) {
                emit(ResponseWrapper.Error(response.message))
                return@flow
            }

            emit(ResponseWrapper.Success(response.message))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
