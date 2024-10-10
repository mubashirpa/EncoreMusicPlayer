package com.encore.music.domain.usecase.songs

import com.encore.music.core.mapper.toPlaylistDomainModel
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSavedPlaylistUseCase(
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(id: String): Flow<Playlist?> = songsRepository.getPlaylistById(id).map { it?.toPlaylistDomainModel() }
}
