package com.encore.music.presentation.ui.activities

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.usecase.authentication.HasUserUseCase
import com.encore.music.domain.usecase.songs.InsertRecentTrackUseCase
import com.encore.music.player.PlaybackState
import com.encore.music.player.PlayerEvent
import com.encore.music.player.service.PlaybackServiceHandler
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainViewModel(
    hasUserUseCase: HasUserUseCase,
    private val playbackServiceHandler: PlaybackServiceHandler,
    private val insertRecentTrackUseCase: InsertRecentTrackUseCase,
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
                        val previousSelectedAudioId = currentSelectedAudio.value?.id

                        if (trackList.value!!.isNotEmpty()) {
                            if (playbackState.mediaItemIndex >= trackList.value!!.size) {
                                currentSelectedAudio.value = null
                            } else {
                                currentSelectedAudio.value =
                                    trackList.value!![playbackState.mediaItemIndex]

                                // Insert the track into the recent tracks list
                                if (previousSelectedAudioId != currentSelectedAudio.value?.id) {
                                    insertRecentTrack(currentSelectedAudio.value!!)
                                }
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
        selectedTrackId: String? = null,
    ) {
        if (selectedTrackId != null && currentSelectedAudio.value?.id == selectedTrackId) return

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

    // Add a media item to the end of the playlist (for "Add to Queue")
    private fun addToPlaylist(track: Track) {
        if (track.mediaUrl == null) return

        if (trackList.value!!.isEmpty()) {
            addPlaylist(listOf(track), track.id)
            return
        }

        val songIndex = findTrackItemIndex(track)

        if (songIndex != -1) {
            if (songIndex <= currentMediaItemIndex) return

            // If the song is already in the playlist, move it to the end
            moveTrackItem(songIndex, trackList.value!!.size)
        } else {
            // Add the media item to the end
            trackList.value?.add(track)
            // Trigger LiveData update
            trackList.value = trackList.value
        }

        playbackServiceHandler.addMediaItem(track.toMediaItem())
    }

    // Add a media item to play next (for "Play Next")
    private fun addNextInPlaylist(track: Track) {
        if (track.mediaUrl == null) return

        if (trackList.value!!.isEmpty()) {
            addPlaylist(listOf(track), track.id)
            return
        }

        val songIndex = findTrackItemIndex(track)

        if (songIndex != -1) {
            if (songIndex <= currentMediaItemIndex) return

            // If the song is already in the playlist, move it to the next position
            moveTrackItem(songIndex, currentMediaItemIndex + 1)
        } else {
            // Add the media item right after the currently playing song
            trackList.value?.add(currentMediaItemIndex + 1, track)
            // Trigger LiveData update
            trackList.value = trackList.value
        }

        playbackServiceHandler.addMediaItemNext(track.toMediaItem())
    }

    private fun findTrackItemIndex(track: Track): Int = trackList.value?.indexOfFirst { it.id == track.id } ?: -1

    private fun moveTrackItem(
        fromIndex: Int,
        toIndex: Int,
    ) {
        trackList.value?.let {
            val item = it[fromIndex]
            it.add(toIndex, item)
            it.removeAt(fromIndex)
            // Trigger LiveData update
            trackList.value = it
        }
    }

    private fun insertRecentTrack(track: Track) {
        insertRecentTrackUseCase(track).launchIn(viewModelScope)
    }

    private fun Track.toMediaItem(): MediaItem =
        MediaItem
            .Builder()
            .setMediaId(id.orEmpty())
            .setUri(mediaUrl.orEmpty())
            .setMediaMetadata(
                MediaMetadata
                    .Builder()
                    .setArtist(artists?.firstOrNull()?.name)
                    .setTitle(name)
                    .setArtworkUri(Uri.parse(image.orEmpty()))
                    .build(),
            ).build()
}
