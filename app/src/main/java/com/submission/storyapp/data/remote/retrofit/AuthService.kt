package com.submission.storyapp.data.remote.retrofit

import com.submission.storyapp.data.remote.dto.SignInResponse
import com.submission.storyapp.data.remote.dto.SignUpResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

// Doesn't require token
interface AuthService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): SignInResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignUpResponse
}
