package com.encore.music.data.repository

import com.encore.music.core.Encore
import com.encore.music.data.remote.dto.categories.CategoriesDto
import com.encore.music.domain.repository.CategoriesRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments

class CategoriesRepositoryImpl(
    private val httpClient: HttpClient,
) : CategoriesRepository {
    override suspend fun getCategories(
        accessToken: String,
        locale: String?,
        limit: Int,
        offset: Int,
    ): CategoriesDto =
        httpClient
            .get(Encore.API_BASE_URL) {
                url {
                    appendPathSegments(Encore.ENDPOINT_GET_CATEGORIES)
                    locale?.let { parameters.append(Encore.Parameters.LOCALE, it) }
                    parameters.append(Encore.Parameters.LIMIT, limit.toString())
                    parameters.append(Encore.Parameters.OFFSET, offset.toString())
                }
                header(HttpHeaders.Authorization, accessToken)
            }.body()
}
