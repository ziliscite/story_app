package com.submission.storyapp.domain.usecases.story

import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.utils.ResponseWrapper
import kotlinx.coroutines.flow.Flow

class GetStories(
    private val storyRepository: StoryRepository
) {
    operator fun invoke(bearerToken: String): Flow<ResponseWrapper<List<Story>>> {
        return storyRepository.getStories(bearerToken)
    }
}
