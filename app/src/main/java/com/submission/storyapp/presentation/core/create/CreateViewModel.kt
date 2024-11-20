package com.submission.storyapp.presentation.core.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor (
    private val storyUseCases: StoryUseCases
) : ViewModel() {
    var state = MutableStateFlow(CreateState())
        private set

    fun postStory(file: File): LiveData<ResponseWrapper<String>> {
        return storyUseCases.postStory(file, state.value.description)
    }

    fun onSuccess(message: String) {
        state.value = state.value.copy(
            message = message,
            loading = false,
            error = null
        )
    }

    fun onError(error: String) {
        state.value = state.value.copy(
            error = error,
            loading = false
        )
    }

    fun onLoading() {
        state.value = state.value.copy(
            error = null,
            loading = true
        )
    }

    fun updateUri(uri: Uri) {
        state.value = state.value.copy(uri = uri)
    }

    fun updateDescription(description: String) {
        state.value = state.value.copy(description = description)
    }

    fun clearError() {
        state.value = state.value.copy(error = null)
    }

    fun updatePreviousUri() {
        state.value.uri?.let { state.value = state.value.copy(previousUri = it) }
    }

    fun revertUri() {
        state.value = state.value.copy(uri = state.value.previousUri)
    }
}
