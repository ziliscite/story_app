package com.submission.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.submission.storyapp.data.local.room.StoryDatabase
import com.submission.storyapp.data.paging.StoryRemoteMediator
import com.submission.storyapp.data.remote.retrofit.StoryService
import com.submission.storyapp.domain.models.Story
import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.utils.ResponseWrapper
import com.submission.storyapp.utils.UnauthorizedException
import com.submission.storyapp.utils.withToken
import com.submission.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyDatabase: StoryDatabase,
    private val storyService: StoryService,
    private val sessionUseCases: SessionUseCases
): StoryRepository {
    override fun getStories(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, storyService, sessionUseCases),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    override fun getStoriesWithLocation(): LiveData<ResponseWrapper<List<Story>>> = liveData {
        wrapEspressoIdlingResource {
            emit(ResponseWrapper.Loading)
            try {
                val response = withToken(sessionUseCases.getSession()) {
                    storyService.getStoriesWithLocation("Bearer $it")
                }

                if (response.error) {
                    throw Exception(response.message)
                }

                if (response.listStory.isEmpty()) {
                    throw Exception("No stories found")
                }

                emit(ResponseWrapper.Success(response.listStory))
            } catch (e: UnauthorizedException) {
                emit(ResponseWrapper.Error("Unauthorized: ${e.message}"))
            } catch (e: Exception) {
                emit(ResponseWrapper.Error(e.message.toString()))
            }
        }
    }

    override fun postStories(
        file: File?, description: String, lat: Double?, lon: Double?
    ): LiveData<ResponseWrapper<String>> = liveData {
        wrapEspressoIdlingResource {
            emit(ResponseWrapper.Loading)

            if (file == null) {
                emit(ResponseWrapper.Error("No media selected"))
                return@liveData
            }

            if (description.isEmpty()) {
                emit(ResponseWrapper.Error("No description provided"))
                return@liveData
            }

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

            try {
                val response = withToken(sessionUseCases.getSession()) {
                    storyService.postStory("Bearer $it", multipartBody, requestBody)
                }

                if (response.error) {
                    throw Exception(response.message)
                }

                emit(ResponseWrapper.Success(response.message))
            } catch (e: UnauthorizedException) {
                emit(ResponseWrapper.Error("Unauthorized: ${e.message}"))
            } catch (e: Exception) {
                emit(ResponseWrapper.Error(e.message.toString()))
            }
        }
    }
}
