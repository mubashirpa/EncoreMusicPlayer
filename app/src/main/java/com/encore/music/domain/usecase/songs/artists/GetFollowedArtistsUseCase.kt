package com.encore.music.domain.usecase.songs.artists

import com.encore.music.core.mapper.toArtistDomainModel
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFollowedArtistsUseCase(
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(): Flow<List<Artist>> =
        songsRepository.getFollowedArtists().map { artists ->
            artists.map { it.toArtistDomainModel() }
        }
}
