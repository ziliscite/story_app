package com.submission.storyapp.presentation.core.home

import com.submission.storyapp.domain.models.Story

data class HomeState(
    val stories: List<Story> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val refresh: Boolean = false
)
