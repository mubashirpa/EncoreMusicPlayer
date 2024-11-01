package com.encore.music.presentation.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.encore.music.R
import com.encore.music.databinding.FragmentHomeBinding
import com.encore.music.domain.model.tracks.Track
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.navigation.navigateToProfile
import com.encore.music.presentation.ui.activities.MainUiEvent
import com.encore.music.presentation.ui.activities.MainViewModel
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: HomeViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModel()

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
                onTrackClicked = { track ->
                    playTrack(track)
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
                onStart = {
                    binding.topAppBar.setNavigationIcon(R.drawable.baseline_account_circle_24)
                },
                onSuccess = { result ->
                    binding.topAppBar.navigationIcon = result
                },
                onError = {
                    binding.topAppBar.setNavigationIcon(R.drawable.baseline_account_circle_24)
                },
            )
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is HomeUiState.Error -> {
                    binding.apply {
                        progressCircular.visibility = View.GONE
                        errorView.apply {
                            root.visibility = View.VISIBLE
                            errorText.text = uiState.message.asString(requireContext())
                            retryButton.apply {
                                visibility = View.VISIBLE
                                setOnClickListener { viewModel.retry() }
                            }
                        }
                    }
                }

                is HomeUiState.Success -> {
                    binding.apply {
                        progressCircular.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE

                        val newItems =
                            uiState.playlists.map {
                                HomeListItem.PlaylistsItem(
                                    title = it.title,
                                    playlists = it.playlists,
                                )
                            }

                        homeAdapter.items.addAll(newItems)
                        recyclerView.apply {
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
                }

                HomeUiState.Loading -> {
                    binding.apply {
                        errorView.root.visibility = View.GONE
                        progressCircular.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModel.topTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                val newItem = HomeListItem.TopTracksItem(tracks.take(6))
                when (homeAdapter.items.firstOrNull()) {
                    is HomeListItem.TopTracksItem -> {
                        homeAdapter.items[0] = newItem
                        homeAdapter.notifyItemChanged(0)
                    }

                    else -> {
                        homeAdapter.items.add(0, newItem)
                        homeAdapter.notifyItemInserted(0)
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

    private fun playTrack(track: Track) {
        if (track.id != null && track.mediaUrl != null) {
            val tracks = viewModel.topTracks.value!!
            mainViewModel.onEvent(MainUiEvent.AddPlaylist(tracks, track.id))
            navController.navigateToPlayer()
        } else {
            showMessage(getString(R.string.error_unexpected))
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
