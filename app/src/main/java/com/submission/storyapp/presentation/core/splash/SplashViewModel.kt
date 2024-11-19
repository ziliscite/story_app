package com.submission.storyapp.presentation.core.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor (
    private val sessionUseCases: SessionUseCases
): ViewModel() {
    var state = MutableStateFlow(SplashState())
        private set

    init { getSession() }

    private fun getSession() {
        sessionUseCases.getSession().onEach {
            state.value = state.value.copy(authenticated = it != null)
        }.launchIn(viewModelScope)
    }
}
