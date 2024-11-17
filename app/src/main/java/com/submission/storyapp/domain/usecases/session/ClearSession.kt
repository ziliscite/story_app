package com.submission.storyapp.domain.usecases.session

import com.submission.storyapp.domain.repository.LocalUserRepository

class ClearSession(private val localUserRepository: LocalUserRepository) {
    suspend operator fun invoke() {
        return localUserRepository.clearSession()
    }
}
