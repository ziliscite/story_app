package com.submission.storyapp.domain.usecases.story

import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.utils.ResponseWrapper
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostStory(
    private val storyRepository: StoryRepository
) {
    suspend operator fun invoke(
        bearerToken: String,
        file: MultipartBody.Part,
        description: RequestBody,
    ): Flow<ResponseWrapper<String>> {
        return storyRepository.postStories(bearerToken, file, description)
    }
}
