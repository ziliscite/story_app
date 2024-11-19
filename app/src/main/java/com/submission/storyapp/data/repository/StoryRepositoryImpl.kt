package com.submission.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.submission.storyapp.data.remote.retrofit.StoryService
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.utils.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyService: StoryService
): StoryRepository {
    override fun getStories(bearerToken: String): LiveData<ResponseWrapper<List<Story>>> = liveData {
        emit(ResponseWrapper.Loading)
        try {
            val response = storyService.getStories(bearerToken)

            if (response.error) {
                emit(ResponseWrapper.Error(response.message))
                return@liveData
            }

            if (response.listStory.isEmpty()) {
                emit(ResponseWrapper.Error("No stories found"))
                return@liveData
            }

            emit(ResponseWrapper.Success(response.listStory))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }

    override fun postStories(
        bearerToken: String, file: File?, description: String
    ): LiveData<ResponseWrapper<String>> = liveData {
        emit(ResponseWrapper.Loading)

        if (file == null) {
            emit(ResponseWrapper.Error("No media selected"))
            return@liveData
        }

        if (description.isEmpty()) {
            emit(ResponseWrapper.Error("No description provided"))
            return@liveData
        }

        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())

        val multipartBody = MultipartBody.Part.createFormData(
            "photo", file.name, requestImageFile
        )

        try {
            val response = storyService.postStory(bearerToken, multipartBody, requestBody)

            if (response.error) {
                emit(ResponseWrapper.Error(response.message))
                return@liveData
            }

            emit(ResponseWrapper.Success(response.message))
        } catch (e: Exception) {
            emit(ResponseWrapper.Error(e.message.toString()))
        }
    }
}
