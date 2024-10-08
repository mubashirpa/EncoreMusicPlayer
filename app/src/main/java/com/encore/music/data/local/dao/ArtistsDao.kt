package com.encore.music.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.encore.music.data.local.entity.artist.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistsDao {
    @Upsert
    suspend fun insertFollowedArtist(artist: ArtistEntity)

    @Query("SELECT * FROM artists WHERE followedAt IS NOT NULL ORDER BY followedAt DESC")
    fun getFollowedArtists(): Flow<List<ArtistEntity>>
}