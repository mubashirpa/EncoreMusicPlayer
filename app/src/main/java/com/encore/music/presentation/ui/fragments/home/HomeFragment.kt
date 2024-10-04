package com.encore.music.presentation.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.encore.music.R
import com.encore.music.databinding.FragmentHomeBinding
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.navigation.navigateToProfile
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: HomeViewModel by viewModel()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

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
                launch {
                    viewModel.currentUser.collect {
                        if (it != null) {
                            ImageUtils.loadProfile(
                                context = requireContext(),
                                url = it.photoUrl,
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
                    }
                }

                launch {
                    viewModel.topTracks.collect { tracks ->
                        if (tracks.isNotEmpty() && homeAdapter.items.firstOrNull() !is HomeListItem.TopTracksItem) {
                            homeAdapter.items.add(0, HomeListItem.TopTracksItem(tracks))
                            homeAdapter.notifyItemInserted(0)
                        }
                    }
                }

                launch {
                    viewModel.uiState.collect { uiState ->
                        when (uiState) {
                            is HomeUiState.Error -> {
                                binding.progressCircular.visibility = View.GONE
                                binding.recyclerView.visibility = View.GONE
                                // TODO: Show error screen
                                Snackbar
                                    .make(
                                        requireContext(),
                                        binding.root,
                                        uiState.message.asString(requireContext()),
                                        Snackbar.LENGTH_SHORT,
                                    ).show()
                            }

                            is HomeUiState.Success -> {
                                binding.progressCircular.visibility = View.GONE

                                val positionStart = homeAdapter.items.size
                                homeAdapter.items.addAll(
                                    uiState.playlists
                                        .map {
                                            HomeListItem.PlaylistsItem(
                                                title = it.title,
                                                playlists = it.playlists,
                                            )
                                        }.toMutableList(),
                                )
                                homeAdapter.notifyItemRangeInserted(
                                    positionStart,
                                    uiState.playlists.size,
                                )
                            }

                            HomeUiState.Empty -> {}

                            HomeUiState.Loading -> {
                                binding.progressCircular.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateToProfile()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
