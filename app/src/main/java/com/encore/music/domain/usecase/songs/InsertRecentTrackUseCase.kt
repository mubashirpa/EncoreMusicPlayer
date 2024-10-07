package com.encore.music.domain.usecase.songs

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InsertRecentTrackUseCase(
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(track: Track): Flow<Result<String>> =
        flow {
            try {
                emit(Result.Loading())
                val trackId = track.id!!
                songsRepository.insertRecentTrack(
                    track =
                        TrackEntity(
                            trackId = trackId,
                            image = track.image,
                            name = track.name,
                            mediaUrl = track.mediaUrl,
                            lastPlayed = 0, // TODO: Get current time
                        ),
                    trackArtistCrossRef =
                        track.artists
                            ?.map { artist ->
                                TrackArtistCrossRef(
                                    trackId = trackId,
                                    artistId = artist.id!!,
                                )
                            }.orEmpty(),
                )
                emit(Result.Success(trackId))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }
}
