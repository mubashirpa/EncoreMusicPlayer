package com.encore.music.presentation.ui.fragments.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.usecase.artists.GetArtistsTopTracksUseCase
import com.encore.music.domain.usecase.songs.artists.FollowArtistUseCase
import com.encore.music.domain.usecase.songs.artists.GetFollowedArtistUseCase
import com.encore.music.domain.usecase.songs.artists.UnfollowArtistUseCase
import com.encore.music.domain.usecase.songs.playlists.CreatePlaylistUseCase
import com.encore.music.domain.usecase.songs.playlists.GetSavedLocalPlaylistsUseCase
import com.encore.music.domain.usecase.songs.playlists.InsertPlaylistUseCase
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ArtistViewModel(
    savedStateHandle: SavedStateHandle,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val followArtistUseCase: FollowArtistUseCase,
    private val getArtistsTopTracksUseCase: GetArtistsTopTracksUseCase,
    private val getFollowedArtistUseCase: GetFollowedArtistUseCase,
    private val getSavedLocalPlaylistsUseCase: GetSavedLocalPlaylistsUseCase,
    private val insertPlaylistUseCase: InsertPlaylistUseCase,
    private val unfollowArtistUseCase: UnfollowArtistUseCase,
) : ViewModel() {
    private val artistId = savedStateHandle.toRoute<Screen.Artist>().id

    private val _uiState = MutableLiveData<ArtistUiState>()
    val uiState: LiveData<ArtistUiState> = _uiState

    private val _isFollowed = MutableLiveData<Boolean>()
    val isFollowed: LiveData<Boolean> = _isFollowed

    private val _savedPlaylists = MutableLiveData<List<Playlist>>()
    val savedPlaylists: LiveData<List<Playlist>> = _savedPlaylists

    init {
        getFollowedArtist(artistId)
        getArtist(artistId)
        getSavedLocalPlaylists()
    }

    private fun getArtist(artistId: String) {
        getArtistsTopTracksUseCase(artistId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> Unit

                    is Result.Error -> {
                        _uiState.value = ArtistUiState.Error(result.message!!)
                    }

                    is Result.Loading -> {
                        _uiState.value = ArtistUiState.Loading
                    }

                    is Result.Success -> {
                        _uiState.value = result.data?.let { artist ->
                            ArtistUiState.Success(
                                artist = artist,
                                isFollowed = isFollowed.value == true,
                            )
                        } ?: ArtistUiState.Error(UiText.StringResource(R.string.error_unexpected))
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getFollowedArtist(artistId: String) {
        viewModelScope.launch {
            getFollowedArtistUseCase(artistId).collect { artist ->
                _isFollowed.value = artist != null
            }
        }
    }

    // Fetched to add track to playlist
    private fun getSavedLocalPlaylists() {
        viewModelScope.launch {
            getSavedLocalPlaylistsUseCase().collect {
                _savedPlaylists.value = it
            }
        }
    }

    fun followArtist(artist: Artist) {
        followArtistUseCase(artist).launchIn(viewModelScope)
    }

    fun unfollowArtist(artist: Artist) {
        unfollowArtistUseCase(artist).launchIn(viewModelScope)
    }

    fun retry() {
        getArtist(artistId)
    }

    fun createPlaylist(playlist: Playlist) {
        createPlaylistUseCase(playlist).launchIn(viewModelScope)
    }

    fun savePlaylist(playlist: Playlist) {
        insertPlaylistUseCase(playlist).launchIn(viewModelScope)
    }
}
