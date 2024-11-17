package com.submission.storyapp.domain.usecases.session

import com.submission.storyapp.domain.repository.LocalUserRepository

class SaveSession(private val localUserRepository: LocalUserRepository) {
    suspend operator fun invoke(token: String) {
        localUserRepository.saveSession(token)
    }
}
