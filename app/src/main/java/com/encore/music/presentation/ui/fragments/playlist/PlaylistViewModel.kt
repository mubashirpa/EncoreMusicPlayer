package com.encore.music.presentation.ui.fragments.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.usecase.playlists.GetPlaylistUseCase
import com.encore.music.domain.usecase.songs.DeletePlaylistUseCase
import com.encore.music.domain.usecase.songs.GetSavedPlaylistUseCase
import com.encore.music.domain.usecase.songs.GetSavedPlaylistWithTracksAndArtistsUseCase
import com.encore.music.domain.usecase.songs.InsertPlaylistUseCase
import com.encore.music.domain.usecase.songs.InsertRecentTrackUseCase
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaylistViewModel(
    savedStateHandle: SavedStateHandle,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getSavedPlaylistUseCase: GetSavedPlaylistUseCase,
    private val getSavedPlaylistWithTracksAndArtistsUseCase: GetSavedPlaylistWithTracksAndArtistsUseCase,
    private val insertPlaylistUseCase: InsertPlaylistUseCase,
    private val insertRecentTrackUseCase: InsertRecentTrackUseCase,
) : ViewModel() {
    private val playlistRoute = savedStateHandle.toRoute<Screen.Playlist>()
    private val playlistId = playlistRoute.id
    val isLocal = playlistRoute.isLocal

    private val _uiState = MutableLiveData<PlaylistUiState>()
    val uiState: LiveData<PlaylistUiState> = _uiState

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> = _isSaved

    init {
        if (isLocal) {
            getPlaylistWithTracksAndArtists(playlistId)
        } else {
            getPlaylist(playlistId)
            getSavedPlaylist(playlistId)
        }
    }

    private fun getPlaylist(playlistId: String) {
        getPlaylistUseCase(playlistId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> Unit

                    is Result.Error -> {
                        _uiState.value = PlaylistUiState.Error(result.message!!)
                    }

                    is Result.Loading -> {
                        _uiState.value = PlaylistUiState.Loading
                    }

                    is Result.Success -> {
                        _uiState.value = result.data?.let { playlist ->
                            PlaylistUiState.Success(playlist)
                        } ?: PlaylistUiState.Error(UiText.StringResource(R.string.error_unexpected))
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getPlaylistWithTracksAndArtists(playlistId: String) {
        viewModelScope.launch {
            getSavedPlaylistWithTracksAndArtistsUseCase(playlistId).collect {
                _uiState.value = it?.let { playlist ->
                    PlaylistUiState.Success(playlist)
                } ?: PlaylistUiState.Error(UiText.StringResource(R.string.error_unexpected))
            }
        }
    }

    // Load only if playlist is not local to determine if the playlist is saved
    private fun getSavedPlaylist(playlistId: String) {
        viewModelScope.launch {
            getSavedPlaylistUseCase(playlistId).collect { playlist ->
                _isSaved.value = playlist != null
            }
        }
    }

    private fun savePlaylist(playlist: Playlist) {
        // No need to save tracks if the playlist is not local since the tracks are loaded form API
        // Also for local playlists, the tracks are already saved in the database
        insertPlaylistUseCase(playlist.copy(tracks = null)).launchIn(viewModelScope)
    }

    private fun deletePlaylist(playlist: Playlist) {
        deletePlaylistUseCase(playlist).launchIn(viewModelScope)
    }

    fun savePlaylist() {
        if (uiState.value is PlaylistUiState.Success) {
            savePlaylist((uiState.value as PlaylistUiState.Success).playlist)
        }
    }

    fun editPlaylist(
        name: String,
        description: String,
    ) {
        if (uiState.value is PlaylistUiState.Success) {
            val playlist = (uiState.value as PlaylistUiState.Success).playlist
            savePlaylist(playlist.copy(name = name, description = description))
        }
    }

    fun deletePlaylist() {
        if (uiState.value is PlaylistUiState.Success) {
            deletePlaylist((uiState.value as PlaylistUiState.Success).playlist)
        }
    }

    fun insertRecentTrack(track: Track) {
        insertRecentTrackUseCase(track).launchIn(viewModelScope)
    }

    fun retry() {
        if (!isLocal) {
            getPlaylist(playlistId)
        }
    }
}
