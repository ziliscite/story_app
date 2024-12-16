package com.submission.storyapp.di

import android.app.Application
import com.submission.storyapp.data.local.room.StoryDatabase
import com.submission.storyapp.data.remote.retrofit.AuthService
import com.submission.storyapp.data.remote.retrofit.StoryService
import com.submission.storyapp.domain.repository.AuthRepository
import com.submission.storyapp.domain.repository.LocalUserRepository
import com.submission.storyapp.domain.repository.StoryRepository
import com.submission.storyapp.domain.usecases.auth.AuthUseCases
import com.submission.storyapp.domain.usecases.auth.SignIn
import com.submission.storyapp.domain.usecases.auth.SignUp
import com.submission.storyapp.domain.usecases.session.ClearSession
import com.submission.storyapp.domain.usecases.session.GetSession
import com.submission.storyapp.domain.usecases.session.SaveSession
import com.submission.storyapp.domain.usecases.session.SessionUseCases
import com.submission.storyapp.domain.usecases.story.GetStories
import com.submission.storyapp.domain.usecases.story.GetStoriesWithLocation
import com.submission.storyapp.domain.usecases.story.PostStory
import com.submission.storyapp.domain.usecases.story.StoryUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBaseUrl() = "https://story-api.dicoding.dev/v1/"

    @Provides
    @Singleton
    fun provideSessionUseCases(
        localUserRepository: LocalUserRepository
    ): SessionUseCases {
        return SessionUseCases(
            getSession = GetSession(localUserRepository),
            saveSession = SaveSession(localUserRepository),
            clearSession = ClearSession(localUserRepository)
        )
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        // Cannot add auth interceptor due to dependency injection being done at compile time.

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        retrofit: Retrofit
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthUseCases(
        authRepository: AuthRepository
    ): AuthUseCases {
        return AuthUseCases(
            signIn = SignIn(authRepository),
            signUp = SignUp(authRepository)
        )
    }

    @Provides
    @Singleton
    fun provideStoryService(
        retrofit: Retrofit
    ): StoryService {
        return retrofit.create(StoryService::class.java)
    }

    @Provides
    @Singleton
    fun provideStoryUseCases(
        storyRepository: StoryRepository
    ): StoryUseCases {
        return StoryUseCases(
            getStories = GetStories(storyRepository),
            getStoriesWithLocation = GetStoriesWithLocation(storyRepository),
            postStory = PostStory(storyRepository)
        )
    }

    @Provides
    @Singleton
    fun provideStoryDatabase(
        context: Application
    ): StoryDatabase {
        return StoryDatabase.getDatabase(context)
    }
}
