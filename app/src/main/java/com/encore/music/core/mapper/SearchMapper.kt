package com.encore.music.core.mapper

import com.encore.music.data.remote.dto.search.SearchDto
import com.encore.music.data.remote.dto.search.SearchItem
import com.encore.music.domain.model.search.Search as SearchDomainModel
import com.encore.music.domain.model.search.SearchItem as SearchItemDomainModel

fun SearchDto.toSearchDomainModel(): SearchDomainModel =
    SearchDomainModel(
        tracks = tracks?.items?.map { it.toTrackDomainModel() },
        artists = artists?.items?.map { it.toArtistDomainModel() },
        playlists = playlists?.items?.map { it.toPlaylistDomainModel() },
    )

fun SearchItem.toSearchItemDomainModel(): SearchItemDomainModel =
    when (this) {
        is SearchItem.TrackItem -> SearchItemDomainModel.TrackItem(track.toTrackDomainModel())
        is SearchItem.ArtistItem -> SearchItemDomainModel.ArtistItem(artist.toArtistDomainModel())
        is SearchItem.PlaylistItem -> SearchItemDomainModel.PlaylistItem(playlist.toPlaylistDomainModel())
    }
