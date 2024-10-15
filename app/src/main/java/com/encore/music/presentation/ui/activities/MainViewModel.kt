package com.encore.music.presentation.ui.activities

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.usecase.authentication.HasUserUseCase
import com.encore.music.player.PlaybackState
import com.encore.music.player.PlayerEvent
import com.encore.music.player.service.PlaybackServiceHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainViewModel(
    hasUserUseCase: HasUserUseCase,
    private val playbackServiceHandler: PlaybackServiceHandler,
) : ViewModel() {
    var isLoggedIn = false
    private var currentMediaItemIndex = 0

    val duration: MutableLiveData<Long> by lazy { MutableLiveData<Long>(0L) }
    val progress: MutableLiveData<Float> by lazy { MutableLiveData<Float>(0f) }
    val progressString: MutableLiveData<String> by lazy { MutableLiveData<String>("00:00") }
    val isPlaying: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }
    val currentSelectedAudio: MutableLiveData<Track> by lazy { MutableLiveData<Track>(null) }
    val trackList: MutableLiveData<MutableList<Track>> by lazy {
        MutableLiveData<MutableList<Track>>(mutableListOf())
    }

    init {
        isLoggedIn = hasUserUseCase()
        collectPlaybackState()
    }

    override fun onCleared() {
        viewModelScope.launch {
            playbackServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }
        super.onCleared()
    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.AddNextInPlaylist -> {
                addNextInPlaylist(event.track)
            }

            is MainUiEvent.AddPlaylist -> {
                addPlaylist(event.tracks, event.selectedTrackId)
            }

            is MainUiEvent.AddToPlaylist -> {
                addToPlaylist(event.track)
            }

            is MainUiEvent.ChangeShuffleModeEnabled -> {
                playbackServiceHandler.onPlayerEvents(PlayerEvent.ChangeShuffleModeEnabled(event.shuffleModeEnabled))
            }

            MainUiEvent.PlayPause -> {
                playbackServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
            }

            is MainUiEvent.SeekTo -> {
                playbackServiceHandler.onPlayerEvents(
                    PlayerEvent.SeekTo,
                    seekPosition = (((duration.value ?: 0) * event.position) / 100f).toLong(),
                )
            }

            MainUiEvent.SeekToNext -> {
                playbackServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)
            }

            MainUiEvent.SeekToPrevious -> {
                playbackServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrevious)
            }

            is MainUiEvent.SelectedAudioChange -> {
                playbackServiceHandler.onPlayerEvents(
                    PlayerEvent.SelectedAudioChange,
                    selectedAudioIndex = event.index,
                )
            }

            is MainUiEvent.UpdateProgress -> {
                playbackServiceHandler.onPlayerEvents(PlayerEvent.UpdateProgress(event.newProgress))
                progress.value = event.newProgress
            }
        }
    }

    private fun collectPlaybackState() {
        viewModelScope.launch {
            playbackServiceHandler.playbackState.collectLatest { playbackState ->
                when (playbackState) {
                    PlaybackState.Initial -> {}

                    is PlaybackState.Buffering -> {
                        calculateProgressValue(playbackState.progress)
                    }

                    is PlaybackState.Playing -> {
                        isPlaying.value = playbackState.isPlaying
                    }

                    is PlaybackState.Progress -> {
                        calculateProgressValue(playbackState.progress)
                    }

                    is PlaybackState.CurrentPlaying -> {
                        currentMediaItemIndex = playbackState.mediaItemIndex
                        if (trackList.value!!.isNotEmpty()) {
                            if (playbackState.mediaItemIndex >= trackList.value!!.size) {
                                currentSelectedAudio.value = null
                            } else {
                                currentSelectedAudio.value =
                                    trackList.value!![playbackState.mediaItemIndex]
                            }
                        }
                    }

                    is PlaybackState.Ready -> {
                        duration.value = playbackState.duration
                    }
                }
            }
        }
    }

    private fun calculateProgressValue(currentProgress: Long) {
        progress.value =
            if (currentProgress > 0) {
                ((currentProgress.toFloat() / duration.value!!.toFloat()) * 100f)
            } else {
                0f
            }
        progressString.value = formatDuration(currentProgress)
    }

    private fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes)
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun addPlaylist(
        tracks: List<Track>,
        selectedTrackId: String?,
    ) {
        trackList.value = tracks.filter { it.mediaUrl != null }.toMutableList()
        val selectedAudioIndex = trackList.value!!.indexOfFirst { it.id == selectedTrackId }
        trackList.value!!
            .map { it.toMediaItem() }
            .also { playbackServiceHandler.setMediaItems(it) }

        playbackServiceHandler.onPlayerEvents(
            PlayerEvent.SelectedAudioChange,
            selectedAudioIndex,
        )
    }

    private fun addToPlaylist(track: Track) {
        if (trackList.value!!.contains(track)) return
        if (track.mediaUrl == null) return

        trackList.value!!.add(track)
        playbackServiceHandler.addMediaItem(track.toMediaItem())
    }

    private fun addNextInPlaylist(track: Track) {
        if (track.mediaUrl == null) return

        val index = trackList.value!!.indexOfFirst { it.id == track.id }
        if (index != -1) {
            trackList.value!!.removeAt(index)
            trackList.value!!.add(currentMediaItemIndex + 1, track)
            playbackServiceHandler.moveMediaItemNext(index)
        } else {
            trackList.value!!.add(currentMediaItemIndex + 1, track)
            playbackServiceHandler.addMediaItemNext(track.toMediaItem())
        }
    }

    private fun Track.toMediaItem(): MediaItem =
        MediaItem
            .Builder()
            .setMediaId(id.orEmpty())
            .setUri(mediaUrl)
            .setMediaMetadata(
                MediaMetadata
                    .Builder()
                    .setArtist(artists?.firstOrNull()?.name)
                    .setTitle(name)
                    .setArtworkUri(Uri.parse(image.orEmpty()))
                    .build(),
            ).build()
}
