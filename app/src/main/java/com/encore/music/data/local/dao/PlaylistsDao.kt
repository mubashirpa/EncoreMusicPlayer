package com.encore.music.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.playlists.PlaylistEntity
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.playlists.PlaylistWithTracksAndArtists
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Upsert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Upsert
    suspend fun insertPlaylistTrackCrossRef(crossRefs: List<PlaylistTrackCrossRef>)

    @Upsert
    suspend fun insertTrackArtistCrossRef(crossRefs: List<TrackArtistCrossRef>)

    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    fun getPlaylistById(id: String): Flow<PlaylistEntity?>

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    fun getPlaylistWithTracksAndArtistsById(id: String): Flow<PlaylistWithTracksAndArtists?>

    @Query("SELECT * FROM playlists ORDER BY addedAt DESC LIMIT :limit")
    fun getPlaylists(limit: Int = 20): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE isLocal = 1 ORDER BY addedAt DESC LIMIT :limit")
    fun getLocalPlaylists(limit: Int = 20): Flow<List<PlaylistEntity>>

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM TrackArtistCrossRef WHERE trackId IN (SELECT trackId FROM PlaylistTrackCrossRef WHERE playlistId = :playlistId)")
    suspend fun deleteTrackArtistCrossRefByPlaylistId(playlistId: String)

    @Query("DELETE FROM PlaylistTrackCrossRef WHERE playlistId = :playlistId")
    suspend fun deletePlaylistTrackCrossRefByPlaylistId(playlistId: String)

    @Query("DELETE FROM PlaylistTrackCrossRef WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deletePlaylistTrackCrossRefByIds(
        playlistId: String,
        trackId: String,
    )

    @Transaction
    suspend fun insertPlaylistWithTracksAndArtists(
        playlist: PlaylistEntity,
        tracks: List<TrackEntity>? = null,
        artists: List<ArtistEntity>? = null,
        playlistTrackCrossRefs: List<PlaylistTrackCrossRef>? = null,
        trackArtistCrossRefs: List<TrackArtistCrossRef>? = null,
    ) {
        insertPlaylist(playlist)
        if (!tracks.isNullOrEmpty()) {
            insertTracks(tracks)
        }
        if (!artists.isNullOrEmpty()) {
            insertArtists(artists)
        }
        if (!playlistTrackCrossRefs.isNullOrEmpty()) {
            insertPlaylistTrackCrossRef(playlistTrackCrossRefs)
        }
        if (!trackArtistCrossRefs.isNullOrEmpty()) {
            insertTrackArtistCrossRef(trackArtistCrossRefs)
        }
    }

    @Transaction
    suspend fun deletePlaylistWithCrossRefs(playlist: PlaylistEntity) {
        // No need to delete the Track-Artist cross-references since tracks won't deleted
        // Delete the Track-Artist cross-references first
        // deleteTrackArtistCrossRefByPlaylistId(playlist.playlistId)

        // Delete the Playlist-Track cross-references
        deletePlaylistTrackCrossRefByPlaylistId(playlist.playlistId)
        // Finally, delete the playlist itself
        deletePlaylist(playlist)
    }
}
