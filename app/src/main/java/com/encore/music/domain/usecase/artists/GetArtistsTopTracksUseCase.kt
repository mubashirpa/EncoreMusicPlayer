package com.encore.music.domain.usecase.artists

import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toArtistDomainModel
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.repository.ArtistsRepository
import com.encore.music.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetArtistsTopTracksUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val artistsRepository: ArtistsRepository,
) {
    operator fun invoke(
        artistId: String,
        market: String? = null,
    ): Flow<Result<Artist>> =
        flow {
            try {
                emit(Result.Loading())
                val idToken = authenticationRepository.getIdToken().orEmpty()
                val artistTopTracks =
                    artistsRepository
                        .getArtistTopTracks(idToken, artistId, market)
                        .toArtistDomainModel()
                emit(Result.Success(artistTopTracks))
            } catch (e: Exception) {
                emit(Result.Error(UiText.DynamicString(e.message.toString())))
            }
        }
}
