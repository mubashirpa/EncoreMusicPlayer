package com.encore.music.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.encore.music.core.Encore
import com.encore.music.core.utils.toResult
import com.encore.music.data.remote.dto.tracks.Track
import com.encore.music.data.remote.dto.tracks.TracksDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments

class TracksPagingSource(
    private val httpClient: HttpClient,
    private val accessToken: String,
    private val playlistId: String,
    private val market: String? = null,
    private val fields: String? = null,
    private val offset: Int = 0,
    private val additionalTypes: String? = null,
) : PagingSource<Int, Track>() {
    override fun getRefreshKey(state: PagingState<Int, Track>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> =
        try {
            val limit = params.loadSize
            val currentOffset = params.key ?: offset
            val response =
                getPlaylistTracks(
                    accessToken = accessToken,
                    playlistId = playlistId,
                    market = market,
                    fields = fields,
                    limit = limit,
                    offset = currentOffset,
                    additionalTypes = additionalTypes,
                )
            val tracks = response.items.orEmpty()
            val total = response.total ?: tracks.size

            val prevKey =
                if (currentOffset > offset) (currentOffset - limit).coerceAtLeast(offset) else null
            val nextKey = if (currentOffset + limit < total) currentOffset + limit else null

            LoadResult.Page(
                data = tracks,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    private suspend fun getPlaylistTracks(
        accessToken: String,
        playlistId: String,
        market: String? = null,
        fields: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        additionalTypes: String? = null,
    ): TracksDto =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(
                        Encore.ENDPOINT_GET_PLAYLIST_TRACKS.replace(
                            "{playlist_id}",
                            playlistId,
                        ),
                    )
                    market?.let { parameters.append(Encore.Parameters.MARKET, it) }
                    fields?.let { parameters.append(Encore.Parameters.FIELDS, it) }
                    parameters.append(Encore.Parameters.LIMIT, limit.toString())
                    parameters.append(Encore.Parameters.OFFSET, offset.toString())
                    additionalTypes?.let {
                        parameters.append(Encore.Parameters.ADDITIONAL_TYPES, it)
                    }
                }
                header(HttpHeaders.Authorization, accessToken)
            }.toResult()
}
