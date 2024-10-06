package com.encore.music.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.encore.music.data.local.entity.playlists.Playlist
import com.encore.music.data.local.entity.playlists.PlaylistWithTracks
import com.encore.music.data.local.entity.tracks.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Upsert
    suspend fun insertPlaylist(playlist: Playlist)

    @Upsert
    suspend fun insertTracks(tracks: List<Track>)

    @Transaction
    @Query("SELECT * FROM playlists")
    fun getPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>>
}
