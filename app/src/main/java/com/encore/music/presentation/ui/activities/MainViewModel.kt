package com.encore.music.presentation.ui.activities

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.encore.music.core.UiText
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.authentication.HasUserUseCase
import com.encore.music.domain.usecase.songs.tracks.InsertRecentTrackUseCase
import com.encore.music.player.PlaybackState
import com.encore.music.player.PlayerEvent
import com.encore.music.player.RepeatMode
import com.encore.music.player.service.PlaybackServiceHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainViewModel(
    hasUserUseCase: HasUserUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val playbackServiceHandler: PlaybackServiceHandler,
    private val insertRecentTrackUseCase: InsertRecentTrackUseCase,
) : ViewModel() {
    private val _playerUiState = MutableStateFlow(PlayerUiState())
    val playerUiState: StateFlow<PlayerUiState> = _playerUiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MainEvent>()
    val uiEvent: SharedFlow<MainEvent> = _uiEvent

    var isLoggedIn = false
    private var isServiceRunning = false

    val duration: MutableLiveData<Long> by lazy { MutableLiveData<Long>(0L) }
    val progress: MutableLiveData<Float> by lazy { MutableLiveData<Float>(0f) }
    val progressString: MutableLiveData<String> by lazy { MutableLiveData<String>("00:00") }
    val trackList: MutableLiveData<List<Track>> by lazy {
        MutableLiveData<List<Track>>(mutableListOf())
    }

    init {
        isLoggedIn = hasUserUseCase()
        getCurrentUser()
        collectPlaybackState()
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

            is MainUiEvent.ChangeRepeatMode -> {
                val repeatMode =
                    when (playerUiState.value.repeatMode) {
                        RepeatMode.REPEAT_MODE_OFF -> RepeatMode.REPEAT_MODE_ALL
                        RepeatMode.REPEAT_MODE_ALL -> RepeatMode.REPEAT_MODE_ONE
                        RepeatMode.REPEAT_MODE_ONE -> RepeatMode.REPEAT_MODE_OFF
                    }
                playbackServiceHandler.onPlayerEvents(PlayerEvent.ChangeRepeatMode(repeatMode))
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

            is MainUiEvent.SetVolume -> {
                playbackServiceHandler.onPlayerEvents(PlayerEvent.SetVolume(event.volume))
            }

            is MainUiEvent.UpdateProgress -> {
                playbackServiceHandler.onPlayerEvents(PlayerEvent.UpdateProgress(event.newProgress))
                progress.value = event.newProgress
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                if (user == null) {
                    playbackServiceHandler.onPlayerEvents(PlayerEvent.Stop)
                    clearPlayerData()
                }
            }
        }
    }

    private fun collectPlaybackState() {
        viewModelScope.launch {
            playbackServiceHandler.playbackState.collectLatest { playbackState ->
                when (playbackState) {
                    is PlaybackState.AudioSessionId -> {
                        _playerUiState.update {
                            it.copy(audioSessionId = playbackState.audioSessionId)
                        }
                    }

                    is PlaybackState.Buffering -> {
                        calculateProgressValue(playbackState.progress)
                    }

                    is PlaybackState.CurrentPlaying -> {
                        if (trackList.value!!.isNotEmpty()) {
                            if (playbackState.mediaItemIndex >= trackList.value!!.size) {
                                _playerUiState.update {
                                    it.copy(
                                        currentTrackIndex = 0,
                                        currentPlayingTrack = null,
                                    )
                                }
                            } else {
                                val previousSelectedAudioId =
                                    playerUiState.value.currentPlayingTrack?.id
                                val currentPlayingTrack =
                                    trackList.value!![playbackState.mediaItemIndex]

                                _playerUiState.update {
                                    it.copy(
                                        currentTrackIndex = playbackState.mediaItemIndex,
                                        currentPlayingTrack = currentPlayingTrack,
                                    )
                                }

                                // Insert the track into the recent tracks list
                                if (previousSelectedAudioId != currentPlayingTrack.id) {
                                    insertRecentTrack(currentPlayingTrack)
                                }
                            }
                        }
                    }

                    PlaybackState.Initial -> Unit

                    is PlaybackState.Playing -> {
                        _playerUiState.update { it.copy(isPlaying = playbackState.isPlaying) }
                    }

                    is PlaybackState.Progress -> {
                        calculateProgressValue(playbackState.progress)
                    }

                    is PlaybackState.Ready -> {
                        duration.value = playbackState.duration
                    }

                    is PlaybackState.RepeatMode -> {
                        _playerUiState.update { it.copy(repeatMode = playbackState.repeatMode) }
                    }

                    is PlaybackState.ShuffleMode -> {
                        _playerUiState.update { it.copy(shuffleModeEnabled = playbackState.isEnabled) }
                    }

                    is PlaybackState.Volume -> {
                        _playerUiState.update { it.copy(volume = playbackState.volume) }
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
        if (!isServiceRunning) {
            viewModelScope.launch {
                _uiEvent.emit(MainEvent.StartService)
                isServiceRunning = true
            }
        }

        if (selectedTrackId != null && playerUiState.value.currentPlayingTrack?.id == selectedTrackId) return

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
        track.mediaUrl ?: return
        val tracks = trackList.value ?: return

        if (tracks.isEmpty()) {
            addPlaylist(listOf(track), track.id)
            return
        }

        if (playbackServiceHandler.addMediaItem(track.toMediaItem())) {
            trackList.value?.apply {
                trackList.value = plus(track)
            }
        } else {
            viewModelScope.launch {
                _uiEvent.emit(MainEvent.ShowMessage(UiText.DynamicString("This song is already in your queue")))
            }
        }
    }

    private fun addNextInPlaylist(track: Track) {
        track.mediaUrl ?: return
        val tracks = trackList.value ?: return

        if (tracks.isEmpty()) {
            addPlaylist(listOf(track), track.id)
            return
        }

        if (playbackServiceHandler.addMediaItemNext(track.toMediaItem())) {
            trackList.value =
                tracks.toMutableList().apply {
                    add(playerUiState.value.currentTrackIndex + 1, track)
                }
        } else {
            viewModelScope.launch {
                _uiEvent.emit(MainEvent.ShowMessage(UiText.DynamicString("This song is already in your queue")))
            }
        }
    }

    private fun clearPlayerData() {
        _playerUiState.update { PlayerUiState() }
        duration.value = 0L
        progress.value = 0f
        progressString.value = "00:00"
        trackList.value = mutableListOf()
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

    private fun insertRecentTrack(track: Track) {
        insertRecentTrackUseCase(track).launchIn(viewModelScope)
    }
}
