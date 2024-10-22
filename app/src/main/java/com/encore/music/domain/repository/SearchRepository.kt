package com.encore.music.domain.repository

import androidx.paging.PagingData
import com.encore.music.data.remote.dto.search.SearchDto
import com.encore.music.data.remote.dto.search.SearchItem
import com.encore.music.domain.model.search.SearchType
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchForItem(
        accessToken: String,
        query: String,
        type: List<SearchType>,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): SearchDto

    fun searchForItem(
        query: String,
        type: List<SearchType>,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): Flow<PagingData<SearchItem>>
}
