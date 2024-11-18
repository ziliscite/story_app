package com.submission.storyapp.presentation.core.create

import android.net.Uri

data class CreateState(
    val token: String? = null,
    val uri: Uri? = null,
    val previousUri: Uri = Uri.parse("android.resource://com.submission.storyapp/drawable/outline_image_24"),
    val description: String = "",
    val loading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
