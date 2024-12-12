package com.submission.storyapp.data.remote.retrofit

import com.submission.storyapp.data.remote.responses.PostStoryResponse
import com.submission.storyapp.data.remote.responses.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface StoryService {
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") bearerToken: String,
    ): StoriesResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") bearerToken: String,
        @Query("location") location : Int = 1,
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Header("Authorization") bearerToken: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): PostStoryResponse
}
