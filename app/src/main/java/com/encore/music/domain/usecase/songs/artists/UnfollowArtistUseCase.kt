package com.encore.music.domain.usecase.songs.artists

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toArtistEntity
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UnfollowArtistUseCase(
    private val songsRepository: SongsRepository,
) {
    operator fun invoke(artist: Artist): Flow<Result<String>> =
        flow {
            try {
                emit(Result.Loading())
                val artistId = artist.id!!
                songsRepository.updateFollowedArtist(artist.toArtistEntity(followedAt = null))
                emit(Result.Success(artistId))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }
}
