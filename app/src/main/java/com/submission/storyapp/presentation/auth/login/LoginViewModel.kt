package com.submission.storyapp.presentation.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.submission.storyapp.data.remote.responses.SignInResponse
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
    private var state = MutableStateFlow(LoginState())

    fun updateEmail(email: String) {
        state.value = state.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        state.value = state.value.copy(password = password)
    }

    fun isValid(): Boolean { state.value.run {
        return email.isNotBlank() && password.isNotBlank()
    }}

    fun signIn(): LiveData<ResponseWrapper<SignInResponse>> { state.value.run {
        return authUseCases.signIn(email, password)
    }}

    // Previous attempt had race condition, perhaps due to fragment being destroyed by navigating before saving session
    fun onSignedIn(auth: Auth) { CoroutineScope(Dispatchers.IO).launch {
        sessionUseCases.saveSession(auth.token)
    }}
}
