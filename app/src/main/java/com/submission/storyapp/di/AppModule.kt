package com.submission.storyapp.di

import com.submission.storyapp.data.remote.retrofit.AuthService
import com.submission.storyapp.domain.repository.AuthRepository
import com.submission.storyapp.domain.repository.LocalUserRepository
import com.submission.storyapp.domain.usecases.auth.AuthUseCases
import com.submission.storyapp.domain.usecases.auth.SignIn
import com.submission.storyapp.domain.usecases.auth.SignUp
import com.submission.storyapp.domain.usecases.session.ClearSession
import com.submission.storyapp.domain.usecases.session.GetSession
import com.submission.storyapp.domain.usecases.session.SaveSession
import com.submission.storyapp.domain.usecases.session.SessionUseCases
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
    fun provideAuthService(): AuthService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

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
}
