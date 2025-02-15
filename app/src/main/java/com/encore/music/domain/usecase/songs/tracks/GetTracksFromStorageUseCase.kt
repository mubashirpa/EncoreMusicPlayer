package com.encore.music.domain.usecase.songs.tracks

import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.repository.SongsRepository

class GetTracksFromStorageUseCase(
    private val songsRepository: SongsRepository,
) {
    suspend operator fun invoke(): List<Track> = songsRepository.getTrackFromStorage()
}
