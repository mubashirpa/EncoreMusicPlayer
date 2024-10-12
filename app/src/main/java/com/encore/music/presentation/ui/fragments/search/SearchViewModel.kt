package com.encore.music.presentation.ui.fragments.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.encore.music.R
import com.encore.music.core.Result
import com.encore.music.core.UiText
import com.encore.music.domain.model.authentication.User
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.categories.GetCategoriesUseCase
import com.encore.music.domain.usecase.search.SearchItemUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SearchViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val searchItemUseCase: SearchItemUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<CategoriesUiState>()
    val uiState: LiveData<CategoriesUiState> = _uiState

    private val _searchState = MutableLiveData<SearchUiState>()
    val searchState: LiveData<SearchUiState> = _searchState

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    var searchType = SearchType.TRACK
    private var searchItemUseCaseJob: Job? = null

    init {
        getCurrentUser()
        getCategories()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                user?.let { _currentUser.value = it }
            }
        }
    }

    fun getCategories() {
        getCategoriesUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Empty -> Unit

                    is Result.Error -> {
                        _uiState.value = CategoriesUiState.Error(result.message!!)
                    }

                    is Result.Loading -> {
                        _uiState.value = CategoriesUiState.Loading
                    }

                    is Result.Success -> {
                        _uiState.value = result.data?.let { categories ->
                            CategoriesUiState.Success(categories)
                        }
                            ?: CategoriesUiState.Error(UiText.StringResource(R.string.error_unexpected))
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun search(
        query: String,
        type: SearchType,
        delay: Long = 0,
    ) {
        searchItemUseCaseJob?.cancel()
        searchItemUseCaseJob = null
        if (query.isBlank()) {
            _searchState.value =
                SearchUiState.Empty(null)
            return
        }
        searchItemUseCaseJob =
            viewModelScope.launch {
                delay(delay)
                searchItemUseCase(
                    query = query.trim(),
                    type = listOf(type),
                ).onEach { result ->
                    when (result) {
                        is Result.Empty -> Unit

                        is Result.Error -> {
                            _searchState.value = SearchUiState.Error(result.message!!)
                        }

                        is Result.Loading -> {
                            _searchState.value = SearchUiState.Loading
                        }

                        is Result.Success -> {
                            _searchState.value = result.data?.let { search ->
                                when (type) {
                                    SearchType.ARTIST -> {
                                        val artists = search.artists.orEmpty()
                                        if (artists.isNotEmpty()) {
                                            SearchUiState.Success(SearchListItem.ArtistsItem(artists))
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
                                        val playlists = search.playlists.orEmpty()
                                        if (playlists.isNotEmpty()) {
                                            SearchUiState.Success(
                                                SearchListItem.PlaylistsItem(
                                                    playlists,
                                                ),
                                            )
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
                                        val tracks = search.tracks.orEmpty()
                                        if (tracks.isNotEmpty()) {
                                            SearchUiState.Success(SearchListItem.TracksItem(tracks))
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
}
