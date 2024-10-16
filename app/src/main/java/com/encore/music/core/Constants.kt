package com.encore.music.core

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constants {
    const val GOOGLE_SERVER_CLIENT_ID =
        "228051723605-fpsj95h4o48fa9l0soudi9r0fdqb9896.apps.googleusercontent.com"
    const val ONBOARDING_BG_IMAGE_FILE_NAME = "bg_onboarding.jpg"
}

object PreferencesKeys {
    val REMEMBER_LOGIN_DATA = booleanPreferencesKey("remember_login_data")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PASSWORD = stringPreferencesKey("user_password")
}

object Encore {
    const val API_BASE_URL = "http://192.168.220.36:8080/v1"
    const val ENDPOINT_GET_ARTIST_TOP_TRACKS = "artists/{artist_id}/top-tracks"
    const val ENDPOINT_GET_CATEGORIES = "categories"
    const val ENDPOINT_GET_CATEGORY_PLAYLISTS = "categories/{category_id}/playlists"
    const val ENDPOINT_GET_HOME_PLAYLISTS = "home-playlists"
    const val ENDPOINT_GET_PLAYLIST = "playlists/{playlist_id}"
    const val ENDPOINT_GET_SEARCH = "search"

    object Parameters {
        const val ADDITIONAL_TYPES = "additional_types"
        const val INCLUDE_EXTERNAL = "include_external"
        const val LIMIT = "limit"
        const val LOCALE = "locale"
        const val MARKET = "market"
        const val OFFSET = "offset"
        const val TYPE = "type"
        const val QUERY = "query"
    }
}

object Navigation {
    object Args {
        const val RESET_PASSWORD_EMAIL = "reset_password_email"
    }
}
