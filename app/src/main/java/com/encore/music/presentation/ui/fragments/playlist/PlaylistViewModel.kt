package com.encore.music.presentation.ui.fragments.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.encore.music.core.Result
import com.encore.music.domain.usecase.playlists.GetPlaylistUseCase
import com.encore.music.domain.usecase.playlists.InsertPlaylistUseCase
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaylistViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val insertPlaylistUseCase: InsertPlaylistUseCase,
) : ViewModel() {
    private val playlistId = savedStateHandle.toRoute<Screen.Playlist>().id

    private val _uiState = MutableLiveData<PlaylistUiState>()
    val uiState: LiveData<PlaylistUiState> = _uiState

    init {
        getPlaylist(playlistId)
    }

    fun onEvent(event: PlaylistUiEvent) {
        when (event) {
            PlaylistUiEvent.AddToPlaylist -> {
                viewModelScope.launch {
                    insertPlaylistUseCase((_uiState.value as PlaylistUiState.Success).playlist)
                }
            }

            PlaylistUiEvent.OnRetry -> {
                getPlaylist(playlistId)
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
}
