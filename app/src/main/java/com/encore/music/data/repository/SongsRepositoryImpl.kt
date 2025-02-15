package com.encore.music.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.encore.music.data.local.ContentResolverHelper
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
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SongsRepositoryImpl(
    private val artistsDao: ArtistsDao,
    private val playlistsDao: PlaylistsDao,
    private val tracksDao: TracksDao,
    private val contentResolver: ContentResolverHelper,
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

    override fun getRecentTracksPaging(limit: Int): Flow<PagingData<TrackWithArtists>> =
        Pager(
            config = PagingConfig(pageSize = limit, initialLoadSize = limit),
            pagingSourceFactory = {
                tracksDao.getRecentTracksPaging()
            },
        ).flow

    override suspend fun getTrackFromStorage(): List<Track> =
        withContext(Dispatchers.IO) {
            contentResolver.getAudioData()
        }
}
