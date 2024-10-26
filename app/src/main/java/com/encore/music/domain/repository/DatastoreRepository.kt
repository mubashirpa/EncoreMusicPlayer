package com.encore.music.domain.repository

import com.encore.music.domain.model.preferences.LoginPreferences
import kotlinx.coroutines.flow.Flow

interface DatastoreRepository {
    val loginPreferences: Flow<LoginPreferences>

    suspend fun saveLoginPreferences(loginPreferences: LoginPreferences)

    suspend fun clearLoginPreferences()
}
