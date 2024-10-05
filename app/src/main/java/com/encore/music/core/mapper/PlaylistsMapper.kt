package com.encore.music.core.mapper

import com.encore.music.data.remote.dto.playlists.Playlist
import com.encore.music.data.remote.dto.playlists.PlaylistsDto
import com.encore.music.domain.model.playlists.Playlist as PlaylistDomainModel

fun PlaylistsDto.toPlaylistList(): List<PlaylistDomainModel> = items?.map { it.toPlaylistDomainModel() } ?: emptyList()

fun Playlist.toPlaylistDomainModel(): PlaylistDomainModel =
    PlaylistDomainModel(
        description = description,
        id = id,
        image = image,
        name = name,
        owner = owner,
        tracks = tracks?.items?.map { it.toTrackDomainModel() },
    )
