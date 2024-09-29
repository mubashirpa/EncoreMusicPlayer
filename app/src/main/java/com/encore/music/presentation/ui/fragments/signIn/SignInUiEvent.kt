package com.encore.music.presentation.ui.fragments.signIn

sealed class SignInUiEvent {
    data class OnEmailValueChange(
        val email: String,
    ) : SignInUiEvent()

    data class OnPasswordValueChange(
        val password: String,
    ) : SignInUiEvent()

    data class OnRememberSwitchCheckedChange(
        val checked: Boolean,
    ) : SignInUiEvent()

    data class SignIn(
        val email: String,
        val password: String,
        val remember: Boolean,
    ) : SignInUiEvent()

    data class SignInWithGoogle(
        val token: String,
    ) : SignInUiEvent()

    data object UserMessageShown : SignInUiEvent()
}
