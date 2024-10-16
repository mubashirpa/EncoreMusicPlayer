package com.encore.music.presentation.ui.activities

import com.encore.music.domain.model.tracks.Track
import com.encore.music.player.RepeatMode

data class PlayerUiState(
    val currentPlayingTrack: Track? = null,
    val currentTrackIndex: Int = 0,
    val isPlaying: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.REPEAT_MODE_OFF,
    val shuffleModeEnabled: Boolean = false,
)
