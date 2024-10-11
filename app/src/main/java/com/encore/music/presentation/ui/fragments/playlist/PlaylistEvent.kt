package com.encore.music.presentation.ui.fragments.playlist

sealed class PlaylistEvent {
    data object NavigateUp : PlaylistEvent()
}
