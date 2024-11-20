package com.submission.storyapp.presentation.core.home

data class HomeState(
    val token: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val refresh: Boolean = false
)
