package com.encore.music.data.repository

import com.encore.music.data.local.dao.ArtistsDao
import com.encore.music.data.local.dao.PlaylistsDao
import com.encore.music.data.local.dao.TracksDao
import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.playlists.PlaylistEntity
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.playlists.PlaylistWithTracksAndArtists
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.data.local.entity.tracks.TrackWithArtists
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow

class SongsRepositoryImpl(
    private val artistsDao: ArtistsDao,
    private val playlistsDao: PlaylistsDao,
    private val tracksDao: TracksDao,
) : SongsRepository {
    /**
     * Artists
     */

    override suspend fun insertFollowedArtist(artist: ArtistEntity) {
        artistsDao.insertFollowedArtist(artist)
    }

    override suspend fun updateFollowedArtist(artist: ArtistEntity) {
        artistsDao.updateFollowedArtist(artist)
    }

    override fun getFollowedArtists(limit: Int): Flow<List<ArtistEntity>> = artistsDao.getFollowedArtists(limit)

    override fun getFollowedArtistById(artistId: String): Flow<ArtistEntity?> = artistsDao.getFollowedArtistById(artistId)

    /**
     * Playlists
     */

    override suspend fun insertPlaylist(
        playlist: PlaylistEntity,
        tracks: List<TrackEntity>?,
        artists: List<ArtistEntity>?,
        playlistTrackCrossRef: List<PlaylistTrackCrossRef>?,
        trackArtistCrossRef: List<TrackArtistCrossRef>?,
    ) {
        playlistsDao.insertPlaylistWithTracksAndArtists(
            playlist = playlist,
            tracks = tracks,
            artists = artists,
            playlistTrackCrossRefs = playlistTrackCrossRef,
            trackArtistCrossRefs = trackArtistCrossRef,
        )
    }

    override fun getPlaylistById(id: String): Flow<PlaylistEntity?> = playlistsDao.getPlaylistById(id)

    override fun getPlaylistWithTracksAndArtistsById(id: String): Flow<PlaylistWithTracksAndArtists?> =
        playlistsDao.getPlaylistWithTracksAndArtistsById(id)

    override fun getPlaylists(limit: Int): Flow<List<PlaylistEntity>> = playlistsDao.getPlaylists(limit)

    override fun getLocalPlaylists(limit: Int): Flow<List<PlaylistEntity>> = playlistsDao.getLocalPlaylists(limit)

    override suspend fun deletePlaylistWithCrossRefs(playlist: PlaylistEntity) {
        playlistsDao.deletePlaylistWithCrossRefs(playlist)
    }

    override suspend fun deleteTrackFromPlaylist(
        playlistId: String,
        trackId: String,
    ) {
        playlistsDao.deletePlaylistTrackCrossRefByIds(playlistId, trackId)
    }

    /**
     * Tracks
     */

    override suspend fun insertRecentTrack(
        track: TrackEntity,
        artists: List<ArtistEntity>,
        trackArtistCrossRef: List<TrackArtistCrossRef>,
    ) {
        tracksDao.insertRecentTrack(track)
        tracksDao.insertArtists(artists)
        tracksDao.insertTrackArtistCrossRef(trackArtistCrossRef)
    }

    override fun getRecentTracks(limit: Int): Flow<List<TrackWithArtists>> = tracksDao.getRecentTracks(limit)
}
