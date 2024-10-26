package com.encore.music.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.encore.music.core.PreferencesKeys
import com.encore.music.core.utils.dataStore
import com.encore.music.domain.model.preferences.LoginPreferences
import com.encore.music.domain.repository.DatastoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DatastoreRepositoryImpl(
    private val context: Context,
) : DatastoreRepository {
    override val loginPreferences: Flow<LoginPreferences>
        get() =
            context.dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }.map { preferences ->
                    val userEmail: String = preferences[PreferencesKeys.USER_EMAIL].orEmpty()
                    val userPassword: String = preferences[PreferencesKeys.USER_PASSWORD].orEmpty()
                    val remember: Boolean =
                        preferences[PreferencesKeys.REMEMBER_LOGIN_DATA] ?: false

                    LoginPreferences(
                        email = userEmail,
                        password = userPassword,
                        remember = remember,
                    )
                }

    override suspend fun saveLoginPreferences(loginPreferences: LoginPreferences) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.USER_EMAIL] = loginPreferences.email
            settings[PreferencesKeys.USER_PASSWORD] = loginPreferences.password
            settings[PreferencesKeys.REMEMBER_LOGIN_DATA] = loginPreferences.remember
        }
    }

    override suspend fun clearLoginPreferences() {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.USER_EMAIL] = ""
            settings[PreferencesKeys.USER_PASSWORD] = ""
            settings[PreferencesKeys.REMEMBER_LOGIN_DATA] = false
        }
    }
}
