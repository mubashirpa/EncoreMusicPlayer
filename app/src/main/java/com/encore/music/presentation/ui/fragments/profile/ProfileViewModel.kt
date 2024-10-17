package com.encore.music.presentation.ui.fragments.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.authentication.SignOutUseCase
import com.encore.music.domain.usecase.songs.playlists.GetSavedLocalPlaylistsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getSavedLocalPlaylistsUseCase: GetSavedLocalPlaylistsUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _uiEvent = MutableSharedFlow<ProfileEvent>()
    val uiEvent: SharedFlow<ProfileEvent> = _uiEvent

    private val _createdPlaylists = MutableLiveData<List<Playlist>>()
    val createdPlaylists: LiveData<List<Playlist>> = _createdPlaylists

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                user?.let { _currentUser.value = it }
            }
        }
        getSavedLocalPlaylists()
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.Logout -> {
                viewModelScope.launch {
                    signOutUseCase()
                    _uiEvent.emit(ProfileEvent.Logout)
                }
            }
        }
    }

    private fun getSavedLocalPlaylists() {
        viewModelScope.launch {
            getSavedLocalPlaylistsUseCase().collect {
                _createdPlaylists.value = it
            }
        }
    }
}
