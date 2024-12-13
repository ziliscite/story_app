package com.submission.storyapp.presentation.core.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storyUseCases: StoryUseCases,
    private val sessionUseCases: SessionUseCases
) : ViewModel() {
    var scroll = MutableStateFlow(false)
        private set

    fun setScrollToTop(shouldScroll: Boolean) {
        scroll.value = shouldScroll
    }

    val stories = storyUseCases.getStories().cachedIn(viewModelScope)

    // Use CoroutineScope to make sure it doesn't get interrupted
    fun logout() { CoroutineScope(Dispatchers.IO).launch {
        sessionUseCases.clearSession()
    }}
}
