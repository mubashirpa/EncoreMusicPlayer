package com.encore.music.presentation.ui.fragments.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.usecase.playlists.GetCategoryPlaylistsUseCase
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val getCategoryPlaylistsUseCase: GetCategoryPlaylistsUseCase,
) : ViewModel() {
    private val category = savedStateHandle.toRoute<Screen.Category>()
    val title = category.title

    private val _playlists = MutableLiveData<PagingData<Playlist>>()
    val playlists: LiveData<PagingData<Playlist>> = _playlists

    init {
        getCategoryPlaylists(category.id)
    }

    private fun getCategoryPlaylists(categoryId: String) {
        viewModelScope.launch {
            getCategoryPlaylistsUseCase(categoryId)
                .cachedIn(this)
                .collectLatest {
                    _playlists.value = it
                }
        }
    }
}
