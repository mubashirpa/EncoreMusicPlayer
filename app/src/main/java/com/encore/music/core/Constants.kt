package com.encore.music.core

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val GOOGLE_SERVER_CLIENT_ID =
        "228051723605-e86203qhe98ed82ad5q13u8cu06hsu9o.apps.googleusercontent.com"
}

object PreferencesKeys {
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PASSWORD = stringPreferencesKey("user_password")
    val REMEMBER_LOGIN_DATA = booleanPreferencesKey("remember_login_data")
}
