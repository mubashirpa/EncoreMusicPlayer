package com.encore.music.data.local.entity.tracks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey val trackId: String,
    val image: String?,
    val name: String?,
    val mediaUrl: String?,
)
