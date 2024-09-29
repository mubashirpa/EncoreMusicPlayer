package com.encore.music.core.mapper

import com.encore.music.data.remote.dto.playlists.PlaylistsDto
import com.encore.music.domain.model.playlists.Playlist

fun PlaylistsDto.toPlaylists(): List<Playlist> =
    items.map {
        Playlist(
            description = it.description,
            id = it.id,
            imageUrl = it.imageUrl,
            isPublic = it.isPublic,
            name = it.name,
            ownerDisplayName = it.ownerDisplayName,
            tracks = it.tracks,
        )
    }
