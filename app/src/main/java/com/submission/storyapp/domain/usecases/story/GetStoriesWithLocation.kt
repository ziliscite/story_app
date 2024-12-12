package com.submission.storyapp.domain.usecases.story

import androidx.lifecycle.LiveData
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.utils.ResponseWrapper

class GetStoriesWithLocation(
    private val storyRepository: StoryRepository
) {
    operator fun invoke(): LiveData<ResponseWrapper<List<Story>>> {
        return storyRepository.getStoriesWithLocation()
    }
}
