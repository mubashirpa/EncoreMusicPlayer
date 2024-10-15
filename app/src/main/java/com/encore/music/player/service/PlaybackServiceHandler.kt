package com.encore.music.player.service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.encore.music.player.PlaybackState
import com.encore.music.player.PlayerEvent
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
        _playbackState.update { PlaybackState.CurrentPlaying(exoPlayer.currentMediaItemIndex) }
        _playbackState.update { PlaybackState.Ready(exoPlayer.duration) }
        _playbackState.update { PlaybackState.Playing(exoPlayer.isPlaying) }
    }

    // Private methods

    private fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            _playbackState.update { PlaybackState.Playing(true) }
            startProgressUpdate()
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
        _playbackState.update { PlaybackState.Playing(false) }
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

    fun addMediaItem(mediaItem: MediaItem) {
        val songIndex = findMediaItemIndex(mediaItem)

        if (songIndex != -1) {
            val lastIndex = exoPlayer.mediaItemCount - 1
            exoPlayer.moveMediaItem(songIndex, lastIndex + 1)
        } else {
            exoPlayer.addMediaItem(mediaItem)
        }
    }

    fun addMediaItemNext(mediaItem: MediaItem) {
        val currentIndex = exoPlayer.currentMediaItemIndex
        val songIndex = findMediaItemIndex(mediaItem)

        if (songIndex != -1) {
            exoPlayer.moveMediaItem(songIndex, currentIndex + 1)
        } else {
            exoPlayer.addMediaItem(currentIndex + 1, mediaItem)
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
                    _playbackState.update { PlaybackState.Playing(true) }
                    exoPlayer.playWhenReady = true
                    startProgressUpdate()
                }
            }

            PlayerEvent.Stop -> {
                stopProgressUpdate()
            }

            is PlayerEvent.UpdateProgress -> {
                exoPlayer.seekTo((exoPlayer.duration * event.newProgress).toLong())
            }
        }
    }
}
