package com.encore.music.core.mapper

import com.encore.music.data.local.entity.tracks.TrackEntity
import com.encore.music.data.local.entity.tracks.TrackWithArtists
import com.encore.music.data.remote.dto.tracks.Track
import com.encore.music.data.remote.dto.tracks.TracksDto
import com.encore.music.domain.model.tracks.Track as TrackDomainModel

fun TracksDto.toTrackList(): List<TrackDomainModel> = items?.map { it.toTrackDomainModel() } ?: emptyList()

fun Track.toTrackDomainModel(): TrackDomainModel =
    TrackDomainModel(
        artists = artists?.map { it.toArtistDomainModel() },
        id = id,
        image = image,
        name = name,
        mediaUrl = mediaUrl,
    )

// Entities

fun TrackDomainModel.toTrackEntity(lastPlayed: Long? = null): TrackEntity =
    TrackEntity(
        trackId = id!!,
        image = image,
        lastPlayed = lastPlayed,
        mediaUrl = mediaUrl,
        name = name,
    )

fun TrackWithArtists.toTrackDomainModel(): TrackDomainModel =
    TrackDomainModel(
        artists = artists.map { it.toArtistDomainModel() },
        id = track.trackId,
        image = track.image,
        name = track.name,
        mediaUrl = track.mediaUrl,
    )
