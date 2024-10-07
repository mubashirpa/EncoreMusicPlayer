package com.encore.music.data.local.entity.tracks

import androidx.room.Entity

@Entity(primaryKeys = ["trackId", "artistId"])
data class TrackArtistCrossRef(
    val trackId: String,
    val artistId: String,
)
