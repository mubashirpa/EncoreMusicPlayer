package com.encore.music.presentation.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.playlists.GetHomePlaylistsUseCase
import com.encore.music.domain.usecase.songs.GetRecentTracksUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getHomePlaylistsUseCase: GetHomePlaylistsUseCase,
    private val getRecentTracksUseCase: GetRecentTracksUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _topTracks = MutableLiveData<List<Track>>()
    val topTracks: LiveData<List<Track>> = _topTracks

    init {
        getCurrentUser()
        getTopTracks()
        getHomePlaylists()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                user?.let { _currentUser.value = it }
            }
        }
    }

    private fun getTopTracks() {
        viewModelScope.launch {
            getRecentTracksUseCase().collect { tracks ->
                _topTracks.value = tracks
            }
        }
    }

    private fun getHomePlaylists() {
        getHomePlaylistsUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Empty -> Unit

                    is Result.Error -> {
                        _uiState.value = HomeUiState.Error(result.message!!)
                    }

                    is Result.Loading -> {
                        _uiState.value = HomeUiState.Loading
                    }

                    is Result.Success -> {
                        _uiState.value = result.data?.let { categories ->
                            HomeUiState.Success(categories)
                        } ?: HomeUiState.Error(UiText.StringResource(R.string.error_unexpected))
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun retry() {
        getHomePlaylists()
    }
}
