package com.encore.music.presentation.ui.fragments.artist

import com.encore.music.domain.model.spotify.artists.Artist
import com.encore.music.domain.model.spotify.tracks.Track

sealed class ArtistListItem {
    data class HeaderItem(
        val artist: Artist,
    ) : ArtistListItem()

    data class TracksItem(
        val track: Track,
    ) : ArtistListItem()
}
