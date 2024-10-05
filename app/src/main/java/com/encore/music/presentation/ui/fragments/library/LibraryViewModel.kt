package com.encore.music.presentation.ui.fragments.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    private val _uiState = MutableSharedFlow<LibraryUiState>()
    val uiState: SharedFlow<LibraryUiState> = _uiState

    private val _artists = MutableStateFlow(emptyList<Artist>())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _playlists = MutableStateFlow(emptyList<Playlist>())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _tracks = MutableStateFlow(emptyList<Track>())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    val loading = MutableList<Boolean?>(3) { null }

    init {
        viewModelScope.launch {
            _uiState.emit(LibraryUiState.Loading)
        }
        getUserArtists()
        getUserPlaylists()
        getUserTracks()
    }

    private fun getUserArtists() {
        viewModelScope.launch {
            delay(3000)
            // TODO("Emit success only if list is not empty")
            _uiState.emit(LibraryUiState.Success)
            _artists.update {
                listOf(
                    Artist(
                        name = "Artist 1",
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Artist(
                        name = "Artist 2",
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Artist(
                        name = "Artist 3",
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                )
            }
        }
    }

    private fun getUserPlaylists() {
        viewModelScope.launch {
            delay(2000)
            // TODO("Emit success only if list is not empty")
            _uiState.emit(LibraryUiState.Success)
            _playlists.update {
                listOf(
                    Playlist(
                        name = "Playlist 1",
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                        owner = "Spotify",
                    ),
                    Playlist(
                        name = "Playlist 2",
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                        owner = "Spotify",
                    ),
                )
            }
        }
    }

    private fun getUserTracks() {
        viewModelScope.launch {
            delay(1000)
            // TODO("Emit success only if list is not empty")
            _uiState.emit(LibraryUiState.Success)
            _tracks.update {
                listOf(
                    Track(
                        name = "Track 1",
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Track(
                        name = "Track 2",
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Track(
                        name = "Track 3",
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Track(
                        name = "Track 4",
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                )
            }
        }
    }
}
