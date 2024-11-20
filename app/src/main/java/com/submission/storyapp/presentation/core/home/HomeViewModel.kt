package com.submission.storyapp.presentation.core.home

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    // Use CoroutineScope to make sure it doesn't get interrupted
    fun logout() { CoroutineScope(Dispatchers.IO).launch {
        sessionUseCases.clearSession()
    }}

    private fun collectStories() {
        storyObserver = Observer { response ->
            when (response) {
                is ResponseWrapper.Success -> {
                    onSuccess(response.data)
                }
                is ResponseWrapper.Error -> {
                    onError(response.error)
                }
                ResponseWrapper.Loading -> {
                    onLoading()
                }
            }
        }
        storyUseCases.getStories("Bearer ${state.value.token}").observeForever(storyObserver)
    }

    private fun onSuccess(stories: List<Story>) {
        state.value = state.value.copy(
            stories = stories,
            loading = false,
            error = null,
            refresh = false
        )
    }

    private fun onError(error: String) {
        state.value = state.value.copy(
            error = error,
            loading = false,
            refresh = false
        )
    }

    private fun onLoading() {
        state.value = state.value.copy(
            error = null,
            loading = true
        )
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
