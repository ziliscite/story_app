package com.submission.storyapp.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UnauthorizedException(message: String) : Exception(message)

suspend fun <T> withToken(tokenFlow: Flow<String?>, block: suspend (String) -> T): T {
    return tokenFlow.firstOrNull()?.let {
        block(it)
    } ?: run {
        throw UnauthorizedException("No Access Token Found")
    }
}
