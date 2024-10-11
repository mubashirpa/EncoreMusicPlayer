package com.encore.music.domain.usecase.songs.playlists

import com.encore.music.core.mapper.toPlaylistDomainModel
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSavedPlaylistsUseCase(
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(): Flow<List<Playlist>> =
        songsRepository.getPlaylists().map { playlists ->
            playlists.map { it.toPlaylistDomainModel() }
        }
}
