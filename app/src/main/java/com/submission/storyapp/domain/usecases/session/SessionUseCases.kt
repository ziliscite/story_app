package com.submission.storyapp.domain.usecases.session

data class SessionUseCases(
    val getSession: GetSession,
    val saveSession: SaveSession,
    val clearSession: ClearSession
)
