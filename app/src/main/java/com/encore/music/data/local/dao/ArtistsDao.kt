package com.encore.music.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.encore.music.data.local.entity.artist.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistsDao {
    @Upsert
    suspend fun insertFollowedArtist(artist: ArtistEntity)

    @Update
    suspend fun updateFollowedArtist(artist: ArtistEntity)

    @Query("SELECT * FROM artists WHERE artistId = :artistId AND followedAt IS NOT NULL")
    fun getFollowedArtistById(artistId: String): Flow<ArtistEntity?>

    @Query("SELECT * FROM artists WHERE followedAt IS NOT NULL ORDER BY followedAt DESC LIMIT :limit")
    fun getFollowedArtists(limit: Int = 20): Flow<List<ArtistEntity>>
}
