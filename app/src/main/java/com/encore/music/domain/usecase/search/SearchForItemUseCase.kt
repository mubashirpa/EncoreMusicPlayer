package com.encore.music.domain.usecase.search

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toSearchDomainModel
import com.encore.music.core.utils.KtorException
import com.encore.music.domain.model.search.Search
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException
import java.util.Locale

class SearchForItemUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val searchRepository: SearchRepository,
) {
    operator fun invoke(
        query: String,
        type: List<SearchType>,
        market: String? = Locale.getDefault().country,
        limit: Int = 20,
        offset: Int = 0,
        includeExternal: String? = null,
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
            } catch (e: ConnectException) {
                emit(Result.Error(UiText.StringResource(R.string.error_connect)))
            } catch (e: KtorException) {
                emit(Result.Error(e.localizedMessage))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
            }
        }
}
