package com.submission.storyapp.presentation.core.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.storyapp.domain.usecases.auth.AuthUseCases
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyUseCases: StoryUseCases,
    private val sessionUseCases: SessionUseCases
) : ViewModel() {
    var state = MutableStateFlow(HomeState())
        private set

    init { getToken() }

    private fun getToken() { viewModelScope.launch {
        state.value = state.value.copy(token = sessionUseCases.getSession().firstOrNull() ?: "")
        collectStories()
    }}

    private fun collectStories() { storyUseCases.getStories("Bearer ${state.value.token}")
        .onEach { response ->
            when (response) {
                is ResponseWrapper.Success -> {
                    state.value = state.value.copy(
                        stories = response.data,
                        loading = false,
                        error = null
                    )
                }
                is ResponseWrapper.Error -> {
                    state.value = state.value.copy(
                        loading = false,
                        error = response.error
                    )
                }
                ResponseWrapper.Loading -> {
                    state.value = state.value.copy(
                        loading = true,
                        error = null
                    )
                }
            }
        }
        .catch { exception ->
            state.value = state.value.copy(
                loading = false,
                error = exception.localizedMessage ?: "An unexpected error occurred"
            )
        }
        .launchIn(viewModelScope)
    }
}