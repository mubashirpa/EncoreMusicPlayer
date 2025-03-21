package com.encore.music.presentation.ui.fragments.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.songs.artists.GetFollowedArtistsUseCase
import com.encore.music.domain.usecase.songs.playlists.CreatePlaylistUseCase
import com.encore.music.domain.usecase.songs.playlists.GetSavedPlaylistsUseCase
import com.encore.music.domain.usecase.songs.playlists.InsertPlaylistUseCase
import com.encore.music.domain.usecase.songs.tracks.GetRecentTracksUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getFollowedArtistsUseCase: GetFollowedArtistsUseCase,
    private val getRecentTracksUseCase: GetRecentTracksUseCase,
    private val getSavedPlaylistsUseCase: GetSavedPlaylistsUseCase,
    private val insertPlaylistUseCase: InsertPlaylistUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<LibraryUiState>()
    val uiState: LiveData<LibraryUiState> = _uiState

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _savedArtists = MutableLiveData<List<Artist>>()
    val savedArtists: LiveData<List<Artist>> = _savedArtists

    private val _savedPlaylists = MutableLiveData<List<Playlist>>()
    val savedPlaylists: LiveData<List<Playlist>> = _savedPlaylists

    private val _recentTracks = MutableLiveData<List<Track>>()
    val recentTracks: LiveData<List<Track>> = _recentTracks

    private val isEmpty = mutableListOf(false, false, false)

    init {
        _uiState.value = LibraryUiState.Loading
        getCurrentUser()
        getSavedArtists()
        getSavedPlaylists()
        getRecentTracks()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                user?.let { _currentUser.value = it }
            }
        }
    }

    private fun getSavedArtists() {
        viewModelScope.launch {
            getFollowedArtistsUseCase().collect {
                isEmpty[0] = it.isEmpty()
                _savedArtists.value = it
                emitUiState()
            }
        }
    }

    private fun getSavedPlaylists() {
        viewModelScope.launch {
            getSavedPlaylistsUseCase().collect {
                isEmpty[1] = it.isEmpty()
                _savedPlaylists.value = it
                emitUiState()
            }
        }
    }

    private fun getRecentTracks() {
        viewModelScope.launch {
            getRecentTracksUseCase().collect {
                isEmpty[2] = it.isEmpty()
                _recentTracks.value = it
                emitUiState()
            }
        }
    }

    private fun emitUiState() {
        if (isEmpty.all { it }) {
            _uiState.value = LibraryUiState.Empty
        } else {
            if (uiState.value !is LibraryUiState.Success) {
                _uiState.value = LibraryUiState.Success
            }
        }
    }

    fun createPlaylist(playlist: Playlist) {
        createPlaylistUseCase(playlist).launchIn(viewModelScope)
    }

    fun savePlaylist(playlist: Playlist) {
        insertPlaylistUseCase(playlist).launchIn(viewModelScope)
    }
}
