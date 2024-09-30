package com.encore.music.domain.usecase.playlists

import com.encore.music.core.mapper.toPlaylists
import com.encore.music.domain.model.spotify.playlists.Playlist
import com.encore.music.domain.repository.EncoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFeaturedPlaylistsUseCase(
    private val repository: EncoreRepository,
) {
    operator fun invoke(
        accessToken: String,
        locale: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): Flow<List<Playlist>> =
        flow {
            try {
                val playlists =
                    repository
                        .getFeaturedPlaylists(accessToken, locale, limit, offset)
                        .toPlaylists()
                emit(playlists)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(emptyList())
            }
        }
}
