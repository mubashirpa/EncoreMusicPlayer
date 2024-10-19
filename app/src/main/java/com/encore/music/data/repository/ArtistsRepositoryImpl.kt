package com.encore.music.data.repository

import com.encore.music.core.Encore
import com.encore.music.core.utils.toResult
import com.encore.music.data.remote.dto.artists.Artist
import com.encore.music.domain.repository.ArtistsRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments

class ArtistsRepositoryImpl(
    private val httpClient: HttpClient,
) : ArtistsRepository {
    override suspend fun getArtistTopTracks(
        accessToken: String,
        artistId: String,
        market: String?,
    ): Artist =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(
                        Encore.ENDPOINT_GET_ARTIST_TOP_TRACKS.replace(
                            "{artist_id}",
                            artistId,
                        ),
                    )
                    market?.let { parameters.append(Encore.Parameters.MARKET, market) }
                }
                header(HttpHeaders.Authorization, accessToken)
            }.toResult()
}
