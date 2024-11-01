package com.encore.music.data.local.entity.playlists

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val playlistId: String,
    val addedAt: Long?,
    val description: String?,
    val externalUrl: String?,
    val image: String?,
    val isLocal: Boolean?,
    val name: String?,
    val owner: String?,
    val ownerId: String?,
)
