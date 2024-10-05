package com.encore.music.presentation.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.core.Result
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.playlists.GetHomePlaylistsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomePlaylistsUseCase: GetHomePlaylistsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    private val _uiState = MutableSharedFlow<HomeUiState>()
    val uiState: SharedFlow<HomeUiState> = _uiState

    private val _topTracks = MutableStateFlow(emptyList<Track>())
    val topTracks: StateFlow<List<Track>> = _topTracks.asStateFlow()

    private val _currentUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        getCurrentUser()
        getTopTracks()
        getHomePlaylists()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect {
                _currentUser.value = it
            }
        }
    }

    private fun getTopTracks() {
        // TODO: Get top tracks from Room Database
        viewModelScope.launch {
            delay(1000)
            _topTracks.value =
                List(6) {
                    Track(
                        id = "$it",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                        name = "Track $it",
                    )
                }
        }
    }

    private fun getHomePlaylists() {
        getHomePlaylistsUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {
                        _uiState.emit(HomeUiState.Empty)
                    }

                    is Result.Error -> {
                        _uiState.emit(HomeUiState.Error(result.message!!))
                    }

                    is Result.Loading -> {
                        _uiState.emit(HomeUiState.Loading)
                    }

                    is Result.Success -> {
                        val playlist = result.data.orEmpty()
                        if (playlist.isEmpty()) {
                            _uiState.emit(HomeUiState.Empty)
                        } else {
                            _uiState.emit(HomeUiState.Success(playlist))
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
