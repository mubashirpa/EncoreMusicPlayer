package com.encore.music.core.mapper

import com.encore.music.data.remote.dto.tracks.Track
import com.encore.music.data.remote.dto.tracks.TracksDto
import com.encore.music.domain.model.tracks.Track as TrackDomainModel

fun TracksDto.toTrackList(): List<TrackDomainModel> = items?.map { it.toTrackDomainModel() } ?: emptyList()

fun Track.toTrackDomainModel(): TrackDomainModel =
    TrackDomainModel(
        artists = artists?.items?.map { it.toArtistDomainModel() },
        id = id,
        image = image,
        name = name,
        mediaUrl = mediaUrl,
    )
