package com.encore.music.domain.usecase.playlists

import androidx.paging.PagingData
import androidx.paging.map
import com.encore.music.core.mapper.toTrackDomainModel
import com.encore.music.domain.model.tracks.Track
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPlaylistTracksUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val playlistsRepository: PlaylistsRepository,
) {
    suspend operator fun invoke(
        playlistId: String,
        market: String? = null,
        fields: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        additionalTypes: String? = null,
    ): Flow<PagingData<Track>> {
        val idToken = authenticationRepository.getIdToken().orEmpty()
        return playlistsRepository
            .getPlaylistTracks(
                accessToken = idToken,
                playlistId = playlistId,
                market = market,
                fields = fields,
                limit = limit,
                offset = offset,
                additionalTypes = additionalTypes,
            ).map { pagingData ->
                pagingData.map { it.toTrackDomainModel() }
            }
    }
}
