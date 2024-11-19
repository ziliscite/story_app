package com.submission.storyapp.di

import com.submission.storyapp.data.repository.AuthRepositoryImpl
import com.submission.storyapp.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// These modules are 100% necessary for Hilt. Not sure why it says unused.
@Suppress("unused", "unused", "unused", "unused", "unused", "unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ) : AuthRepository
}
