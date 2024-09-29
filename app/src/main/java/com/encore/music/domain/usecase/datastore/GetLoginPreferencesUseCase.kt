package com.encore.music.domain.usecase.datastore

import com.encore.music.domain.model.preferences.LoginPreferences
import com.encore.music.domain.repository.DatastoreRepository
import kotlinx.coroutines.flow.Flow

class GetLoginPreferencesUseCase(
    private val repository: DatastoreRepository,
) {
    operator fun invoke(): Flow<LoginPreferences> = repository.loginPreferences
}
