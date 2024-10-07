package com.encore.music.domain.usecase.songs

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRecentTracksUseCase(
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(limit: Int = 20): Flow<List<Track>> =
        songsRepository.getRecentTracks(limit).map { tracksWithArtists ->
            tracksWithArtists.map { trackWithArtists ->
                Track(
                    artists =
                        trackWithArtists.artists.map { artist ->
                            Artist(
                                id = artist.artistId,
                                image = artist.image,
                                name = artist.name,
                            )
                        },
                    id = trackWithArtists.track.trackId,
                    image = trackWithArtists.track.image,
                    name = trackWithArtists.track.name,
                    mediaUrl = trackWithArtists.track.mediaUrl,
                )
            }
        }
}
