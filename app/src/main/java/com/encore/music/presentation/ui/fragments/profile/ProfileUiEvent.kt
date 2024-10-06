package com.encore.music.presentation.ui.fragments.profile

sealed class ProfileUiEvent {
    data object Logout : ProfileUiEvent()
}
