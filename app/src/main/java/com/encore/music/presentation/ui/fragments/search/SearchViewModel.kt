package com.encore.music.presentation.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.categories.GetCategoriesUseCase
import com.encore.music.domain.usecase.search.SearchForItemUseCase
import com.encore.music.domain.usecase.songs.playlists.CreatePlaylistUseCase
import com.encore.music.domain.usecase.songs.playlists.GetSavedLocalPlaylistsUseCase
import com.encore.music.domain.usecase.songs.playlists.InsertPlaylistUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getSavedLocalPlaylistsUseCase: GetSavedLocalPlaylistsUseCase,
    private val searchForItemUseCase: SearchForItemUseCase,
    private val insertPlaylistUseCase: InsertPlaylistUseCase,
) : ViewModel() {
    private val _categoriesUiState = MutableLiveData<CategoriesUiState>()
    val categoriesUiState: LiveData<CategoriesUiState> = _categoriesUiState

    private val _searchUiState = MutableLiveData<SearchUiState>()
    val searchUiState: LiveData<SearchUiState> = _searchUiState

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _savedPlaylists = MutableLiveData<List<Playlist>>()
    val savedPlaylists: LiveData<List<Playlist>> = _savedPlaylists

    var searchType = SearchType.TRACK
    private var searchItemUseCaseJob: Job? = null

    init {
        getCurrentUser()
        getCategories()
        getSavedLocalPlaylists()
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnCreatePlaylist -> {
                createPlaylist(event.playlist)
            }

            is SearchUiEvent.OnInsertTrackToLocalPlaylist -> {
                savePlaylist(event.playlist)
            }

            SearchUiEvent.OnRetry -> {
                getCategories()
            }

            is SearchUiEvent.OnSearch -> {
                search(event.query, event.searchType, event.delay)
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                user?.let { _currentUser.value = it }
            }
        }
    }

    private fun getCategories() {
        getCategoriesUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Empty -> Unit

                    is Result.Error -> {
                        _categoriesUiState.value = CategoriesUiState.Error(result.message!!)
                    }

                    is Result.Loading -> {
                        _categoriesUiState.value = CategoriesUiState.Loading
                    }

                    is Result.Success -> {
                        _categoriesUiState.value = result.data?.let { categories ->
                            CategoriesUiState.Success(categories)
                        }
                            ?: CategoriesUiState.Error(UiText.StringResource(R.string.error_unexpected))
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun search(
        query: String,
        type: SearchType,
        delay: Long = 0,
    ) {
        searchItemUseCaseJob?.cancel()
        searchItemUseCaseJob = null
        if (query.isBlank()) {
            _searchUiState.value = SearchUiState.Empty(null)
            return
        }
        searchItemUseCaseJob =
            viewModelScope.launch {
                delay(delay)
                searchForItemUseCase(
                    query = query.trim(),
                    type = listOf(type),
                ).onEach { result ->
                    when (result) {
                        is Result.Empty -> Unit

                        is Result.Error -> {
                            _searchUiState.value = SearchUiState.Error(result.message!!)
                        }

                        is Result.Loading -> {
                            _searchUiState.value = SearchUiState.Loading
                        }

                        is Result.Success -> {
                            _searchUiState.value = result.data?.let { search ->
                                when (type) {
                                    SearchType.ARTIST -> {
                                        val artists =
                                            search.artists
                                                ?.map {
                                                    SearchListItem.ArtistItem(it)
                                                }.orEmpty()

                                        if (artists.isNotEmpty()) {
                                            SearchUiState.Success(artists)
                                        } else {
                                            SearchUiState.Empty(
                                                UiText.StringResource(
                                                    R.string.no_results_for_,
                                                    query,
                                                ),
                                            )
                                        }
                                    }

                                    SearchType.PLAYLIST -> {
                                        val playlists =
                                            search.playlists
                                                ?.map {
                                                    SearchListItem.PlaylistItem(it)
                                                }.orEmpty()

                                        if (playlists.isNotEmpty()) {
                                            SearchUiState.Success(playlists)
                                        } else {
                                            SearchUiState.Empty(
                                                UiText.StringResource(
                                                    R.string.no_results_for_,
                                                    query,
                                                ),
                                            )
                                        }
                                    }

                                    SearchType.TRACK -> {
                                        val tracks =
                                            search.tracks
                                                ?.map {
                                                    SearchListItem.TrackItem(it)
                                                }.orEmpty()

                                        if (tracks.isNotEmpty()) {
                                            SearchUiState.Success(tracks)
                                        } else {
                                            SearchUiState.Empty(
                                                UiText.StringResource(
                                                    R.string.no_results_for_,
                                                    query,
                                                ),
                                            )
                                        }
                                    }
                                }
                            }
                                ?: SearchUiState.Error(UiText.StringResource(R.string.error_unexpected))
                        }
                    }
                }.launchIn(this)
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
