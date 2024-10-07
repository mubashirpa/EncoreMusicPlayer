package com.encore.music.domain.usecase.songs

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.data.local.entity.playlists.PlaylistEntity
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.encore.music.domain.model.playlists.Playlist as PlaylistDomainModel

class InsertPlaylistUseCase(
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(playlist: PlaylistDomainModel): Flow<Result<String>> =
        flow {
            try {
                emit(Result.Loading())

                val playlistId = playlist.id!!
                val playlistTrackCrossRefs: MutableList<PlaylistTrackCrossRef> = mutableListOf()
                val trackArtistCrossRefs: MutableList<TrackArtistCrossRef> = mutableListOf()
                playlist.tracks?.forEach { track ->
                    val trackId = track.id!!
                    playlistTrackCrossRefs.add(
                        PlaylistTrackCrossRef(
                            playlistId = playlistId,
                            trackId = trackId,
                        ),
                    )
                    track.artists?.forEach { artist ->
                        trackArtistCrossRefs.add(
                            TrackArtistCrossRef(
                                trackId = trackId,
                                artistId = artist.id!!,
                            ),
                        )
                    }
                }

                songsRepository.insertPlaylist(
                    playlist =
                        PlaylistEntity(
                            playlistId = playlistId,
                            description = playlist.description,
                            image = playlist.image,
                            name = playlist.name,
                            owner = playlist.owner,
                            ownerId = "", // TODO: Get current user id
                        ),
                    playlistTrackCrossRef = playlistTrackCrossRefs.ifEmpty { null },
                    trackArtistCrossRef = trackArtistCrossRefs.ifEmpty { null },
                )

                emit(Result.Success(playlistId))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }
}
