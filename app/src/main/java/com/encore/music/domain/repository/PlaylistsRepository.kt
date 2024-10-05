package com.encore.music.domain.repository

import com.encore.music.data.remote.dto.home.HomePlaylistDto
import com.encore.music.data.remote.dto.playlists.Playlist
import com.encore.music.data.remote.dto.playlists.PlaylistsDto

interface PlaylistsRepository {
    suspend fun getPlaylist(
        accessToken: String,
        playlistId: String,
    ): Playlist

    suspend fun getCategoryPlaylists(
        accessToken: String,
        categoryId: String,
        limit: Int = 20,
        offset: Int = 0,
    ): PlaylistsDto

    suspend fun getHomePlaylists(accessToken: String): List<HomePlaylistDto>
}
