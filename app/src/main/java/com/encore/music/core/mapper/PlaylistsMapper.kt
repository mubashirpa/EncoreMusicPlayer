package com.encore.music.core.mapper

import com.encore.music.data.local.entity.playlists.PlaylistEntity
import com.encore.music.data.local.entity.playlists.PlaylistWithTracksAndArtists
import com.encore.music.data.remote.dto.playlists.Playlist
import com.encore.music.data.remote.dto.playlists.PlaylistsDto
import com.encore.music.domain.model.playlists.Playlist as PlaylistDomainModel

fun PlaylistsDto.toPlaylistList(): List<PlaylistDomainModel> = items?.map { it.toPlaylistDomainModel() } ?: emptyList()

fun Playlist.toPlaylistDomainModel(): PlaylistDomainModel =
    PlaylistDomainModel(
        description = description,
        externalUrl = externalUrl,
        id = id,
        image = image,
        isLocal = isLocal,
        name = name,
        owner = owner,
        ownerId = ownerId,
        tracks = tracks?.map { it.toTrackDomainModel() },
    )

// Entities

fun PlaylistDomainModel.toPlaylistEntity(addedAt: Long? = null): PlaylistEntity =
    PlaylistEntity(
        playlistId = id!!,
        addedAt = addedAt,
        description = description,
        externalUrl = externalUrl,
        image = image ?: tracks?.firstOrNull()?.image,
        isLocal = isLocal,
        name = name,
        owner = owner,
        ownerId = ownerId,
    )

fun PlaylistEntity.toPlaylistDomainModel(): PlaylistDomainModel =
    PlaylistDomainModel(
        description = description,
        externalUrl = externalUrl,
        id = playlistId,
        image = image,
        isLocal = isLocal,
        name = name,
        owner = owner,
        ownerId = ownerId,
    )

fun PlaylistWithTracksAndArtists.toPlaylistDomainModel(): PlaylistDomainModel =
    PlaylistDomainModel(
        description = playlist.description,
        externalUrl = playlist.externalUrl,
        id = playlist.playlistId,
        image = playlist.image,
        isLocal = playlist.isLocal,
        name = playlist.name,
        owner = playlist.owner,
        ownerId = playlist.ownerId,
        tracks = tracks.map { it.toTrackDomainModel() },
    )
