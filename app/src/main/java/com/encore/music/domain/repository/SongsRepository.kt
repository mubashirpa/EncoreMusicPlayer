package com.encore.music.domain.repository

import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.playlists.PlaylistEntity
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.playlists.PlaylistWithTracksAndArtists
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.data.local.entity.tracks.TrackWithArtists
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    /**
     * Artists
     */

    suspend fun insertFollowedArtist(artist: ArtistEntity)

    suspend fun updateFollowedArtist(artist: ArtistEntity)

    fun getFollowedArtists(): Flow<List<ArtistEntity>>

    fun getFollowedArtistById(artistId: String): Flow<ArtistEntity?>

    /**
     * Playlists
     */

    suspend fun insertPlaylist(
        playlist: PlaylistEntity,
        tracks: List<TrackEntity>? = null,
        artists: List<ArtistEntity>? = null,
        playlistTrackCrossRef: List<PlaylistTrackCrossRef>? = null,
        trackArtistCrossRef: List<TrackArtistCrossRef>? = null,
    )

    fun getPlaylistById(id: String): Flow<PlaylistEntity?>

    fun getPlaylistWithTracksAndArtistsById(id: String): Flow<PlaylistWithTracksAndArtists?>

    fun getPlaylists(): Flow<List<PlaylistEntity>>

    fun getLocalPlaylists(): Flow<List<PlaylistEntity>>

    suspend fun deletePlaylistWithCrossRefs(playlist: PlaylistEntity)

    /**
     * Tracks
     */

    suspend fun insertRecentTrack(
        track: TrackEntity,
        artists: List<ArtistEntity>,
        trackArtistCrossRef: List<TrackArtistCrossRef>,
    )

    fun getRecentTracks(limit: Int = 20): Flow<List<TrackWithArtists>>
}
