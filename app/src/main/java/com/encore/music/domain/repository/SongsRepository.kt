package com.encore.music.domain.repository

import com.encore.music.data.local.entity.playlists.Playlist
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    suspend fun insertPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>
}
