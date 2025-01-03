package com.submission.storyapp.di

import com.submission.storyapp.domain.usecases.story.StoryUseCases
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun storyUseCases(): StoryUseCases
}
