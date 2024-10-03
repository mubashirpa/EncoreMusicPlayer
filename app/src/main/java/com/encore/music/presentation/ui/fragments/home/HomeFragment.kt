package com.encore.music.presentation.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.encore.music.R
import com.encore.music.databinding.FragmentHomeBinding
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()
    private val navController by lazy { findNavController() }
    private val homeAdapter by lazy {
        HomeAdapter(
            context = requireContext(),
            items = mutableListOf(),
            onTrackClicked = { track ->
                navController.navigateToPlayer(track.id)
            },
            onPlaylistClicked = { playlist ->
                navController.navigateToPlaylist(playlist.id)
            },
        )
    }
    private var popularItem: HomeListItem.PlaylistsItem? = null
    private var trendingItem: HomeListItem.PlaylistsItem? = null
    private var topChartsItem: HomeListItem.PlaylistsItem? = null
    private var newReleasesItem: HomeListItem.PlaylistsItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        ImageUtils.loadProfile(
            context = requireContext(),
            url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
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

        binding.recyclerView.apply {
            addItemDecoration(
                VerticalItemDecoration(
                    contentPadding =
                        PaddingValues(
                            start = 0,
                            top = 12,
                            end = 0,
                            bottom = 12,
                        ),
                    verticalSpacing = 16,
                ),
            )
            adapter = homeAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val homeItems = homeAdapter.items

                launch {
                    viewModel.popularPlaylists.collect { popular ->
                        if (popular.isNotEmpty()) {
                            if (popularItem == null) {
                                popularItem =
                                    HomeListItem.PlaylistsItem(
                                        title = getString(R.string.popular),
                                        playlists = popular,
                                    )
                                homeItems.add(popularItem!!)
                                homeAdapter.notifyItemInserted(homeItems.size - 1)
                            } else {
                                homeAdapter.notifyPlaylistsDataChange(popular)
                            }
                        }
                    }
                }

                launch {
                    viewModel.trendingPlaylists.collect { trending ->
                        if (trending.isNotEmpty()) {
                            if (trendingItem == null) {
                                trendingItem =
                                    HomeListItem.PlaylistsItem(
                                        title = getString(R.string.trending),
                                        playlists = trending,
                                    )
                                homeItems.add(trendingItem!!)
                                homeAdapter.notifyItemInserted(homeItems.size - 1)
                            } else {
                                homeAdapter.notifyPlaylistsDataChange(trending)
                            }
                        }
                    }
                }

                launch {
                    viewModel.topChartsPlaylists.collect { topCharts ->
                        if (topCharts.isNotEmpty()) {
                            if (topChartsItem == null) {
                                topChartsItem =
                                    HomeListItem.PlaylistsItem(
                                        title = getString(R.string.top_charts_title_case),
                                        playlists = topCharts,
                                    )
                                homeItems.add(topChartsItem!!)
                                homeAdapter.notifyItemInserted(homeItems.size - 1)
                            } else {
                                homeAdapter.notifyPlaylistsDataChange(topCharts)
                            }
                        }
                    }
                }

                launch {
                    viewModel.newReleasesPlaylists.collect { newReleases ->
                        if (newReleases.isNotEmpty()) {
                            if (newReleasesItem == null) {
                                newReleasesItem =
                                    HomeListItem.PlaylistsItem(
                                        title = getString(R.string.new_releases_title_case),
                                        playlists = newReleases,
                                    )
                                homeItems.add(newReleasesItem!!)
                                homeAdapter.notifyItemInserted(homeItems.size - 1)
                            } else {
                                homeAdapter.notifyPlaylistsDataChange(newReleases)
                            }
                        }
                    }
                }

                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        HomeUiState.Empty -> {}

                        HomeUiState.Error -> TODO()

                        HomeUiState.Loading -> {
                            binding.progressCircular.visibility = View.VISIBLE
                        }

                        HomeUiState.Success -> {
                            binding.progressCircular.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
