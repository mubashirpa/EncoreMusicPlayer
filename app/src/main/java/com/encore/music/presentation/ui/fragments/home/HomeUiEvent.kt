package com.encore.music.presentation.ui.fragments.home

sealed class HomeUiEvent {
    data object OnRetry : HomeUiEvent()
}
