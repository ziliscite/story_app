package com.submission.storyapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (
    private val sessionUseCases: SessionUseCases
): ViewModel() {
    var splashCondition = MutableStateFlow(true)
        private set

    var isAuthenticated = MutableStateFlow(false)
        private set

    init {
        sessionUseCases.getSession().onEach { token ->
            isAuthenticated.value = !token.isNullOrEmpty()

            delay(1000)

            splashCondition.value = false
        }.launchIn(viewModelScope)
    }
}
