package com.encore.music.data.repository

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
            .get("http://192.168.23.129:8080/v1") {
                url {
                    appendPathSegments("browse/featured-playlists")
                    if (!locale.isNullOrEmpty()) {
                        parameters.append("locale", locale)
                    }
                    parameters.append("limit", limit.toString())
                    parameters.append("offset", offset.toString())
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
}
