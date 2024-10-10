package com.encore.music.presentation.ui.fragments.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.usecase.playlists.GetCategoryPlaylistsUseCase
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CategoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCategoryPlaylistsUseCase: GetCategoryPlaylistsUseCase,
) : ViewModel() {
    private val category = savedStateHandle.toRoute<Screen.Category>()
    private val categoryId = category.id
    val title = category.title

    private val _uiState = MutableLiveData<CategoryUiState>()
    val uiState: LiveData<CategoryUiState> = _uiState

    init {
        getCategoryPlaylists(categoryId)
    }

    private fun getCategoryPlaylists(categoryId: String) {
        getCategoryPlaylistsUseCase(categoryId)
            .onEach { result ->
                when (result) {
                    is Result.Empty -> Unit

                    is Result.Error -> {
                        _uiState.value = CategoryUiState.Error(result.message!!)
                    }

                    is Result.Loading -> {
                        _uiState.value = CategoryUiState.Loading
                    }

                    is Result.Success -> {
                        _uiState.value = result.data?.let { playlists ->
                            if (playlists.isEmpty()) {
                                CategoryUiState.Empty
                            } else {
                                CategoryUiState.Success(playlists)
                            }
                        } ?: CategoryUiState.Error(UiText.StringResource(R.string.error_unexpected))
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun retry() {
        getCategoryPlaylists(categoryId)
    }
}
