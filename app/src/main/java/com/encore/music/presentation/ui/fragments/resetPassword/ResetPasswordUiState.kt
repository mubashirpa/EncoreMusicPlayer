package com.encore.music.presentation.ui.fragments.resetPassword

import com.encore.music.core.UiText

data class ResetPasswordUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val isPasswordResetEmailSend: Boolean = false,
    val openProgressDialog: Boolean = false,
    val userMessage: UiText? = null,
)
