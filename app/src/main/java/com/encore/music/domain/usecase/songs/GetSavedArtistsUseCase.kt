package com.encore.music.domain.usecase.songs

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSavedArtistsUseCase(
    private val songsRepository: SongsRepository,
) {
    // TODO: Implement this
    operator fun invoke(): Flow<List<Artist>> =
        songsRepository.getPlaylists().map { artists ->
            artists.map { artist ->
                Artist(
                    id = artist.playlistId,
                    image = artist.image,
                    name = artist.name,
                    tracks = null,
                )
            }
        }
}
