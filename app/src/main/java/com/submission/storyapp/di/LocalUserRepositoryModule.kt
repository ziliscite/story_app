package com.submission.storyapp.di

import com.submission.storyapp.data.repository.LocalUserRepositoryImpl
import com.submission.storyapp.domain.repository.LocalUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalUserRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLocalUserRepository(
        localUserRepositoryImpl: LocalUserRepositoryImpl
    ) : LocalUserRepository
}
