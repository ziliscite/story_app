package com.submission.storyapp.presentation.core.create

import android.net.Uri

data class CreateState(
    val uri: Uri? = null,
    val previousUri: Uri = Uri.parse("android.resource://com.submission.storyapp/drawable/outline_image_24"),
    val description: String = "",
    val loading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val isChecked: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
)
