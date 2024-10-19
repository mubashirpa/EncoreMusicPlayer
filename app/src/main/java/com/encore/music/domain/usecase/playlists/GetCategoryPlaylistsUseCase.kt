package com.encore.music.domain.usecase.playlists

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toPlaylistList
import com.encore.music.core.utils.NetworkException
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException

class GetCategoryPlaylistsUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val encoreRepository: PlaylistsRepository,
) {
    operator fun invoke(
        categoryId: String,
        limit: Int = 20,
        offset: Int = 0,
    ): Flow<Result<List<Playlist>>> =
        flow {
            try {
                emit(Result.Loading())
                val idToken = authenticationRepository.getIdToken().orEmpty()
                val playlists =
                    encoreRepository
                        .getCategoryPlaylists(idToken, categoryId, limit, offset)
                        .toPlaylistList()
                emit(Result.Success(playlists))
            } catch (e: ConnectException) {
                emit(Result.Error(UiText.StringResource(R.string.error_connect)))
            } catch (e: NetworkException) {
                emit(Result.Error(UiText.DynamicString(e.message.toString())))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
            }
        }
}
