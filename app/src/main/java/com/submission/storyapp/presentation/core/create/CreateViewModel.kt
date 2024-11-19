package com.submission.storyapp.presentation.core.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor (
    private val storyUseCases: StoryUseCases,
    private val sessionUseCases: SessionUseCases
) : ViewModel() {
    private var _state = MutableLiveData<CreateState>()
    val state: LiveData<CreateState> get() = _state

    init { getToken() }

    private fun getToken() { viewModelScope.launch {
        _state.value = _state.value?.copy(token = sessionUseCases.getSession().firstOrNull() ?: "")
    }}

    fun postStory(file: File) { _state.value?.let {
        storyUseCases.postStory("Bearer ${it.token}", file, it.description).map { response ->
            when(response) {
                is ResponseWrapper.Success -> {
                    _state.value = it.copy(
                        message = response.data,
                        loading = false,
                        error = null
                    )
                }

                is ResponseWrapper.Error -> {
                    _state.value = it.copy(
                        loading = false,
                        error = response.error
                    )
                }

                ResponseWrapper.Loading -> {
                    _state.value = it.copy(
                        loading = true,
                        error = null
                    )
                }
            }
        }
    }}

    fun updateDescription(description: String) {
        _state.value = _state.value?.copy(description = description)
    }

    fun updateUri(uri: Uri) {
        _state.value = _state.value?.copy(uri = uri)
    }

    fun updatePreviousUri() {
        _state.value?.uri?.let { _state.value = _state.value?.copy(previousUri = it) }
    }

    fun revertUri() {
        _state.value = _state.value?.copy(uri = _state.value?.previousUri)
    }
}
