package com.encore.music.presentation.ui.fragments.artist

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.tracks.Track

sealed class ArtistListItem(
    val id: String,
) {
    data class HeaderItem(
        val artist: Artist,
        var isFollowed: Boolean,
    ) : ArtistListItem(artist.id.orEmpty())

    data class TracksItem(
        val track: Track,
    ) : ArtistListItem(track.id.orEmpty())

    data object EmptyTracksItem : ArtistListItem("empty")
}
