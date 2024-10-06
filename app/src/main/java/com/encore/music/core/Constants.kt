package com.encore.music.core

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val GOOGLE_SERVER_CLIENT_ID =
        "228051723605-e86203qhe98ed82ad5q13u8cu06hsu9o.apps.googleusercontent.com"
    const val ONBOARDING_BG_IMAGE_FILE_NAME = "bg_onboarding.jpg"
}

object PreferencesKeys {
    val REMEMBER_LOGIN_DATA = booleanPreferencesKey("remember_login_data")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PASSWORD = stringPreferencesKey("user_password")
}

object Encore {
    const val API_BASE_URL = "http://192.168.23.129:8080/v1"
    const val ENDPOINT_GET_CATEGORIES = "categories"
    const val ENDPOINT_GET_CATEGORY_PLAYLISTS = "categories/{category_id}/playlists"
    const val ENDPOINT_GET_HOME_PLAYLISTS = "home-playlists"
    const val ENDPOINT_GET_PLAYLIST = "playlists/{playlist_id}"

    object Parameters {
        const val LIMIT = "limit"
        const val LOCALE = "locale"
        const val OFFSET = "offset"
    }
}

object Navigation {
    object Args {
        const val RESET_PASSWORD_EMAIL = "reset_password_email"
    }
}
