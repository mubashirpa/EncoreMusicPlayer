package com.encore.music.presentation.ui.fragments.searchItems

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.search.SearchItem
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.usecase.search.SearchForItemPagingUseCase
import com.encore.music.domain.usecase.songs.playlists.CreatePlaylistUseCase
import com.encore.music.domain.usecase.songs.playlists.GetSavedLocalPlaylistsUseCase
import com.encore.music.domain.usecase.songs.playlists.InsertPlaylistUseCase
import com.encore.music.presentation.navigation.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class SearchItemsViewModel(
    savedStateHandle: SavedStateHandle,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val getSavedLocalPlaylistsUseCase: GetSavedLocalPlaylistsUseCase,
    private val insertPlaylistUseCase: InsertPlaylistUseCase,
    private val searchForItemPagingUseCase: SearchForItemPagingUseCase,
) : ViewModel() {
    private val searchItemsRoute = savedStateHandle.toRoute<Screen.SearchItems>()
    val searchQuery = searchItemsRoute.query
    val searchType = searchItemsRoute.type

    private val _searchItems = MutableLiveData<PagingData<SearchItem>>()
    val searchItems: LiveData<PagingData<SearchItem>> = _searchItems

    private val _savedPlaylists = MutableLiveData<List<Playlist>>()
    val savedPlaylists: LiveData<List<Playlist>> = _savedPlaylists

    init {
        searchForItem(searchQuery, searchType)
        getSavedLocalPlaylists()
    }

    fun onEvent(event: SearchItemsUiEvent) {
        when (event) {
            is SearchItemsUiEvent.OnCreatePlaylist -> {
                createPlaylist(event.playlist)
            }

            is SearchItemsUiEvent.OnInsertTrackToLocalPlaylist -> {
                savePlaylist(event.playlist)
            }
        }
    }

    private fun searchForItem(
        query: String,
        type: SearchType,
    ) {
        viewModelScope.launch {
            searchForItemPagingUseCase(query, listOf(type))
                .cachedIn(this)
                .collectLatest {
                    _searchItems.value = it
                }
        }
    }

    // Fetched to add track to playlist
    private fun getSavedLocalPlaylists() {
        viewModelScope.launch {
            getSavedLocalPlaylistsUseCase().collect {
                _savedPlaylists.value = it
            }
        }
    }

    private fun createPlaylist(playlist: Playlist) {
        createPlaylistUseCase(playlist).launchIn(viewModelScope)
    }

    private fun savePlaylist(playlist: Playlist) {
        insertPlaylistUseCase(playlist).launchIn(viewModelScope)
    }
}
