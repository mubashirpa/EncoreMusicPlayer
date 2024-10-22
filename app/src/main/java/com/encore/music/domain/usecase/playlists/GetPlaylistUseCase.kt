package com.encore.music.domain.usecase.playlists

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toPlaylistDomainModel
import com.encore.music.core.utils.KtorException
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException

class GetPlaylistUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val encoreRepository: PlaylistsRepository,
) {
    operator fun invoke(
        playlistId: String,
        market: String? = null,
        additionalTypes: String? = null,
    ): Flow<Result<Playlist>> =
        flow {
            try {
                emit(Result.Loading())
                val idToken = authenticationRepository.getIdToken().orEmpty()
                val playlists =
                    encoreRepository
                        .getPlaylist(idToken, playlistId, market, additionalTypes)
                        .toPlaylistDomainModel()
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
