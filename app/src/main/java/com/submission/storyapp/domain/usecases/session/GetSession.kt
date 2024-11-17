package com.submission.storyapp.domain.usecases.session

import com.submission.storyapp.domain.repository.LocalUserRepository
import kotlinx.coroutines.flow.Flow

class GetSession(private val localUserRepository: LocalUserRepository) {
    operator fun invoke(): Flow<String?> {
        return localUserRepository.getSession()
    }
}
