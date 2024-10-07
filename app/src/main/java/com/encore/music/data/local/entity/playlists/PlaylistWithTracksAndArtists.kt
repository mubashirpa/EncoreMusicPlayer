package com.encore.music.data.local.entity.playlists

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.encore.music.data.local.entity.tracks.Track
import com.encore.music.data.local.entity.tracks.TrackWithArtists

data class PlaylistWithTracksAndArtists(
    @Embedded val playlist: Playlist,
    @Relation(
        entity = Track::class,
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistTrackCrossRef::class),
    )
    val tracks: List<TrackWithArtists>,
)
