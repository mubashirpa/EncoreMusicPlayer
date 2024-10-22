package com.encore.music.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.encore.music.core.Encore
import com.encore.music.core.utils.toResult
import com.encore.music.data.remote.dto.search.SearchDto
import com.encore.music.data.remote.dto.search.SearchItem
import com.encore.music.data.remote.paging.SearchPagingSource
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.repository.SearchRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import kotlinx.coroutines.flow.Flow

class SearchRepositoryImpl(
    private val httpClient: HttpClient,
) : SearchRepository {
    override suspend fun searchForItem(
        accessToken: String,
        query: String,
        type: List<SearchType>,
        market: String?,
        limit: Int,
        offset: Int,
        includeExternal: String?,
    ): SearchDto =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(Encore.ENDPOINT_GET_SEARCH)
                    parameters.append(Encore.Parameters.QUERY, query)
                    parameters.appendAll(Encore.Parameters.TYPE, type.map { it.name.lowercase() })
                    market?.let { parameters.append(Encore.Parameters.MARKET, market) }
                    parameters.append(Encore.Parameters.LIMIT, limit.toString())
                    parameters.append(Encore.Parameters.OFFSET, offset.toString())
                    includeExternal?.let {
                        parameters.append(Encore.Parameters.INCLUDE_EXTERNAL, includeExternal)
                    }
                }
                header(HttpHeaders.Authorization, accessToken)
            }.toResult()

    override fun searchForItem(
        query: String,
        type: List<SearchType>,
        market: String?,
        limit: Int,
        offset: Int,
        includeExternal: String?,
    ): Flow<PagingData<SearchItem>> =
        Pager(
            config = PagingConfig(pageSize = limit, initialLoadSize = limit),
            pagingSourceFactory = {
                SearchPagingSource(
                    query = query,
                    type = type,
                    market = market,
                    offset = offset,
                    includeExternal = includeExternal,
                )
            },
        ).flow
}
