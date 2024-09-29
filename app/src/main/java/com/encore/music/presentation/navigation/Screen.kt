package com.encore.music.presentation.navigation

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
}
