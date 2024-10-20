package com.encore.music.domain.repository

import androidx.paging.PagingData
import com.encore.music.data.remote.dto.home.HomePlaylistDto
import com.encore.music.data.remote.dto.playlists.Playlist
import com.encore.music.data.remote.dto.playlists.PlaylistsDto
import com.encore.music.data.remote.dto.tracks.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun getPlaylist(
        accessToken: String,
        playlistId: String,
        market: String? = null,
        additionalTypes: String? = null,
    ): Playlist

    suspend fun getCategoryPlaylists(
        accessToken: String,
        categoryId: String,
        limit: Int = 20,
        offset: Int = 0,
    ): PlaylistsDto

    suspend fun getHomePlaylists(
        accessToken: String,
        locale: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<HomePlaylistDto>

    fun getPlaylistTracks(
        accessToken: String,
        playlistId: String,
        market: String? = null,
        fields: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        additionalTypes: String? = null,
    ): Flow<PagingData<Track>>
}
