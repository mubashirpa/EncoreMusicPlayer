package com.encore.music.data.local.entity.tracks

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["trackId", "artistId"],
    indices = [Index(value = ["artistId"])],
)
data class TrackArtistCrossRef(
    val trackId: String,
    val artistId: String,
)
