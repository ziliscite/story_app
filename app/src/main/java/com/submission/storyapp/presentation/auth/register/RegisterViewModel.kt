package com.submission.storyapp.presentation.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.submission.storyapp.domain.usecases.auth.AuthUseCases
import com.submission.storyapp.utils.ResponseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {
    private var state = MutableStateFlow(RegisterState())

    fun updateName(name: String) {
        state.value = state.value.copy(name = name)
    }

    fun updateEmail(email: String) {
        state.value = state.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        state.value = state.value.copy(password = password)
    }

    fun signUp(): LiveData<ResponseWrapper<String>> { state.value.run {
        return authUseCases.signUp(name, email, password)
    }}
}
