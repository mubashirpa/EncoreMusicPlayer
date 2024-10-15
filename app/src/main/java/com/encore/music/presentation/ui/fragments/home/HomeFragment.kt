package com.encore.music.presentation.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.encore.music.R
import com.encore.music.databinding.FragmentHomeBinding
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.navigation.navigateToProfile
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: HomeViewModel by viewModel()

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

        val homeAdapter =
            HomeAdapter(
                context = requireContext(),
                items = mutableListOf(),
                onTrackClicked = {
                    navController.navigateToPlayer()
                },
                onPlaylistClicked = { playlist ->
                    playlist.id?.let { id ->
                        navController.navigateToPlaylist(
                            playlistId = id,
                            isLocal = playlist.isLocal == true,
                        )
                    }
                },
            )

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
                is HomeUiState.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.errorView.apply {
                        root.visibility = View.VISIBLE
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener {
                            viewModel.retry()
                        }
                    }
                }

                is HomeUiState.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE

                    homeAdapter.items.addAll(
                        uiState.playlists
                            .map {
                                HomeListItem.PlaylistsItem(
                                    title = it.title,
                                    playlists = it.playlists,
                                )
                            }.toMutableList(),
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
                }

                HomeUiState.Loading -> {
                    binding.errorView.root.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }

        viewModel.topTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                if (homeAdapter.items.firstOrNull() is HomeListItem.TopTracksItem) {
                    homeAdapter.items[0] = HomeListItem.TopTracksItem(tracks)
                    homeAdapter.notifyItemChanged(0)
                } else {
                    homeAdapter.items.add(0, HomeListItem.TopTracksItem(tracks))
                    homeAdapter.notifyItemInserted(0)
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
