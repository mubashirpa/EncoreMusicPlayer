package com.encore.music.presentation.ui.fragments.search

import com.encore.music.core.UiText
import com.encore.music.domain.model.categories.Category

sealed class CategoriesUiState {
    data class Error(
        val message: UiText,
    ) : CategoriesUiState()

    data class Success(
        val categories: List<Category>,
    ) : CategoriesUiState()

    data object Empty : CategoriesUiState()

    data object Loading : CategoriesUiState()
}
