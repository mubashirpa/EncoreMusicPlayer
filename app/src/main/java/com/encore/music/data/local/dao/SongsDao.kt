package com.encore.music.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.encore.music.data.local.entity.artist.Artist
import com.encore.music.data.local.entity.playlists.Playlist
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.playlists.PlaylistWithTracks
import com.encore.music.data.local.entity.playlists.PlaylistWithTracksAndArtists
import com.encore.music.data.local.entity.tracks.Track
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface SongsDao {
    @Upsert
    suspend fun insertPlaylist(playlist: Playlist)

    @Upsert
    suspend fun insertTracks(tracks: List<Track>)

    @Upsert
    suspend fun insertArtists(artists: List<Artist>)

    @Upsert
    suspend fun insertPlaylistTrackCrossRef(crossRefs: List<PlaylistTrackCrossRef>)

    @Upsert
    suspend fun insertTrackArtistCrossRef(crossRefs: List<TrackArtistCrossRef>)

    @Query("SELECT * FROM playlists")
    fun getPlaylists(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM playlists where playlistId = :id")
    fun getPlaylistWithTracksById(id: String): Flow<PlaylistWithTracks>

    @Transaction
    @Query("SELECT * FROM playlists where playlistId = :id")
    fun getPlaylistWithTracksAndArtistsById(id: String): Flow<PlaylistWithTracksAndArtists>
}
