package com.submission.storyapp.presentation.core.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyUseCases: StoryUseCases,
    private val sessionUseCases: SessionUseCases
) : ViewModel() {
    var state = MutableStateFlow(HomeState())
        private set

    private lateinit var storyObserver: Observer<ResponseWrapper<List<Story>>>

    init { getToken() }

    private fun getToken() { viewModelScope.launch {
        state.value = state.value.copy(token = sessionUseCases.getSession().firstOrNull() ?: "")
        collectStories()
    }}

    private fun collectStories() {
        storyObserver = Observer { response ->
            when (response) {
                is ResponseWrapper.Success -> {
                    state.value = state.value.copy(
                        stories = response.data,
                        loading = false,
                        error = null,
                        refresh = false
                    )
                }
                is ResponseWrapper.Error -> {
                    state.value = state.value.copy(
                        loading = false,
                        error = response.error,
                        refresh = false
                    )
                }
                ResponseWrapper.Loading -> {
                    state.value = state.value.copy(
                        loading = true,
                        error = null,
                        // don't refresh
                    )
                }
            }
        }
        storyUseCases.getStories("Bearer ${state.value.token}").observeForever(storyObserver)
    }

    fun refresh() { viewModelScope.launch {
        state.value = state.value.copy(refresh = true)
        collectStories()
    }}

    // Will be removed when fragment is destroyed
    fun removeObserver() {
        storyUseCases.getStories("Bearer ${state.value.token}").removeObserver(storyObserver)
    }
}
