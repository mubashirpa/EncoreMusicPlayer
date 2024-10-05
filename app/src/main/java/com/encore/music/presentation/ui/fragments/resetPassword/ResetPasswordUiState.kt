package com.encore.music.presentation.ui.fragments.resetPassword

import com.encore.music.core.UiText

sealed class ResetPasswordUiState {
    data class EmailError(
        val message: UiText?,
    ) : ResetPasswordUiState()

    data class ResetPasswordError(
        val message: UiText,
    ) : ResetPasswordUiState()

    data object ResetPasswordLoading : ResetPasswordUiState()

    data object ResetPasswordSuccess : ResetPasswordUiState()
}
