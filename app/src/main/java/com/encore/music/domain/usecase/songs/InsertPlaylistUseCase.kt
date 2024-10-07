package com.encore.music.domain.usecase.songs

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.data.local.dao.SongsDao
import com.encore.music.data.local.entity.artist.Artist
import com.encore.music.data.local.entity.playlists.Playlist
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.tracks.Track
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.encore.music.domain.model.playlists.Playlist as PlaylistDomainModel

class InsertPlaylistUseCase(
    private val playlistDao: SongsDao,
) {
    operator fun invoke(playlist: PlaylistDomainModel): Flow<Result<String>> =
        flow {
            emit(Result.Loading())
            val playlistId = playlist.id!!
            val playlistEntity =
                Playlist(
                    playlistId = playlistId,
                    description = playlist.description,
                    image = playlist.image,
                    name = playlist.name,
                    owner = playlist.owner,
                    ownerId = "", // TODO
                )
            val trackEntities: MutableList<Track> = mutableListOf()
            val artistEntities: MutableList<Artist> = mutableListOf()
            val playlistTrackCrossRefEntities: MutableList<PlaylistTrackCrossRef> = mutableListOf()
            val trackArtistCrossRefEntities: MutableList<TrackArtistCrossRef> = mutableListOf()

            playlist.tracks?.forEach { track ->
                val trackId = track.id!!

                trackEntities.add(
                    Track(
                        trackId = trackId,
                        image = track.image,
                        name = track.name,
                        mediaUrl = track.mediaUrl,
                    ),
                )
                playlistTrackCrossRefEntities.add(
                    PlaylistTrackCrossRef(
                        playlistId = playlistId,
                        trackId = trackId,
                    ),
                )

                track.artists?.forEach { artist ->
                    val artistId = artist.id!!

                    artistEntities.add(
                        Artist(
                            artistId = artistId,
                            image = artist.image,
                            name = artist.name,
                        ),
                    )
                    trackArtistCrossRefEntities.add(
                        TrackArtistCrossRef(
                            trackId = trackId,
                            artistId = artistId,
                        ),
                    )
                }
            }

            try {
                playlistDao.insertPlaylist(playlistEntity)
                playlistDao.insertTracks(trackEntities)
                playlistDao.insertArtists(artistEntities)
                playlistDao.insertPlaylistTrackCrossRef(playlistTrackCrossRefEntities)
                playlistDao.insertTrackArtistCrossRef(trackArtistCrossRefEntities)
                emit(Result.Success(playlistEntity.playlistId))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }
}
