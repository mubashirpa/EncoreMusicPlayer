package com.encore.music.domain.usecase.songs.tracks

import androidx.paging.PagingData
import androidx.paging.map
import com.encore.music.core.mapper.toTrackDomainModel
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRecentTracksPagingUseCase(
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(limit: Int = 20): Flow<PagingData<Track>> =
        songsRepository.getRecentTracksPaging(limit).map { pagingData ->
            pagingData.map { it.toTrackDomainModel() }
        }
}
