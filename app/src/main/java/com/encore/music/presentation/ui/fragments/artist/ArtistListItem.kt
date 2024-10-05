package com.encore.music.presentation.ui.fragments.artist

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.tracks.Track

sealed class ArtistListItem {
    data class HeaderItem(
        val artist: Artist,
    ) : ArtistListItem()

    data class TracksItem(
        val track: Track,
    ) : ArtistListItem()
}
