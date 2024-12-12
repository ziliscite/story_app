package com.submission.storyapp.presentation.core.maps

import androidx.lifecycle.ViewModel
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val storyUseCases: StoryUseCases
): ViewModel() {
    val stories = storyUseCases.getStoriesWithLocation()
}
