package com.encore.music.domain.usecase.categories

import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toCategoryDomainModelList
import com.encore.music.domain.model.categories.Category
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCategoriesUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val repository: CategoriesRepository,
) {
    operator fun invoke(): Flow<Result<List<Category>>> =
        flow {
            try {
                emit(Result.Loading())
                val idToken = authenticationRepository.getIdToken().orEmpty()
                val playlists =
                    repository.getCategories(idToken).toCategoryDomainModelList()
                emit(Result.Success(playlists))
            } catch (e: Exception) {
                emit(Result.Error(UiText.DynamicString(e.message.toString())))
            }
        }
}
