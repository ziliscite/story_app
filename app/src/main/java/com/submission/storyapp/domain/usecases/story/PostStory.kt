package com.submission.storyapp.domain.usecases.story

import androidx.lifecycle.LiveData
import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.utils.ResponseWrapper
import java.io.File

class PostStory(
    private val storyRepository: StoryRepository
) {
    operator fun invoke(
        bearerToken: String,
        file: File?,
        description: String,
    ): LiveData<ResponseWrapper<String>> {
        return storyRepository.postStories(bearerToken, file, description)
    }
}
