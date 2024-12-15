package com.submission.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.domain.usecases.story.GetStories
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import com.submission.storyapp.presentation.core.home.HomeViewModel
import com.submission.storyapp.presentation.core.home.StoryAdapter
import com.submission.storyapp.utils.DataDummy
import com.submission.storyapp.utils.MainDispatcherRule
import com.submission.storyapp.utils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var homeViewModel: HomeViewModel

    @Mock
    private lateinit var storyUseCases: StoryUseCases

    @Mock
    private lateinit var getStories: GetStories

    @Mock
    private lateinit var sessionUseCases: SessionUseCases

    @Test
    fun `verify story Should Not Null and Return Data`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStory = DataDummy.generateDummyStoriesResponse()
        val data = PagedTestDataSources.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<Story>>()
        story.value = data

        // Mock use cases, because I did not use repository in the view model
        `when`(storyUseCases.getStories).thenReturn(getStories)
        `when`(getStories.invoke()).thenReturn(story)

        // Initialize view model
        homeViewModel = HomeViewModel(storyUseCases, sessionUseCases)

        val actualData = homeViewModel.stories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        differ.submitData(actualData)

        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }

    @Test
    fun `when story is Empty Should Return No Data`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val data = PagedTestDataSources.snapshot(emptyList())
        val story = MutableLiveData<PagingData<Story>>()
        story.value = data

        `when`(storyUseCases.getStories).thenReturn(getStories)
        `when`(getStories.invoke()).thenReturn(story)

        homeViewModel = HomeViewModel(storyUseCases, sessionUseCases)

        val actualData = homeViewModel.stories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )

        differ.submitData(actualData)

        Assert.assertEquals(0, differ.snapshot().size)
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }

    class PagedTestDataSources private constructor() :
        PagingSource<Int, LiveData<List<Story>>>() {
        companion object {
            fun snapshot(items: List<Story>): PagingData<Story> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }
}