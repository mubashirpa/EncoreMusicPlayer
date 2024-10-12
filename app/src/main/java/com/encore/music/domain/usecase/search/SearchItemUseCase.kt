package com.encore.music.domain.usecase.search

import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toSearchDomainModel
import com.encore.music.domain.model.search.IncludeExternal
import com.encore.music.domain.model.search.Search
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchItemUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val searchRepository: SearchRepository,
) {
    operator fun invoke(
        query: String,
        type: List<SearchType>,
        market: String? = null,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: IncludeExternal? = null,
    ): Flow<Result<Search>> =
        flow {
            try {
                emit(Result.Loading())
                val idToken = authenticationRepository.getIdToken().orEmpty()
                val search =
                    searchRepository
                        .searchForItem(
                            accessToken = idToken,
                            query = query,
                            type = type,
                            market = market,
                            limit = limit,
                            offset = offset,
                            includeExternal = includeExternal,
                        ).toSearchDomainModel()
                emit(Result.Success(search))
            } catch (e: Exception) {
                emit(Result.Error(UiText.DynamicString(e.message.toString())))
            }
        }
}
