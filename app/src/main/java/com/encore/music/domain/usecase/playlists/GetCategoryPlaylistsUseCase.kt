package com.encore.music.domain.usecase.playlists

import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toPlaylistsListModel
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.EncoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCategoryPlaylistsUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val encoreRepository: EncoreRepository,
) {
    operator fun invoke(
        locale: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): Flow<Result<List<Playlist>>> =
        flow {
            try {
                emit(Result.Loading())
                val idToken = authenticationRepository.getIdToken().orEmpty()
                val playlists =
                    encoreRepository
                        .getFeaturedPlaylists(idToken, locale, limit, offset)
                        .toPlaylistsListModel()
                emit(Result.Success(playlists))
            } catch (e: Exception) {
                emit(Result.Error(UiText.DynamicString(e.message.toString())))
            }
        }
}
