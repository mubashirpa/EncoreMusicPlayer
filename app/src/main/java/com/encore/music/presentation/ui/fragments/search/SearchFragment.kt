package com.encore.music.presentation.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.encore.music.R
import com.encore.music.core.ext.dpToPx
import com.encore.music.databinding.FragmentSearchBinding
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.model.tracks.Track
import com.encore.music.presentation.navigation.navigateToArtist
import com.encore.music.presentation.navigation.navigateToCategory
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.navigation.navigateToProfile
import com.encore.music.presentation.ui.activities.MainUiEvent
import com.encore.music.presentation.ui.activities.MainViewModel
import com.encore.music.presentation.ui.fragments.dialog.AddToPlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.CreatePlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.MenuItem
import com.encore.music.presentation.ui.fragments.dialog.TrackMenuBottomSheet
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.SpanCount
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: SearchViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val searchAdapter = initSearchAdapter()

        binding.chipGroup.check(
            when (viewModel.searchType) {
                SearchType.ARTIST -> binding.artistsFilter.id
                SearchType.PLAYLIST -> binding.playlistsFilter.id
                SearchType.TRACK -> binding.songsFilter.id
            },
        )

        binding.searchErrorView.root.setBackgroundColor(requireContext().getColor(android.R.color.transparent))

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            ImageUtils.loadProfile(
                context = requireContext(),
                url = user.photoUrl,
                onStart = { placeholder ->
                    binding.topAppBar.navigationIcon = placeholder
                },
                onSuccess = { result ->
                    binding.topAppBar.navigationIcon = result
                },
                onError = { error ->
                    binding.topAppBar.navigationIcon = error
                },
            )
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is CategoriesUiState.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.errorView.apply {
                        root.visibility = View.VISIBLE
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener {
                            viewModel.onEvent(SearchUiEvent.OnRetry)
                        }
                    }
                }

                is CategoriesUiState.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.nestedScrollView.visibility = View.VISIBLE

                    val categoriesAdapter =
                        CategoriesAdapter(
                            uiState.categories,
                            onItemClick = { category ->
                                category.id?.let { categoryId ->
                                    navController.navigateToCategory(
                                        categoryId,
                                        category.name.orEmpty(),
                                    )
                                }
                            },
                        )
                    binding.recyclerView.apply {
                        layoutManager =
                            GridLayoutManager(
                                requireContext(),
                                SpanCount.adaptive(requireContext(), 120),
                            )
                        addItemDecoration(
                            AdaptiveSpacingItemDecoration(
                                size = 12.dpToPx(requireContext()),
                                edgeEnabled = true,
                            ),
                        )
                        adapter = categoriesAdapter
                    }
                }

                CategoriesUiState.Loading -> {
                    binding.errorView.root.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }

        viewModel.searchState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is SearchUiState.Empty -> {
                    binding.searchProgressCircular.visibility = View.GONE
                    binding.searchRecyclerView.visibility = View.GONE

                    binding.searchErrorView.apply {
                        root.isVisible = uiState.message != null
                        uiState.message?.let { errorText.text = it.asString(requireContext()) }
                    }
                }

                is SearchUiState.Error -> {
                    binding.searchProgressCircular.visibility = View.GONE

                    binding.searchErrorView.apply {
                        root.visibility = View.VISIBLE
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE

                        retryButton.setOnClickListener {
                            viewModel.onEvent(
                                SearchUiEvent.OnSearch(
                                    binding.searchView.text.toString(),
                                    viewModel.searchType,
                                ),
                            )
                        }
                    }
                }

                SearchUiState.Loading -> {
                    binding.searchRecyclerView.visibility = View.GONE
                    binding.searchErrorView.root.visibility = View.GONE
                    binding.searchProgressCircular.visibility = View.VISIBLE
                }

                is SearchUiState.Success -> {
                    searchAdapter.items = uiState.items

                    binding.searchRecyclerView.apply {
                        if (uiState.isGridLayout) {
                            layoutManager =
                                GridLayoutManager(
                                    requireContext(),
                                    SpanCount.adaptive(requireContext(), 160),
                                )
                            if (itemDecorationCount == 0) {
                                addItemDecoration(
                                    AdaptiveSpacingItemDecoration(
                                        size = 12.dpToPx(requireContext()),
                                        edgeEnabled = true,
                                    ),
                                )
                            }
                        } else {
                            layoutManager = LinearLayoutManager(requireContext())
                            if (itemDecorationCount >= 1) removeItemDecorationAt(0)
                        }
                    }

                    binding.searchProgressCircular.visibility = View.GONE
                    binding.searchRecyclerView.visibility = View.VISIBLE
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateToProfile()
        }

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            viewModel.searchType =
                when (checkedIds.firstOrNull()) {
                    binding.playlistsFilter.id -> SearchType.PLAYLIST
                    binding.artistsFilter.id -> SearchType.ARTIST
                    else -> SearchType.TRACK
                }
            viewModel.onEvent(
                SearchUiEvent.OnSearch(
                    binding.searchView.text.toString(),
                    viewModel.searchType,
                ),
            )
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                viewModel.onEvent(SearchUiEvent.OnSearchOpened)
            }
        }

        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            viewModel.onEvent(
                SearchUiEvent.OnSearch(
                    text.toString(),
                    viewModel.searchType,
                    500,
                ),
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        binding.searchView.setVisible(false)
        super.onStop()
    }

    private fun initSearchAdapter(): SearchAdapter {
        val searchAdapter =
            SearchAdapter(
                context = requireContext(),
                onArtistClicked = { artist ->
                    artist.id?.let {
                        binding.searchView.setVisible(false)
                        navController.navigateToArtist(it)
                    }
                },
                onPlaylistClicked = { playlist ->
                    playlist.id?.let {
                        binding.searchView.setVisible(false)
                        navController.navigateToPlaylist(it, playlist.isLocal == true)
                    }
                },
                onTrackClicked = { track ->
                    playTrack(track)
                },
                onTrackMoreClicked = { track ->
                    showTrackMenuBottomSheet(track)
                },
            )
        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
        return searchAdapter
    }

    private fun showTrackMenuBottomSheet(track: Track) {
        val artist = track.artists?.firstOrNull()
        val items =
            listOf(
                MenuItem(
                    id = 0,
                    title = getString(R.string.play_now),
                    icon = R.drawable.baseline_play_arrow_24,
                ),
                MenuItem(
                    id = 1,
                    title = getString(R.string.play_next),
                    icon = R.drawable.baseline_skip_next_24,
                ),
                MenuItem(
                    id = 2,
                    title = getString(R.string.add_to_queue),
                    icon = R.drawable.baseline_add_to_queue_24,
                ),
                MenuItem(
                    id = 3,
                    title = getString(R.string.add_to_playlist),
                    icon = R.drawable.baseline_playlist_add_24,
                ),
                MenuItem(
                    id = 4,
                    title =
                        getString(
                            R.string.more_from_,
                            artist?.name.orEmpty(),
                        ),
                    icon = R.drawable.baseline_person_search_24,
                ),
            )
        TrackMenuBottomSheet(track, items)
            .setOnMenuItemClickListener { _, id ->
                when (id) {
                    0 -> {
                        playTrack(track)
                    }

                    1 -> {
                        mainViewModel.onEvent(MainUiEvent.AddNextInPlaylist(track))
                    }

                    2 -> {
                        mainViewModel.onEvent(MainUiEvent.AddToPlaylist(track))
                    }

                    3 -> {
                        showAddToPlaylistBottomSheet(track)
                    }

                    4 -> {
                        artist?.id?.let {
                            binding.searchView.setVisible(false)
                            navController.navigateToArtist(it)
                        }
                    }
                }
            }.show(
                childFragmentManager,
                TrackMenuBottomSheet.TAG,
            )
    }

    private fun showAddToPlaylistBottomSheet(track: Track) {
        val playlists = viewModel.savedPlaylists.value.orEmpty()
        AddToPlaylistBottomSheet(track, playlists)
            .setOnCreatePlaylistClickListener { dialog, playlistTrack ->
                showCreatePlaylistBottomSheet(playlistTrack)
                dialog.dismiss()
            }.setOnAddToPlaylistClickListener { dialog, playlist ->
                viewModel.onEvent(SearchUiEvent.OnInsertTrackToLocalPlaylist(playlist))
                dialog.dismiss()
            }.show(
                childFragmentManager,
                AddToPlaylistBottomSheet.TAG,
            )
    }

    private fun showCreatePlaylistBottomSheet(track: Track) {
        CreatePlaylistBottomSheet()
            .setTracks(listOf(track))
            .setOnCreatePlaylistClickListener { dialog, playlist ->
                viewModel.onEvent(SearchUiEvent.OnCreatePlaylist(playlist))
                dialog.dismiss()
            }.show(
                childFragmentManager,
                CreatePlaylistBottomSheet.TAG,
            )
    }

    private fun playTrack(track: Track) {
        if (track.id != null && track.mediaUrl != null) {
            binding.searchView.setVisible(false)
            mainViewModel.onEvent(MainUiEvent.AddPlaylist(listOf(track), track.id))
            navController.navigateToPlayer()
        } else {
            showMessage(getString(R.string.error_unexpected))
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
