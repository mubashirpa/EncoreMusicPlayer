package com.encore.music.presentation.ui.fragments.signIn

import com.encore.music.core.UiText

data class SignInUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val isUserLoggedIn: Boolean = false,
    val openProgressDialog: Boolean = false,
    val password: String = "",
    val passwordError: UiText? = null,
    val remember: Boolean = false,
    val userMessage: UiText? = null,
)
