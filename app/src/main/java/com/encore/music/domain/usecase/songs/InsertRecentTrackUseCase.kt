package com.encore.music.domain.usecase.songs

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class InsertRecentTrackUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(track: Track): Flow<Result<String>> =
        flow {
            try {
                emit(Result.Loading())
                val trackId = track.id!!
                val artists: MutableList<ArtistEntity> = mutableListOf()
                val trackArtistCrossRef: MutableList<TrackArtistCrossRef> = mutableListOf()

                track.artists?.forEach { artist ->
                    val artistId = artist.id!!
                    artists.add(
                        ArtistEntity(
                            artistId = artistId,
                            image = artist.image,
                            name = artist.name,
                            followedAt = null,
                        ),
                    )
                    trackArtistCrossRef.add(
                        TrackArtistCrossRef(
                            trackId = trackId,
                            artistId = artistId,
                        ),
                    )
                }

                songsRepository.insertRecentTrack(
                    track =
                        TrackEntity(
                            trackId = trackId,
                            image = track.image,
                            name = track.name,
                            mediaUrl = track.mediaUrl,
                            lastPlayed = 0, // TODO: Get current time
                        ),
                    artists = artists,
                    trackArtistCrossRef = trackArtistCrossRef,
                )
                emit(Result.Success(trackId))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }.flowOn(ioDispatcher)
}
