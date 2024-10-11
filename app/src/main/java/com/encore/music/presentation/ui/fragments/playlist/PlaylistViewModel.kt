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
import com.encore.music.domain.usecase.playlists.GetPlaylistUseCase
import com.encore.music.domain.usecase.songs.playlists.CreatePlaylistUseCase
import com.encore.music.domain.usecase.songs.playlists.DeletePlaylistUseCase
import com.encore.music.domain.usecase.songs.playlists.GetSavedLocalPlaylistsUseCase
import com.encore.music.domain.usecase.songs.playlists.GetSavedPlaylistUseCase
import com.encore.music.domain.usecase.songs.playlists.GetSavedPlaylistWithTracksAndArtistsUseCase
import com.encore.music.domain.usecase.songs.playlists.InsertPlaylistUseCase
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaylistViewModel(
    savedStateHandle: SavedStateHandle,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val getSavedLocalPlaylistsUseCase: GetSavedLocalPlaylistsUseCase,
    private val getSavedPlaylistUseCase: GetSavedPlaylistUseCase,
    private val getSavedPlaylistWithTracksAndArtistsUseCase: GetSavedPlaylistWithTracksAndArtistsUseCase,
    private val insertPlaylistUseCase: InsertPlaylistUseCase,
) : ViewModel() {
    private val playlistRoute = savedStateHandle.toRoute<Screen.Playlist>()
    private val playlistId = playlistRoute.id
    val isLocal = playlistRoute.isLocal

    private val _uiState = MutableLiveData<PlaylistUiState>()
    val uiState: LiveData<PlaylistUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<PlaylistUiEvent>()
    val uiEvent: SharedFlow<PlaylistUiEvent> = _uiEvent

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> = _isSaved

    private val _savedPlaylists = MutableLiveData<List<Playlist>>()
    val savedPlaylists: LiveData<List<Playlist>> = _savedPlaylists

    init {
        if (isLocal) {
            getPlaylistWithTracksAndArtists(playlistId)
        } else {
            getPlaylist(playlistId)
            getSavedPlaylist(playlistId)
        }
        getSavedLocalPlaylists()
    }

    fun onEvent(event: PlaylistEvent) {
        when (event) {
            is PlaylistEvent.OnCreatePlaylist -> {
                createPlaylist(event.playlist)
            }

            is PlaylistEvent.OnEditLocalPlaylist -> {
                // No need to save tracks since the tracks are already in the database
                savePlaylist(event.playlist.copy(tracks = null))
            }

            is PlaylistEvent.OnInsertTrackToLocalPlaylist -> {
                savePlaylist(event.playlist)
            }

            PlaylistEvent.OnDeleteLocalPlaylist -> {
                if (uiState.value is PlaylistUiState.Success) {
                    deletePlaylist((uiState.value as PlaylistUiState.Success).playlist)
                }
            }

            PlaylistEvent.OnRetry -> {
                if (!isLocal) {
                    getPlaylist(playlistId)
                }
            }

            PlaylistEvent.OnSavePlaylist -> {
                if (uiState.value is PlaylistUiState.Success) {
                    val playlist = (uiState.value as PlaylistUiState.Success).playlist
                    // No need to save tracks since the tracks are loaded form API
                    savePlaylist(playlist.copy(tracks = null))
                }
            }

            PlaylistEvent.OnUnSavePlaylist -> {
                if (uiState.value is PlaylistUiState.Success) {
                    deletePlaylist((uiState.value as PlaylistUiState.Success).playlist)
                }
            }
        }
    }

    // Load playlist from API if playlist is not local
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

    // Load only if playlist is not local to determine if the playlist is saved
    private fun getSavedPlaylist(playlistId: String) {
        viewModelScope.launch {
            getSavedPlaylistUseCase(playlistId).collect { playlist ->
                _isSaved.value = playlist != null
            }
        }
    }

    // Load playlist from database if playlist is local
    private fun getPlaylistWithTracksAndArtists(playlistId: String) {
        viewModelScope.launch {
            getSavedPlaylistWithTracksAndArtistsUseCase(playlistId).collect {
                _uiState.value = it?.let { playlist ->
                    PlaylistUiState.Success(playlist)
                } ?: PlaylistUiState.Error(UiText.StringResource(R.string.error_unexpected))
            }
        }
    }

    private fun deletePlaylist(playlist: Playlist) {
        deletePlaylistUseCase(playlist)
            .onEach {
                if (it is Result.Success) {
                    // No need to navigate up if the playlist is not local
                    if (isLocal) {
                        _uiEvent.emit(PlaylistUiEvent.NavigateUp)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun savePlaylist(playlist: Playlist) {
        insertPlaylistUseCase(playlist).launchIn(viewModelScope)
    }

    private fun createPlaylist(playlist: Playlist) {
        createPlaylistUseCase(playlist).launchIn(viewModelScope)
    }

    // Fetched to add track to playlist
    private fun getSavedLocalPlaylists() {
        viewModelScope.launch {
            getSavedLocalPlaylistsUseCase().collect {
                _savedPlaylists.value = it
            }
        }
    }
}
