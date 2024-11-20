package com.submission.storyapp.presentation.core.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    init {
        getStories()
    }

    private fun getStories() = viewModelScope.launch {
        _stories.postValue(ResponseWrapper.Loading)
        try {
            val response = storyUseCases.getStories().value
            _stories.postValue(response ?: ResponseWrapper.Error("Empty response"))
        } catch (e: Exception) {
            _stories.postValue(ResponseWrapper.Error(e.message ?: "Failed to fetch stories"))
        }
    }


    private val _stories = MutableLiveData<ResponseWrapper<List<Story>>>()
    val stories: LiveData<ResponseWrapper<List<Story>>> = _stories

    // Use CoroutineScope to make sure it doesn't get interrupted
    fun logout() { CoroutineScope(Dispatchers.IO).launch {
        sessionUseCases.clearSession()
    }}

    fun onSuccess() {
        state.value = state.value.copy(
            loading = false,
            error = null,
            refresh = false
        )
    }

    fun onError(error: String) {
        state.value = state.value.copy(
            error = error,
            loading = false,
            refresh = false
        )
    }

    fun onLoading() {
        state.value = state.value.copy(
            error = null,
            loading = true
        )
    }

    fun refresh() {
        state.value = state.value.copy(refresh = true)
        getStories()
    }
}
