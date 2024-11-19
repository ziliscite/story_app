package com.submission.storyapp.presentation.core.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor (
    private val storyUseCases: StoryUseCases,
    private val sessionUseCases: SessionUseCases
) : ViewModel() {
    var state = MutableStateFlow(CreateState())
        private set

    init { getToken() }

    private fun getToken() { viewModelScope.launch {
        state.value = state.value.copy(token = sessionUseCases.getSession().firstOrNull() ?: "")
    }}

    fun postStory(file: File) {
        storyUseCases.postStory("Bearer ${state.value.token}", file, state.value.description)
        .onEach { response ->
            when (response) {
                is ResponseWrapper.Success -> {
                    state.value = state.value.copy(
                        message = response.data,
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
        }.launchIn(viewModelScope)
    }

    fun updateDescription(description: String) {
        state.value = state.value.copy(description = description)
    }

    fun updateUri(uri: Uri) {
        state.value = state.value.copy(uri = uri)
    }

    fun updatePreviousUri() {
        state.value.uri?.let { state.value = state.value.copy(previousUri = it) }
    }

    fun revertUri() {
        state.value = state.value.copy(uri = state.value.previousUri)
    }
}
