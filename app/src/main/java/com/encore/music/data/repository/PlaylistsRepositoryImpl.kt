package com.encore.music.data.repository

import com.encore.music.core.Encore
import com.encore.music.core.utils.toResult
import com.encore.music.data.remote.dto.home.HomePlaylistDto
import com.encore.music.data.remote.dto.playlists.Playlist
import com.encore.music.data.remote.dto.playlists.PlaylistsDto
import com.encore.music.domain.repository.PlaylistsRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments

class PlaylistsRepositoryImpl(
    private val httpClient: HttpClient,
) : PlaylistsRepository {
    override suspend fun getPlaylist(
        accessToken: String,
        playlistId: String,
        market: String?,
        additionalTypes: String?,
    ): Playlist =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(
                        Encore.ENDPOINT_GET_PLAYLIST.replace(
                            "{playlist_id}",
                            playlistId,
                        ),
                    )
                    market?.let { parameters.append(Encore.Parameters.MARKET, market) }
                    additionalTypes?.let {
                        parameters.append(Encore.Parameters.ADDITIONAL_TYPES, additionalTypes)
                    }
                }
                header(HttpHeaders.Authorization, accessToken)
            }.toResult()

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
                        Encore.ENDPOINT_GET_CATEGORY_PLAYLISTS.replace(
                            "{category_id}",
                            categoryId,
                        ),
                    )
                    parameters.append(Encore.Parameters.LIMIT, limit.toString())
                    parameters.append(Encore.Parameters.OFFSET, offset.toString())
                }
                header(HttpHeaders.Authorization, accessToken)
            }.toResult()

    override suspend fun getHomePlaylists(
        accessToken: String,
        locale: String?,
        limit: Int,
        offset: Int,
    ): List<HomePlaylistDto> =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(Encore.ENDPOINT_GET_HOME_PLAYLISTS)
                    locale?.let { parameters.append(Encore.Parameters.LOCALE, locale) }
                    parameters.append(Encore.Parameters.LIMIT, limit.toString())
                    parameters.append(Encore.Parameters.OFFSET, offset.toString())
                }
                header(HttpHeaders.Authorization, accessToken)
            }.toResult()
}
