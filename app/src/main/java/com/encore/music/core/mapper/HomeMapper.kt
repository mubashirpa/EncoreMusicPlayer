package com.encore.music.core.mapper

import com.encore.music.data.remote.dto.home.HomePlaylistDto
import com.encore.music.domain.model.home.HomePlaylist

fun HomePlaylistDto.toHomePlaylistModel(): HomePlaylist =
    HomePlaylist(
        title = title,
        playlists = playlists.map { it.toPlaylistDomainModel() },
    )
