package com.encore.music.domain.usecase.search

import androidx.paging.PagingData
import androidx.paging.map
import com.encore.music.core.mapper.toSearchItemDomainModel
import com.encore.music.domain.model.search.SearchItem
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchForItemPagingUseCase(
    private val searchRepository: SearchRepository,
) {
    operator fun invoke(
        query: String,
        type: List<SearchType>,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
    ): Flow<PagingData<SearchItem>> =
        searchRepository
            .searchForItem(
                query = query,
                type = type,
                market = market,
                limit = limit,
                offset = offset,
                includeExternal = includeExternal,
            ).map { pagingData ->
                pagingData.map { it.toSearchItemDomainModel() }
            }
}
