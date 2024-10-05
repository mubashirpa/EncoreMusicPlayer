package com.encore.music.presentation.ui.fragments.signIn

import com.encore.music.core.UiText

sealed class SignInUiState {
    data class EmailError(
        val message: UiText?,
    ) : SignInUiState()

    data class PasswordError(
        val message: UiText?,
    ) : SignInUiState()

    data class SignInError(
        val message: UiText,
    ) : SignInUiState()

    data object SignInLoading : SignInUiState()

    data object SignInSuccess : SignInUiState()
}
