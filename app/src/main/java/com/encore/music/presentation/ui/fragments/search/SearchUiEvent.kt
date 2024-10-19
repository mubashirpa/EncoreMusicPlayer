package com.encore.music.presentation.ui.fragments.search

import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.search.SearchType

sealed class SearchUiEvent {
    data class OnCreatePlaylist(
        val playlist: Playlist,
    ) : SearchUiEvent()

    data class OnInsertTrackToLocalPlaylist(
        val playlist: Playlist,
    ) : SearchUiEvent()

    data class OnSearch(
        val query: String,
        val searchType: SearchType,
        val delay: Long = 0,
    ) : SearchUiEvent()

    data object OnRetry : SearchUiEvent()

    data object OnSearchOpened : SearchUiEvent()
}
