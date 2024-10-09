package com.encore.music.domain.repository

import com.encore.music.data.remote.dto.artists.Artist

interface ArtistsRepository {
    suspend fun getArtistTopTracks(
        accessToken: String,
        artistId: String,
        market: String? = null,
    ): Artist
}
