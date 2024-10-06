package com.encore.music.data.local.entity.artist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey val artistId: String,
    val image: String? = null,
    val name: String? = null,
)
