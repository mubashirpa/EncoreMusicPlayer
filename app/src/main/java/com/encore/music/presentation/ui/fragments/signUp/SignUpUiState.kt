package com.encore.music.presentation.ui.fragments.signUp

import com.encore.music.core.UiText

data class SignUpUiState(
    val email: String = "",
    val emailError: UiText? = null,
    val isUserLoggedIn: Boolean = false,
    val name: String = "",
    val nameError: UiText? = null,
    val openProgressDialog: Boolean = false,
    val password: String = "",
    val passwordError: UiText? = null,
    val userMessage: UiText? = null,
)
