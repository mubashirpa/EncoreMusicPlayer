package com.encore.music.presentation.ui.fragments.searchItems

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.encore.music.domain.model.search.SearchItem
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.usecase.search.SearchForItemPagingUseCase
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchItemsViewModel(
    savedStateHandle: SavedStateHandle,
    private val serForItemPagingUseCase: SearchForItemPagingUseCase,
) : ViewModel() {
    private val searchItemsRoute = savedStateHandle.toRoute<Screen.SearchItems>()
    val searchQuery = searchItemsRoute.query
    val searchType = searchItemsRoute.type

    private val _searchItems = MutableLiveData<PagingData<SearchItem>>()
    val searchItems: LiveData<PagingData<SearchItem>> = _searchItems

    init {
        searchForItem(searchQuery, searchType)
    }

    private fun searchForItem(
        query: String,
        type: SearchType,
    ) {
        viewModelScope.launch {
            serForItemPagingUseCase(query, listOf(type))
                .cachedIn(this)
                .collectLatest {
                    _searchItems.value = it
                }
        }
    }
}
