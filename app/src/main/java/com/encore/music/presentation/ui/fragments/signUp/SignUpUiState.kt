package com.encore.music.presentation.ui.fragments.signUp

import com.encore.music.core.UiText

sealed class SignUpUiState {
    data class EmailError(
        val message: UiText?,
    ) : SignUpUiState()

    data class NameError(
        val message: UiText?,
    ) : SignUpUiState()

    data class PasswordError(
        val message: UiText?,
    ) : SignUpUiState()

    data class SignUpError(
        val message: UiText,
    ) : SignUpUiState()

    data object SignUpLoading : SignUpUiState()

    data object SignUpSuccess : SignUpUiState()
}
