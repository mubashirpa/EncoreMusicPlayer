package com.encore.music.data.local.entity.tracks

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.encore.music.data.local.entity.artist.ArtistEntity

data class TrackWithArtists(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "artistId",
        associateBy = Junction(TrackArtistCrossRef::class),
    )
    val artists: List<ArtistEntity>,
)
