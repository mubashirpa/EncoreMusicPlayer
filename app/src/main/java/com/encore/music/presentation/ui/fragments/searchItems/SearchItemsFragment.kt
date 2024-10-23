package com.encore.music.presentation.ui.fragments.searchItems

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.encore.music.R
import com.encore.music.core.PagingSourceException
import com.encore.music.core.ext.dpToPx
import com.encore.music.databinding.FragmentSearchItemsBinding
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.model.tracks.Track
import com.encore.music.presentation.navigation.navigateToArtist
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.ui.activities.MainUiEvent
import com.encore.music.presentation.ui.activities.MainViewModel
import com.encore.music.presentation.ui.adapters.LoaderStateAdapter
import com.encore.music.presentation.ui.fragments.dialog.AddToPlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.CreatePlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.MenuItem
import com.encore.music.presentation.ui.fragments.dialog.TrackMenuBottomSheet
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.SpanCount
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchItemsFragment : Fragment() {
    private var _binding: FragmentSearchItemsBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: SearchItemsViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.title = viewModel.searchQuery

        val adapter = initRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { loadStates ->
                    when (loadStates.refresh) {
                        is LoadState.Loading -> {
                            binding.apply {
                                if (adapter.itemCount == 0) {
                                    progressCircular.visibility = View.VISIBLE
                                }
                                errorView.root.visibility = View.GONE
                                recyclerView.visibility = View.GONE
                            }
                        }

                        is LoadState.Error -> {
                            binding.apply {
                                progressCircular.visibility = View.GONE
                                errorView.apply {
                                    val error =
                                        (loadStates.refresh as LoadState.Error).error as PagingSourceException
                                    errorText.text =
                                        error.localizedMessage.asString(requireContext())
                                    retryButton.visibility = View.VISIBLE
                                    root.visibility = View.VISIBLE

                                    retryButton.setOnClickListener {
                                        adapter.refresh()
                                    }
                                }
                            }
                        }

                        is LoadState.NotLoading -> {
                            binding.apply {
                                progressCircular.visibility = View.GONE
                                if (adapter.itemCount == 0) {
                                    recyclerView.visibility = View.GONE
                                    errorView.errorText.text =
                                        getString(R.string.no_results_for_, viewModel.searchQuery)
                                    errorView.root.visibility = View.VISIBLE
                                } else {
                                    errorView.root.visibility = View.GONE
                                    recyclerView.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }

        viewModel.searchItems.observe(viewLifecycleOwner) { items ->
            adapter.submitData(viewLifecycleOwner.lifecycle, items)
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(): SearchItemsAdapter {
        val searchItemsAdapter =
            SearchItemsAdapter(
                context = requireContext(),
                onArtistClicked = { artist ->
                    artist.id?.let {
                        navController.navigateToArtist(it)
                    }
                },
                onPlaylistClicked = { playlist ->
                    playlist.id?.let {
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
        val loaderStateAdapter =
            LoaderStateAdapter {
                searchItemsAdapter.retry()
            }
        val concatAdapter = searchItemsAdapter.withLoadStateFooter(loaderStateAdapter)

        binding.recyclerView.apply {
            layoutManager =
                if (viewModel.searchType == SearchType.PLAYLIST) {
                    val gridLayoutManager =
                        GridLayoutManager(
                            requireContext(),
                            SpanCount.adaptive(requireContext(), 160),
                        )
                    gridLayoutManager.spanSizeLookup =
                        object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int =
                                when (concatAdapter.getItemViewType(position)) {
                                    0 -> 1
                                    else -> gridLayoutManager.spanCount
                                }
                        }

                    addItemDecoration(
                        AdaptiveSpacingItemDecoration(
                            size = 12.dpToPx(requireContext()),
                            edgeEnabled = true,
                        ),
                    )
                    gridLayoutManager
                } else {
                    LinearLayoutManager(requireContext())
                }
            adapter = concatAdapter
        }
        return searchItemsAdapter
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
                viewModel.onEvent(SearchItemsUiEvent.OnInsertTrackToLocalPlaylist(playlist))
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
                viewModel.onEvent(SearchItemsUiEvent.OnCreatePlaylist(playlist))
                dialog.dismiss()
            }.show(
                childFragmentManager,
                CreatePlaylistBottomSheet.TAG,
            )
    }

    private fun playTrack(track: Track) {
        if (track.id != null && track.mediaUrl != null) {
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
