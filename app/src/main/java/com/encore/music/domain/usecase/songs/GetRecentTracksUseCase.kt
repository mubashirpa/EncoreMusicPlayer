package com.encore.music.domain.usecase.songs

import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRecentTracksUseCase(
    private val songsRepository: SongsRepository,
) {
    // TODO: Implement this
    operator fun invoke(): Flow<List<Track>> =
        songsRepository.getPlaylists().map { tracks ->
            tracks.map { track ->
                Track(
                    artists = null,
                    id = track.playlistId,
                    image = track.image,
                    name = track.name,
                    mediaUrl = null,
                )
            }
        }
}
