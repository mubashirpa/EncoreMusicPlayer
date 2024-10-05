package com.encore.music.presentation.ui.fragments.playlist

sealed class PlaylistUiEvent {
    data object AddToPlaylist : PlaylistUiEvent()

    data object OnRetry : PlaylistUiEvent()
}
