package com.encore.music.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.data.local.entity.tracks.TrackWithArtists
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {
    @Upsert
    suspend fun insertRecentTrack(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Upsert
    fun insertTrackArtistCrossRef(crossRefs: List<TrackArtistCrossRef>)

    @Transaction
    @Query("SELECT * FROM tracks WHERE lastPlayed IS NOT NULL ORDER BY lastPlayed DESC LIMIT :limit")
    fun getRecentTracks(limit: Int = 20): Flow<List<TrackWithArtists>>
}
