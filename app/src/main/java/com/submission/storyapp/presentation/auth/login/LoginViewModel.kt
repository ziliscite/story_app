package com.submission.storyapp.presentation.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.submission.storyapp.domain.models.Auth
import com.submission.storyapp.domain.usecases.auth.AuthUseCases
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionUseCases: SessionUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {
    // Trying jetpack compose style
    private var state = MutableStateFlow(LoginState())

    fun updateEmail(email: String) {
        state.value = state.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        state.value = state.value.copy(password = password)
    }

    fun signIn(): LiveData<ResponseWrapper<Auth>> { state.value.run {
        return authUseCases.signIn(email, password)
    }}

    // Using viewModelScope result in race condition, perhaps due to fragment being destroyed by navigating
    fun onSignedIn(auth: Auth) { CoroutineScope(Dispatchers.IO).launch {
        sessionUseCases.saveSession(auth.token)
    }}
}
