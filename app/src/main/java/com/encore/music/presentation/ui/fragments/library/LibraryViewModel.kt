package com.encore.music.presentation.ui.fragments.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.domain.model.spotify.artists.Artist
import com.encore.music.domain.model.spotify.playlists.Playlist
import com.encore.music.domain.model.spotify.tracks.Track
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
            _artists.update {
                listOf(
                    Artist(
                        name = "Artist 1",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Artist(
                        name = "Artist 2",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Artist(
                        name = "Artist 3",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                )
            }
            _uiState.emit(LibraryUiState.Success)
        }
    }

    private fun getUserPlaylists() {
        viewModelScope.launch {
            delay(2000)
            _playlists.update {
                listOf(
                    Playlist(
                        name = "Playlist 1",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                        ownerDisplayName = "Spotify",
                    ),
                    Playlist(
                        name = "Playlist 2",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                        ownerDisplayName = "Spotify",
                    ),
                )
            }
            _uiState.emit(LibraryUiState.Success)
        }
    }

    private fun getUserTracks() {
        viewModelScope.launch {
            delay(1000)
            _tracks.update {
                listOf(
                    Track(
                        name = "Track 1",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Track(
                        name = "Track 2",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Track(
                        name = "Track 3",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                    Track(
                        name = "Track 4",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                )
            }
            _uiState.emit(LibraryUiState.Success)
        }
    }
}
