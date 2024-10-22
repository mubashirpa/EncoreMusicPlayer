package com.encore.music.domain.usecase.playlists

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toHomePlaylistModel
import com.encore.music.core.utils.KtorException
import com.encore.music.domain.model.home.HomePlaylist
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException

class GetHomePlaylistsUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val encoreRepository: PlaylistsRepository,
) {
    operator fun invoke(
        locale: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): Flow<Result<List<HomePlaylist>>> =
        flow {
            try {
                emit(Result.Loading())
                val idToken = authenticationRepository.getIdToken().orEmpty()
                val playlists =
                    encoreRepository
                        .getHomePlaylists(idToken, locale, limit, offset)
                        .map { it.toHomePlaylistModel() }
                emit(Result.Success(playlists))
            } catch (e: ConnectException) {
                emit(Result.Error(UiText.StringResource(R.string.error_connect)))
            } catch (e: KtorException) {
                emit(Result.Error(e.localizedMessage))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
            }
        }
}
