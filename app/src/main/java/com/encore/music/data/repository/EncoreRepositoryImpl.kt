package com.encore.music.data.repository

import com.encore.music.core.Encore
import com.encore.music.data.remote.dto.home.HomePlaylistDto
import com.encore.music.data.remote.dto.playlists.PlaylistsDto
import com.encore.music.domain.repository.EncoreRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments

class EncoreRepositoryImpl(
    private val httpClient: HttpClient,
) : EncoreRepository {
    override suspend fun getFeaturedPlaylists(
        accessToken: String,
        locale: String?,
        limit: Int,
        offset: Int,
    ): PlaylistsDto =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(Encore.ENDPOINT_FEATURED_PLAYLISTS)
                    if (!locale.isNullOrEmpty()) parameters.append(Encore.Parameters.LOCALE, locale)
                    parameters.append(Encore.Parameters.LIMIT, limit.toString())
                    parameters.append(Encore.Parameters.OFFSET, offset.toString())
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()

    override suspend fun getCategoryPlaylists(
        accessToken: String,
        categoryId: String,
        limit: Int,
        offset: Int,
    ): PlaylistsDto =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(
                        Encore.ENDPOINT_CATEGORY_PLAYLISTS.replace(
                            "{category_id}",
                            categoryId,
                        ),
                    )
                    parameters.append(Encore.Parameters.LIMIT, limit.toString())
                    parameters.append(Encore.Parameters.OFFSET, offset.toString())
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()

    override suspend fun getHomePlaylists(accessToken: String): List<HomePlaylistDto> =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(Encore.ENDPOINT_HOME_PLAYLISTS)
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
}
