package com.encore.music.domain.usecase.songs

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toArtistEntity
import com.encore.music.core.mapper.toPlaylistEntity
import com.encore.music.core.mapper.toTrackEntity
import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.encore.music.domain.model.playlists.Playlist as PlaylistDomainModel

class InsertPlaylistUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(playlist: PlaylistDomainModel): Flow<Result<String>> =
        flow {
            try {
                emit(Result.Loading())
                val playlistId = playlist.id!!
                val trackEntities: MutableList<TrackEntity> = mutableListOf()
                val artistEntities: MutableList<ArtistEntity> = mutableListOf()
                val playlistTrackCrossRefs: MutableList<PlaylistTrackCrossRef> = mutableListOf()
                val trackArtistCrossRefs: MutableList<TrackArtistCrossRef> = mutableListOf()

                playlist.tracks?.forEach { track ->
                    val trackId = track.id!!

                    trackEntities.add(track.toTrackEntity())
                    playlistTrackCrossRefs.add(
                        PlaylistTrackCrossRef(
                            playlistId = playlistId,
                            trackId = trackId,
                        ),
                    )

                    track.artists?.forEach { artist ->
                        artistEntities.add(artist.toArtistEntity())
                        trackArtistCrossRefs.add(
                            TrackArtistCrossRef(
                                trackId = trackId,
                                artistId = artist.id!!,
                            ),
                        )
                    }
                }

                songsRepository.insertPlaylist(
                    playlist = playlist.toPlaylistEntity(),
                    tracks = trackEntities.ifEmpty { null },
                    artists = artistEntities.ifEmpty { null },
                    playlistTrackCrossRef = playlistTrackCrossRefs.ifEmpty { null },
                    trackArtistCrossRef = trackArtistCrossRefs.ifEmpty { null },
                )
                emit(Result.Success(playlistId))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }.flowOn(ioDispatcher)
}
