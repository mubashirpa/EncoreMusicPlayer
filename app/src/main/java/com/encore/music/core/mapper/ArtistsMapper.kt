package com.encore.music.core.mapper

import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.remote.dto.artists.Artist
import com.encore.music.data.remote.dto.artists.ArtistsDto
import com.encore.music.domain.model.artists.Artist as ArtistDomainModel

fun ArtistsDto.toArtistList(): List<ArtistDomainModel> = items?.map { it.toArtistDomainModel() } ?: emptyList()

fun Artist.toArtistDomainModel(): ArtistDomainModel =
    ArtistDomainModel(
        id = id,
        image = image,
        name = name,
        tracks = tracks?.map { it.toTrackDomainModel() },
    )

// Entities

fun ArtistDomainModel.toArtistEntity(followAt: Long? = null): ArtistEntity =
    ArtistEntity(
        artistId = id!!,
        followedAt = followAt,
        image = image,
        name = name,
    )

fun ArtistEntity.toArtistDomainModel(): ArtistDomainModel =
    ArtistDomainModel(
        id = artistId,
        image = image,
        name = name,
    )
