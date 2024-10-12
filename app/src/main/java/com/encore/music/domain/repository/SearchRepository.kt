package com.encore.music.domain.repository

import com.encore.music.data.remote.dto.search.SearchDto
import com.encore.music.domain.model.search.IncludeExternal
import com.encore.music.domain.model.search.SearchType

interface SearchRepository {
    suspend fun searchForItem(
        accessToken: String,
        query: String,
        type: List<SearchType>,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: IncludeExternal? = null,
    ): SearchDto
}
