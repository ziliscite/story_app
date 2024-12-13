package com.submission.storyapp.domain.usecases.story

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.utils.ResponseWrapper
import kotlinx.coroutines.flow.Flow

class GetStories(
    private val storyRepository: StoryRepository
) {
    operator fun invoke(): Flow<PagingData<Story>> {
        return storyRepository.getStories()
    }
}
