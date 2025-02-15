package com.encore.music.domain.repository

import androidx.paging.PagingData
import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.playlists.PlaylistEntity
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.playlists.PlaylistWithTracksAndArtists
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.data.local.entity.tracks.TrackWithArtists
import com.encore.music.domain.model.tracks.Track
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    /**
     * Artists
     */

    suspend fun insertFollowedArtist(artist: ArtistEntity)

    suspend fun updateFollowedArtist(artist: ArtistEntity)

    fun getFollowedArtists(limit: Int = 20): Flow<List<ArtistEntity>>

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

    fun getPlaylists(limit: Int = 20): Flow<List<PlaylistEntity>>

    fun getLocalPlaylists(limit: Int = 20): Flow<List<PlaylistEntity>>

    suspend fun deletePlaylistWithCrossRefs(playlist: PlaylistEntity)

    suspend fun deleteTrackFromPlaylist(
        playlistId: String,
        trackId: String,
    )

    /**
     * Tracks
     */

    suspend fun insertRecentTrack(
        track: TrackEntity,
        artists: List<ArtistEntity>,
        trackArtistCrossRef: List<TrackArtistCrossRef>,
    )

    fun getRecentTracks(limit: Int = 20): Flow<List<TrackWithArtists>>

    fun getRecentTracksPaging(limit: Int = 20): Flow<PagingData<TrackWithArtists>>

    suspend fun getTrackFromStorage(): List<Track>
}
