package com.encore.music.domain.usecase.categories

import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.core.mapper.toCategoryDomainModelList
import com.encore.music.core.utils.KtorException
import com.encore.music.domain.model.categories.Category
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.ConnectException
import java.util.Locale

class GetCategoriesUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val repository: CategoriesRepository,
) {
    private val defaultLocale = Locale.getDefault()
    private val languageCountryCode = "${defaultLocale.language}_${defaultLocale.country}"

    operator fun invoke(
        locale: String? = languageCountryCode,
        limit: Int = 20,
        offset: Int = 0,
    ): Flow<Result<List<Category>>> =
        flow {
            try {
                emit(Result.Loading())
                val idToken = authenticationRepository.getIdToken().orEmpty()
                val playlists =
                    repository
                        .getCategories(idToken, locale, limit, offset)
                        .toCategoryDomainModelList()
                emit(Result.Success(playlists))
            } catch (e: ConnectException) {
                emit(Result.Error(UiText.StringResource(R.string.error_connect)))
            } catch (e: KtorException) {
                emit(Result.Error(e.localizedMessage))
            } catch (e: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
            }
        }
}
