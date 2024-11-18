package com.submission.storyapp.presentation.core.home

import com.submission.storyapp.domain.models.Story

data class HomeState(
    val token: String = "",
    val stories: List<Story> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

