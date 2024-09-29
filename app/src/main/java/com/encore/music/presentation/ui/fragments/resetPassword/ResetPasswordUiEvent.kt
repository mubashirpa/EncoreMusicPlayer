package com.encore.music.presentation.ui.fragments.resetPassword

sealed class ResetPasswordUiEvent {
    data class OnEmailValueChange(
        val email: String,
    ) : ResetPasswordUiEvent()

    data class ResetPassword(
        val email: String,
    ) : ResetPasswordUiEvent()

    data object UserMessageShown : ResetPasswordUiEvent()
}
