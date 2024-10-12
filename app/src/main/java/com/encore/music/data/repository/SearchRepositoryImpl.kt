package com.encore.music.data.repository

import com.encore.music.core.Encore
import com.encore.music.data.remote.dto.search.SearchDto
import com.encore.music.domain.model.search.IncludeExternal
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.repository.SearchRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments

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
        includeExternal: IncludeExternal?,
    ): SearchDto =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(Encore.ENDPOINT_GET_SEARCH)
                    parameters.append(Encore.Parameters.QUERY, query)
                    parameters.append(
                        Encore.Parameters.TYPE,
                        type.joinToString(",") { it.name.lowercase() },
                    )
                    market?.let { parameters.append(Encore.Parameters.MARKET, market) }
                    parameters.append(Encore.Parameters.LIMIT, limit.toString())
                    parameters.append(Encore.Parameters.OFFSET, offset.toString())
                    includeExternal?.let {
                        parameters.append(
                            Encore.Parameters.INCLUDE_EXTERNAL,
                            it.name.lowercase(),
                        )
                    }
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
}
