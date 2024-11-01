package com.encore.music.data.local.entity.artist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey val artistId: String,
    val externalUrl: String?,
    val followedAt: Long?,
    val followers: Int?,
    val image: String?,
    val name: String?,
)
