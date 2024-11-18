package com.submission.storyapp.di

import com.submission.storyapp.data.repository.StoryRepositoryImpl
import com.submission.storyapp.domain.repository.StoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StoryRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStoryRepository(
        storyRepositoryImpl: StoryRepositoryImpl
    ) : StoryRepository
}
