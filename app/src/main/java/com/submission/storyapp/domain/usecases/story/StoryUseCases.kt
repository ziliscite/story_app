package com.submission.storyapp.domain.usecases.story

data class StoryUseCases(
    val getStories: GetStories,
    val getStoriesWithLocation: GetStoriesWithLocation,
    val postStory: PostStory
)
