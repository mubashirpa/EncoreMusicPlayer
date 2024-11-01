package com.encore.music.data.local.entity.tracks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val trackId: String,
    val externalUrl: String? = null,
    val image: String?,
    val lastPlayed: Long?,
    val mediaUrl: String?,
    val name: String?,
)
