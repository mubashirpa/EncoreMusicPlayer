package com.encore.music.presentation.ui.fragments.signUp

sealed class SignUpUiEvent {
    data class OnEmailValueChange(
        val email: String,
    ) : SignUpUiEvent()

    data class OnNameValueChange(
        val name: String,
    ) : SignUpUiEvent()

    data class OnPasswordValueChange(
        val password: String,
    ) : SignUpUiEvent()

    data class SignUp(
        val name: String,
        val email: String,
        val password: String,
    ) : SignUpUiEvent()

    data class SignUpWithGoogle(
        val token: String,
    ) : SignUpUiEvent()
}
