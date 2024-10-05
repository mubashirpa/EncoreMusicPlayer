package com.encore.music.domain.usecase.playlists

import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toPlaylistDomainModel
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPlaylistUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val encoreRepository: PlaylistsRepository,
) {
    operator fun invoke(playlistId: String): Flow<Result<Playlist>> =
        flow {
            try {
                emit(Result.Loading())
                val idToken = authenticationRepository.getIdToken().orEmpty()
                val playlists =
                    encoreRepository.getPlaylist(idToken, playlistId).toPlaylistDomainModel()
                emit(Result.Success(playlists))
            } catch (e: Exception) {
                emit(Result.Error(UiText.DynamicString(e.message.toString())))
            }
        }
}
