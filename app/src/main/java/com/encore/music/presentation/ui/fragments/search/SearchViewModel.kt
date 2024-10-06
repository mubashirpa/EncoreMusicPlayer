package com.encore.music.presentation.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.core.Result
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.categories.GetCategoriesUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<CategoriesUiState>()
    val uiState: LiveData<CategoriesUiState> = _uiState

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    init {
        getCurrentUser()
        getCategories()
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            SearchUiEvent.OnRetry -> {
                getCategories()
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect {
                _currentUser.value = it
            }
        }
    }

    private fun getCategories() {
        getCategoriesUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Empty -> {
                        _uiState.value = CategoriesUiState.Empty
                    }

                    is Result.Error -> {
                        _uiState.value = CategoriesUiState.Error(result.message!!)
                    }

                    is Result.Loading -> {
                        _uiState.value = CategoriesUiState.Loading
                    }

                    is Result.Success -> {
                        val categories = result.data
                        if (categories == null) {
                            _uiState.value = CategoriesUiState.Empty
                        } else {
                            _uiState.value = CategoriesUiState.Success(categories)
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
