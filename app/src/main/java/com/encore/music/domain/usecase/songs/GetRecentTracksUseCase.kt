package com.encore.music.domain.usecase.songs

import com.encore.music.core.mapper.toTrackDomainModel
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRecentTracksUseCase(
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(limit: Int = 20): Flow<List<Track>> =
        songsRepository.getRecentTracks(limit).map { tracksWithArtists ->
            tracksWithArtists.map { it.toTrackDomainModel() }
        }
}
