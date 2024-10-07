package com.encore.music.data.repository

import com.encore.music.data.local.dao.SongsDao
import com.encore.music.data.local.entity.playlists.Playlist
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow

class SongsRepositoryImpl(
    private val songsDao: SongsDao,
) : SongsRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        songsDao.insertPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = songsDao.getPlaylists()
}
