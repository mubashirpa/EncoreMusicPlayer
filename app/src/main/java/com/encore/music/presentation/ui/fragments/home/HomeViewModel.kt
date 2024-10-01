package com.encore.music.presentation.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.domain.model.spotify.playlists.Playlist
import com.encore.music.domain.model.spotify.tracks.Track
import com.encore.music.domain.usecase.playlists.GetFeaturedPlaylistsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getFeaturedPlaylistsUseCase: GetFeaturedPlaylistsUseCase,
) : ViewModel() {
    private val _uiState = MutableSharedFlow<HomeUiState>()
    val uiState: SharedFlow<HomeUiState> = _uiState

    private val _topTracks = MutableStateFlow(emptyList<Track>())
    val topTracks: StateFlow<List<Track>> = _topTracks.asStateFlow()

    private val _popularPlaylists = MutableStateFlow(emptyList<Playlist>())
    val popularPlaylists: StateFlow<List<Playlist>> = _popularPlaylists.asStateFlow()

    private val _trendingPlaylists = MutableStateFlow(emptyList<Playlist>())
    val trendingPlaylists: StateFlow<List<Playlist>> = _trendingPlaylists.asStateFlow()

    private val _topChartsPlaylists = MutableStateFlow(emptyList<Playlist>())
    val topChartsPlaylists: StateFlow<List<Playlist>> = _topChartsPlaylists.asStateFlow()

    private val _newReleasesPlaylists = MutableStateFlow(emptyList<Playlist>())
    val newReleasesPlaylists: StateFlow<List<Playlist>> = _newReleasesPlaylists.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.emit(HomeUiState.Loading)
        }
        getFeaturedPlaylistsUseCase("token")
            .onEach {
                // TODO("Emit success only if list is not empty")
                _uiState.emit(HomeUiState.Success)
                _popularPlaylists.value = it
                _trendingPlaylists.value = it
                _topChartsPlaylists.value = it
                _newReleasesPlaylists.value = it
            }.launchIn(viewModelScope)
    }
}
