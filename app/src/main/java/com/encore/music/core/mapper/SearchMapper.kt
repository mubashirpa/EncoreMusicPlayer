package com.encore.music.core.mapper

import com.encore.music.data.remote.dto.search.SearchDto
import com.encore.music.domain.model.search.Search as SearchDomainModel

fun SearchDto.toSearchDomainModel(): SearchDomainModel =
    SearchDomainModel(
        tracks = tracks?.map { it.toTrackDomainModel() },
        artists = artists?.map { it.toArtistDomainModel() },
        playlists = playlists?.map { it.toPlaylistDomainModel() },
    )
