package com.encore.music.presentation.ui.fragments.playlist

sealed class PlaylistUiEvent {
    data object NavigateUp : PlaylistUiEvent()
}
