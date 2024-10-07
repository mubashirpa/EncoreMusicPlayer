package com.encore.music.data.local.entity.playlists

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val playlistId: String,
    val description: String?,
    val image: String?,
    val name: String?,
    val owner: String?,
    val ownerId: String?,
)
