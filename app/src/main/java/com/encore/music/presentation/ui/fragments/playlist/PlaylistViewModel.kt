package com.encore.music.presentation.ui.fragments.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.encore.music.core.Result
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.usecase.playlists.GetPlaylistUseCase
import com.encore.music.domain.usecase.songs.InsertPlaylistUseCase
import com.encore.music.domain.usecase.songs.InsertRecentTrackUseCase
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlaylistViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val insertPlaylistUseCase: InsertPlaylistUseCase,
    private val insertRecentTrackUseCase: InsertRecentTrackUseCase,
) : ViewModel() {
    private val playlistId = savedStateHandle.toRoute<Screen.Playlist>().id

    private val _uiState = MutableLiveData<PlaylistUiState>()
    val uiState: LiveData<PlaylistUiState> = _uiState

    init {
        getPlaylist(playlistId)
    }

    fun onEvent(event: PlaylistUiEvent) {
        when (event) {
            is PlaylistUiEvent.AddTrackToPlaylist -> {
                insertRecentTrack(event.track)
            }

            PlaylistUiEvent.OnRetry -> {
                getPlaylist(playlistId)
            }

            PlaylistUiEvent.SavePlaylist -> {
                insertPlaylist((_uiState.value as PlaylistUiState.Success).playlist)
            }
        }
    }

    private fun getPlaylist(playlistId: String) {
        getPlaylistUseCase(playlistId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {
                        _uiState.value = PlaylistUiState.Empty
                    }

                    is Result.Error -> {
                        _uiState.value = PlaylistUiState.Error(result.message!!)
                    }

                    is Result.Loading -> {
                        _uiState.value = PlaylistUiState.Loading
                    }

                    is Result.Success -> {
                        val playlist = result.data
                        if (playlist == null) {
                            _uiState.value = PlaylistUiState.Empty
                        } else {
                            _uiState.value = PlaylistUiState.Success(playlist)
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun insertRecentTrack(track: Track) {
        insertRecentTrackUseCase(track).launchIn(viewModelScope)
    }

    private fun insertPlaylist(playlist: Playlist) {
        insertPlaylistUseCase(playlist).launchIn(viewModelScope)
    }
}
