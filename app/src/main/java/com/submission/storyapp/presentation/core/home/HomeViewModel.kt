package com.submission.storyapp.presentation.core.home

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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

    init {
        collectStories()
    }

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
        storyUseCases.getStories().observeForever(storyObserver)
    }

    // Use CoroutineScope to make sure it doesn't get interrupted
    fun logout() { CoroutineScope(Dispatchers.IO).launch {
        sessionUseCases.clearSession()
    }}

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

    // Will be removed when fragment is destroyed
    fun removeObserver() {
        storyUseCases.getStories().removeObserver(storyObserver)
    }

    fun retry() {
        state.value = state.value.copy(refresh = true)
        collectStories()
    }

    fun refresh() {
        collectStories()
    }
}
