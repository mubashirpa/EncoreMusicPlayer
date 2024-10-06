package com.encore.music.presentation.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.core.Result
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.playlists.GetHomePlaylistsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getHomePlaylistsUseCase: GetHomePlaylistsUseCase,
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

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.OnRetry -> {
                getTopTracks()
                getHomePlaylists()
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                user?.let { _currentUser.value = it }
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
                        image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
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
                        _uiState.value = HomeUiState.Empty
                    }

                    is Result.Error -> {
                        _uiState.value = HomeUiState.Error(result.message!!)
                    }

                    is Result.Loading -> {
                        _uiState.value = HomeUiState.Loading
                    }

                    is Result.Success -> {
                        val categories = result.data
                        if (categories == null) {
                            _uiState.value = HomeUiState.Empty
                        } else {
                            _uiState.value = HomeUiState.Success(categories)
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
