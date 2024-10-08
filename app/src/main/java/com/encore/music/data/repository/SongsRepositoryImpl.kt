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
    override suspend fun insertPlaylist(
        playlist: PlaylistEntity,
        tracks: List<TrackEntity>?,
        artists: List<ArtistEntity>?,
        playlistTrackCrossRef: List<PlaylistTrackCrossRef>?,
        trackArtistCrossRef: List<TrackArtistCrossRef>?,
    ) {
        playlistsDao.insertPlaylist(playlist)
        tracks?.let { playlistsDao.insertTracks(it) }
        artists?.let { playlistsDao.insertArtists(it) }
        playlistTrackCrossRef?.let { playlistsDao.insertPlaylistTrackCrossRef(it) }
        trackArtistCrossRef?.let { playlistsDao.insertTrackArtistCrossRef(it) }
    }

    override fun getPlaylists(): Flow<List<PlaylistEntity>> = playlistsDao.getPlaylists()

    override fun getPlaylistWithTracksAndArtistsById(id: String): Flow<PlaylistWithTracksAndArtists> =
        playlistsDao.getPlaylistWithTracksAndArtistsById(id)

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

    override suspend fun insertFollowedArtist(artist: ArtistEntity) {
        artistsDao.insertFollowedArtist(artist)
    }

    override fun getFollowedArtists(): Flow<List<ArtistEntity>> = artistsDao.getFollowedArtists()
}
