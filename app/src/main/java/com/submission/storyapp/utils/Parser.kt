package com.submission.storyapp.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.Locale

fun String.parseDate(): String? {
    return try {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormatter.parse(this) ?: return null

        val outputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        outputFormatter.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun CharSequence.parseEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

// Regex to make sure the password is at least 8 characters long,
// contains at least one number, one lowercase letter, and one uppercase letter.
// However, this will not be used.
fun String.parsePassword(): Boolean {
    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
    return Regex(passwordPattern).matches(this)
}
