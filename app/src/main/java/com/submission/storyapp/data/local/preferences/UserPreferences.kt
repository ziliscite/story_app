package com.submission.storyapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(UserPreferenceKey.TOKEN_KEY.name)

internal object UserPreferenceKey {
    val TOKEN_KEY = stringPreferencesKey("token")
}
