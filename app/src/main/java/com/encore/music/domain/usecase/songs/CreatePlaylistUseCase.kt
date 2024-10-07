package com.encore.music.domain.usecase.songs

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID
import com.encore.music.data.local.entity.playlists.PlaylistEntity as PlaylistEntity

class CreatePlaylistUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(
        name: String,
        description: String? = null,
        image: String? = null,
    ): Flow<Result<String>> =
        flow {
            try {
                emit(Result.Loading())
                val currentUser = authenticationRepository.currentUser.first()
                currentUser?.let { user ->
                    val playlistEntity =
                        PlaylistEntity(
                            playlistId = UUID.randomUUID().toString(),
                            description = description?.ifEmpty { null },
                            image = image?.ifEmpty { null },
                            name = name,
                            owner = user.name,
                            ownerId = user.id,
                        )
                    songsRepository.insertPlaylist(playlistEntity)
                    emit(Result.Success(playlistEntity.playlistId))
                } ?: emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }
}
