package com.encore.music.presentation.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.encore.music.core.ext.dpToPx
import com.encore.music.databinding.FragmentSearchBinding
import com.encore.music.domain.model.search.SearchType
import com.encore.music.presentation.navigation.navigateToArtist
import com.encore.music.presentation.navigation.navigateToCategory
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.navigation.navigateToProfile
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.SpanCount
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: SearchViewModel by viewModel()

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
                            viewModel.getCategories()
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
                    uiState.message?.let { message ->
                        binding.searchErrorView.apply {
                            root.visibility = View.VISIBLE
                            errorText.text = message.asString(requireContext())
                        }
                    }
                }

                is SearchUiState.Error -> {
                    binding.searchProgressCircular.visibility = View.GONE
                    binding.searchErrorView.apply {
                        root.visibility = View.VISIBLE
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener {
                            viewModel.search(
                                binding.searchView.text.toString(),
                                viewModel.searchType,
                                0,
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
            viewModel.search(binding.searchView.text.toString(), viewModel.searchType, 0)
        }

        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text.toString(), viewModel.searchType, 500)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSearchAdapter(): SearchAdapter {
        val searchAdapter =
            SearchAdapter(
                context = requireContext(),
                onArtistClick = { artist ->
                    artist.id?.let {
                        binding.searchView.setVisible(false)
                        navController.navigateToArtist(it)
                    }
                },
                onPlaylistClick = { playlist ->
                    playlist.id?.let {
                        binding.searchView.setVisible(false)
                        navController.navigateToPlaylist(it, playlist.isLocal == true)
                    }
                },
                onTrackClick = { track ->
                    track.id?.let {
                        binding.searchView.setVisible(false)
                        navController.navigateToPlayer(it)
                    }
                },
            )
        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
        return searchAdapter
    }
}
