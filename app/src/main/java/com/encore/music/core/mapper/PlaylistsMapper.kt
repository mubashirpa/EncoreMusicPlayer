package com.encore.music.core.mapper

import com.encore.music.data.remote.dto.playlists.Playlist
import com.encore.music.data.remote.dto.playlists.PlaylistsDto
import com.encore.music.domain.model.playlists.Playlist as PlaylistModel

fun PlaylistsDto.toPlaylistsListModel(): List<PlaylistModel> = items.map { it.toPlaylistModel() }

fun Playlist.toPlaylistModel(): PlaylistModel =
    PlaylistModel(
        description = description,
        id = id,
        imageUrl = imageUrl,
        isPublic = isPublic,
        name = name,
        ownerDisplayName = ownerDisplayName,
        tracks = tracks,
    )
