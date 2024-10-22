package com.encore.music.presentation.navigation

import com.encore.music.domain.model.search.SearchType
import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Onboarding : Screen()

    @Serializable
    data object SignIn : Screen()

    @Serializable
    data object SignUp : Screen()

    @Serializable
    data class ResetPassword(
        val email: String,
    ) : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object Search : Screen()

    @Serializable
    data object Library : Screen()

    @Serializable
    data class Playlist(
        val id: String,
        val isLocal: Boolean,
    ) : Screen()

    @Serializable
    data class Artist(
        val id: String,
    ) : Screen()

    @Serializable
    data object Player : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data class Category(
        val id: String,
        val title: String,
    ) : Screen()

    @Serializable
    data class SearchItems(
        val query: String,
        val type: SearchType,
    ) : Screen()
}
