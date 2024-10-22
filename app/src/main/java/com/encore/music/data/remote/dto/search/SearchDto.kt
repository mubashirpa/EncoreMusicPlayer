package com.encore.music.data.remote.dto.search

import com.encore.music.data.remote.dto.artists.ArtistsDto
import com.encore.music.data.remote.dto.playlists.PlaylistsDto
import com.encore.music.data.remote.dto.tracks.TracksDto
import kotlinx.serialization.Serializable

@Serializable
data class SearchDto(
    val tracks: TracksDto? = null,
    val artists: ArtistsDto? = null,
    val playlists: PlaylistsDto? = null,
)
