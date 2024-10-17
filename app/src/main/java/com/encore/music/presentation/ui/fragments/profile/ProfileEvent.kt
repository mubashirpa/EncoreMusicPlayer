package com.encore.music.presentation.ui.fragments.profile

sealed class ProfileEvent {
    data object Logout : ProfileEvent()
}
