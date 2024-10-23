package com.encore.music.domain.usecase.playlists

import androidx.paging.PagingData
import androidx.paging.map
import com.encore.music.core.mapper.toPlaylistDomainModel
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategoryPlaylistsUseCase(
    private val playlistsRepository: PlaylistsRepository,
) {
    operator fun invoke(
        categoryId: String,
        limit: Int = 20,
        offset: Int = 0,
    ): Flow<PagingData<Playlist>> =
        playlistsRepository
            .getCategoryPlaylists(
                categoryId = categoryId,
                limit = limit,
                offset = offset,
            ).map { pagingData ->
                pagingData.map { it.toPlaylistDomainModel() }
            }
}
