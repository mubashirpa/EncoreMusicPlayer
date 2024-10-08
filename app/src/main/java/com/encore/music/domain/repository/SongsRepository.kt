package com.encore.music.domain.repository

import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.playlists.PlaylistEntity
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.data.local.entity.tracks.TrackWithArtists
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    suspend fun insertPlaylist(
        playlist: PlaylistEntity,
        tracks: List<TrackEntity>? = null,
        artists: List<ArtistEntity>? = null,
        playlistTrackCrossRef: List<PlaylistTrackCrossRef>? = null,
        trackArtistCrossRef: List<TrackArtistCrossRef>? = null,
    )

    fun getPlaylists(): Flow<List<PlaylistEntity>>

    suspend fun insertRecentTrack(
        track: TrackEntity,
        artists: List<ArtistEntity>,
        trackArtistCrossRef: List<TrackArtistCrossRef>,
    )

    fun getRecentTracks(limit: Int = 20): Flow<List<TrackWithArtists>>

    suspend fun insertFollowedArtist(artist: ArtistEntity)

    fun getFollowedArtists(): Flow<List<ArtistEntity>>
}
