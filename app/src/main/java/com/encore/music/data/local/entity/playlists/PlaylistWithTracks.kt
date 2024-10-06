package com.encore.music.data.local.entity.playlists

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.encore.music.data.local.entity.tracks.Track

data class PlaylistWithTracks(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistTrackCrossRef::class),
    )
    val tracks: List<Track>,
)
