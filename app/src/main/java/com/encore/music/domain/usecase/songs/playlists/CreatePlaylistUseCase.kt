package com.encore.music.domain.usecase.songs.playlists

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toArtistEntity
import com.encore.music.core.mapper.toTrackEntity
import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock
import java.util.UUID
import com.encore.music.data.local.entity.playlists.PlaylistEntity as PlaylistEntity

class CreatePlaylistUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val authenticationRepository: AuthenticationRepository,
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(playlist: Playlist): Flow<Result<String>> =
        flow {
            try {
                emit(Result.Loading())
                val currentUser = authenticationRepository.currentUser.first()
                currentUser?.let { user ->
                    val playlistEntity =
                        PlaylistEntity(
                            playlistId = playlist.id ?: UUID.randomUUID().toString(),
                            addedAt = Clock.System.now().toEpochMilliseconds(),
                            description = playlist.description?.ifBlank { null },
                            externalUrl = playlist.externalUrl?.ifBlank { null },
                            isLocal = playlist.isLocal ?: true,
                            image = playlist.image ?: playlist.tracks?.firstOrNull()?.image,
                            name = playlist.name,
                            owner = playlist.owner ?: user.name,
                            ownerId = playlist.ownerId ?: user.id,
                        )
                    val trackEntities: MutableList<TrackEntity> = mutableListOf()
                    val artistEntities: MutableList<ArtistEntity> = mutableListOf()
                    val playlistTrackCrossRefs: MutableList<PlaylistTrackCrossRef> = mutableListOf()
                    val trackArtistCrossRefs: MutableList<TrackArtistCrossRef> = mutableListOf()

                    playlist.tracks?.forEach { track ->
                        val trackId = track.id!!

                        trackEntities.add(track.toTrackEntity())
                        playlistTrackCrossRefs.add(
                            PlaylistTrackCrossRef(
                                playlistId = playlistEntity.playlistId,
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
                        playlist = playlistEntity,
                        tracks = trackEntities,
                        artists = artistEntities,
                        playlistTrackCrossRef = playlistTrackCrossRefs,
                        trackArtistCrossRef = trackArtistCrossRefs,
                    )
                    emit(Result.Success(playlistEntity.playlistId))
                } ?: emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }.flowOn(ioDispatcher)
}
