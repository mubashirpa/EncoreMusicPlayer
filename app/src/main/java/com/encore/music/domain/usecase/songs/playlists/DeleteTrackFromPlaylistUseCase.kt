package com.encore.music.domain.usecase.songs.playlists

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeleteTrackFromPlaylistUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(
        playlistId: String,
        trackId: String,
    ): Flow<Result<Boolean>> =
        flow {
            try {
                emit(Result.Loading())
                songsRepository.deleteTrackFromPlaylist(playlistId, trackId)
                emit(Result.Success(true))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }.flowOn(ioDispatcher)
}
