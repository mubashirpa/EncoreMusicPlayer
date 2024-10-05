package com.encore.music.core.mapper

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
