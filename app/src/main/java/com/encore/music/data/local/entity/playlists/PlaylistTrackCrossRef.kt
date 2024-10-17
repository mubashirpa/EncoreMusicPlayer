package com.encore.music.data.local.entity.playlists

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["playlistId", "trackId"],
    indices = [Index(value = ["trackId"])],
)
data class PlaylistTrackCrossRef(
    val playlistId: String,
    val trackId: String,
)
