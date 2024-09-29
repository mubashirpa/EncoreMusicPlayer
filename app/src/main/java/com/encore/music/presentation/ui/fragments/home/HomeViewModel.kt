package com.encore.music.presentation.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.encore.music.domain.usecase.playlists.GetFeaturedPlaylistsUseCase
import com.encore.music.player.service.PlaybackServiceHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class HomeViewModel(
    private val getFeaturedPlaylistsUseCase: GetFeaturedPlaylistsUseCase,
    private val playbackServiceHandler: PlaybackServiceHandler,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        getFeaturedPlaylistsUseCase("token")
            .onEach {
                _uiState.update { currentState ->
                    currentState.copy(popularPlaylists = it)
                }
            }.launchIn(viewModelScope)

        val mediaItem =
            MediaItem
                .Builder()
                .setUri("https://download.samplelib.com/mp3/sample-15s.mp3")
                .setMediaMetadata(
                    MediaMetadata
                        .Builder()
                        .setAlbumArtist("Mubashir P A")
                        .setDisplayTitle("Sample 15 S")
                        .setSubtitle("15 Seconds")
                        .build(),
                ).build()
        playbackServiceHandler.setMediaItemList(listOf(mediaItem))
    }
}
