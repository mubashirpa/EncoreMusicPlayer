package com.encore.music.player.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.encore.music.player.PlaybackState
import com.encore.music.player.PlayerEvent
import com.encore.music.player.RepeatMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaybackServiceHandler(
    private val exoPlayer: ExoPlayer,
    private val scope: CoroutineScope,
) : Player.Listener {
    private val _playbackState: MutableStateFlow<PlaybackState> =
        MutableStateFlow(PlaybackState.Initial)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var job: Job? = null

    init {
        exoPlayer.addListener(this)
    }

    // Player event listener

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> {
                _playbackState.update { PlaybackState.Buffering(exoPlayer.currentPosition) }
            }

            ExoPlayer.STATE_READY -> {
                _playbackState.update { PlaybackState.Ready(exoPlayer.duration) }
            }

            else -> Unit
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        _playbackState.update { PlaybackState.Playing(isPlaying) }
        if (isPlaying) {
            startProgressUpdate()
        } else {
            stopProgressUpdate()
        }
    }

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        super.onMediaItemTransition(mediaItem, reason)
        _playbackState.update { PlaybackState.Ready(exoPlayer.duration) }
        _playbackState.update { PlaybackState.CurrentPlaying(exoPlayer.currentMediaItemIndex) }
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
        _playbackState.update { PlaybackState.ShuffleMode(shuffleModeEnabled) }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super.onRepeatModeChanged(repeatMode)
        val playerRepeatMode =
            when (repeatMode) {
                Player.REPEAT_MODE_ONE -> RepeatMode.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ALL -> RepeatMode.REPEAT_MODE_ALL
                else -> RepeatMode.REPEAT_MODE_OFF
            }
        _playbackState.update { PlaybackState.RepeatMode(playerRepeatMode) }
    }

    override fun onVolumeChanged(volume: Float) {
        super.onVolumeChanged(volume)
        _playbackState.update { PlaybackState.Volume(volume) }
    }

    // Private methods

    private fun playOrPause() {
        exoPlayer.run {
            when {
                isPlaying -> {
                    pause()
                }

                exoPlayer.playbackState == Player.STATE_IDLE -> {
                    prepare()
                }

                exoPlayer.playbackState == Player.STATE_ENDED -> {
                    seekToDefaultPosition(0)
                }

                else -> {
                    play()
                }
            }
        }
    }

    private fun startProgressUpdate() {
        scope.launch {
            job.run {
                while (true) {
                    delay(500)
                    _playbackState.update { PlaybackState.Progress(exoPlayer.currentPosition) }
                }
            }
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
    }

    private fun findMediaItemIndex(mediaItem: MediaItem): Int {
        for (i in 0 until exoPlayer.mediaItemCount) {
            if (exoPlayer.getMediaItemAt(i).mediaId == mediaItem.mediaId) {
                return i
            }
        }
        return -1
    }

    // Public methods

    fun setMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun setMediaItems(mediaItems: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    fun addMediaItem(mediaItem: MediaItem): Boolean {
        val songIndex = findMediaItemIndex(mediaItem)

        return if (songIndex == -1) {
            exoPlayer.addMediaItem(mediaItem)
            true
        } else {
            false
        }
    }

    fun addMediaItemNext(mediaItem: MediaItem): Boolean {
        val currentIndex = exoPlayer.currentMediaItemIndex
        val songIndex = findMediaItemIndex(mediaItem)

        return if (songIndex == -1) {
            exoPlayer.addMediaItem(currentIndex + 1, mediaItem)
            true
        } else {
            false
        }
    }

    fun onPlayerEvents(
        event: PlayerEvent,
        selectedAudioIndex: Int = -1,
        seekPosition: Long = 0,
    ) {
        when (event) {
            PlayerEvent.Backward -> {
                exoPlayer.seekBack()
            }

            is PlayerEvent.ChangeRepeatMode -> {
                when (event.repeatMode) {
                    RepeatMode.REPEAT_MODE_OFF -> exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
                    RepeatMode.REPEAT_MODE_ONE -> exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                    RepeatMode.REPEAT_MODE_ALL -> exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                }
            }

            is PlayerEvent.ChangeShuffleModeEnabled -> {
                exoPlayer.shuffleModeEnabled = event.shuffleModeEnabled
            }

            PlayerEvent.Forward -> {
                exoPlayer.seekForward()
            }

            PlayerEvent.PlayPause -> {
                playOrPause()
            }

            PlayerEvent.SeekTo -> {
                exoPlayer.seekTo(seekPosition)
            }

            PlayerEvent.SeekToNext -> {
                exoPlayer.seekToNext()
            }

            PlayerEvent.SeekToPrevious -> {
                exoPlayer.seekToPrevious()
            }

            PlayerEvent.SelectedAudioChange -> {
                // If the selected audio is the current one, just play/pause.
                // Otherwise, seek to the new audio and start playback.
                if (selectedAudioIndex == exoPlayer.currentMediaItemIndex) {
                    playOrPause()
                } else {
                    exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                    exoPlayer.playWhenReady = true
                }
            }

            is PlayerEvent.SetVolume -> {
                exoPlayer.volume = event.volume
            }

            PlayerEvent.Stop -> {
                exoPlayer.stop()
                exoPlayer.clearMediaItems()
            }

            is PlayerEvent.UpdateProgress -> {
                exoPlayer.seekTo((exoPlayer.duration * event.newProgress).toLong())
            }
        }
    }
}
