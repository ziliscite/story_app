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
    storyUseCases: StoryUseCases,
    private val sessionUseCases: SessionUseCases
) : ViewModel() {
    val stories = storyUseCases.getStories().cachedIn(viewModelScope)

    fun logout() { CoroutineScope(Dispatchers.IO).launch {
        sessionUseCases.clearSession()
    }}

    var scroll = MutableStateFlow(false)
        private set

    fun setScrollToTop(shouldScroll: Boolean) {
        scroll.value = shouldScroll
    }
}
