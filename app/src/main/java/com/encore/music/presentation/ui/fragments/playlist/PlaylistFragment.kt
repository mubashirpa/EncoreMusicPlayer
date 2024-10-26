package com.encore.music.presentation.ui.fragments.playlist

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
import com.encore.music.databinding.FragmentPlaylistBinding
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track
import com.encore.music.presentation.navigation.navigateToArtist
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.ui.activities.MainUiEvent
import com.encore.music.presentation.ui.activities.MainViewModel
import com.encore.music.presentation.ui.fragments.dialog.AddToPlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.CreatePlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.MenuItem
import com.encore.music.presentation.ui.fragments.dialog.TrackMenuBottomSheet
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: PlaylistViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val playlistAdapter = initRecyclerView()

        if (viewModel.isLocal) {
            binding.topAppBar.menu.setGroupVisible(R.id.local_playlist, true)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is PlaylistUiState.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.errorView.apply {
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE
                        root.visibility = View.VISIBLE

                        retryButton.setOnClickListener {
                            viewModel.onEvent(PlaylistUiEvent.OnRetry)
                        }
                    }
                }

                is PlaylistUiState.Success -> {
                    val items =
                        buildList {
                            add(PlaylistListItem.HeaderItem(uiState.playlist))
                            if (uiState.playlist.tracks.isNullOrEmpty()) {
                                add(PlaylistListItem.EmptyTracksItem)
                            } else {
                                addAll(uiState.playlist.tracks.map { PlaylistListItem.TracksItem(it) })
                            }
                        }
                    playlistAdapter.submitList(items) {
                        binding.progressCircular.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                }

                PlaylistUiState.Loading -> {
                    binding.errorView.root.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }

        viewModel.isSaved.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved) {
                binding.topAppBar.menu
                    .findItem(R.id.save_playlist)
                    .setIcon(R.drawable.baseline_favorite_24)
                    .isVisible = true
            } else {
                binding.topAppBar.menu
                    .findItem(R.id.save_playlist)
                    .setIcon(R.drawable.baseline_favorite_border_24)
                    .isVisible = true
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        PlaylistEvent.NavigateUp -> navController.navigateUp()
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save_playlist -> {
                    if (viewModel.isSaved.value == true) {
                        viewModel.onEvent(PlaylistUiEvent.OnUnSavePlaylist)
                    } else {
                        viewModel.onEvent(PlaylistUiEvent.OnSavePlaylist)
                    }
                    true
                }

                R.id.edit_playlist -> {
                    if (viewModel.uiState.value is PlaylistUiState.Success) {
                        val playlist = (viewModel.uiState.value as PlaylistUiState.Success).playlist
                        showEditPlaylistBottomSheet(playlist)
                        true
                    } else {
                        false
                    }
                }

                R.id.delete_playlist -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.delete_playlist)
                        .setMessage(R.string.are_you_sure_you_want_to_delete_this_playlist)
                        .setNegativeButton(R.string.cancel) { _, _ -> }
                        .setPositiveButton(R.string.delete) { _, _ ->
                            viewModel.onEvent(PlaylistUiEvent.OnDeleteLocalPlaylist)
                        }.show()
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(): PlaylistAdapter {
        val playlistAdapter =
            PlaylistAdapter(
                context = requireContext(),
                onShuffleClicked = {
                    playPlaylist(true)
                },
                onPlayClicked = {
                    playPlaylist(false)
                },
                onTrackClicked = { track ->
                    playTrack(track)
                },
                onTrackMoreClicked = { track ->
                    showTrackMenuBottomSheet(track)
                },
            )
        binding.recyclerView.apply {
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
                val insets =
                    windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                if (itemDecorationCount == 0) {
                    addItemDecoration(
                        VerticalItemDecoration(
                            contentPadding =
                                PaddingValues(
                                    start = insets.left,
                                    top = 0,
                                    end = insets.right,
                                    bottom = insets.bottom,
                                    convertToDp = false,
                                ),
                            verticalSpacing = 0,
                        ),
                    )
                }
                WindowInsetsCompat.CONSUMED
            }
            adapter = playlistAdapter
        }
        return playlistAdapter
    }

    private fun showTrackMenuBottomSheet(track: Track) {
        val artist = track.artists?.firstOrNull()
        val items =
            buildList {
                add(
                    MenuItem(
                        id = 0,
                        title = getString(R.string.play_now),
                        icon = R.drawable.baseline_play_arrow_24,
                    ),
                )
                add(
                    MenuItem(
                        id = 1,
                        title = getString(R.string.play_next),
                        icon = R.drawable.baseline_skip_next_24,
                    ),
                )
                add(
                    MenuItem(
                        id = 2,
                        title = getString(R.string.add_to_queue),
                        icon = R.drawable.baseline_add_to_queue_24,
                    ),
                )
                if (viewModel.isLocal) {
                    add(
                        MenuItem(
                            id = 3,
                            title = getString(R.string.remove_from_playlist),
                            icon = R.drawable.baseline_remove_circle_outline_24,
                        ),
                    )
                }
                add(
                    MenuItem(
                        id = 4,
                        title = getString(R.string.add_to_another_playlist),
                        icon = R.drawable.baseline_playlist_add_24,
                    ),
                )
                artist?.let {
                    add(
                        MenuItem(
                            id = 5,
                            title =
                                getString(
                                    R.string.more_from_,
                                    artist.name.orEmpty(),
                                ),
                            icon = R.drawable.baseline_person_search_24,
                        ),
                    )
                }
            }
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
                        track.id?.let {
                            viewModel.onEvent(PlaylistUiEvent.OnRemoveTrackFromLocalPlaylist(it))
                        }
                    }

                    4 -> {
                        showAddToPlaylistBottomSheet(track)
                    }

                    5 -> {
                        artist?.id?.let { navController.navigateToArtist(it) }
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
                viewModel.onEvent(PlaylistUiEvent.OnInsertTrackToLocalPlaylist(playlist))
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
                viewModel.onEvent(PlaylistUiEvent.OnCreatePlaylist(playlist))
                dialog.dismiss()
            }.show(
                childFragmentManager,
                CreatePlaylistBottomSheet.TAG,
            )
    }

    private fun showEditPlaylistBottomSheet(playlist: Playlist) {
        CreatePlaylistBottomSheet()
            .setPlaylist(playlist)
            .setOnUpdatePlaylistClickListener { dialog, updatedPlaylist ->
                viewModel.onEvent(PlaylistUiEvent.OnEditLocalPlaylist(updatedPlaylist))
                dialog.dismiss()
            }.show(
                childFragmentManager,
                CreatePlaylistBottomSheet.TAG,
            )
    }

    private fun playTrack(track: Track) {
        if (viewModel.uiState.value is PlaylistUiState.Success) {
            if (track.id != null && track.mediaUrl != null) {
                val tracks =
                    (viewModel.uiState.value as PlaylistUiState.Success).playlist.tracks!!
                mainViewModel.onEvent(MainUiEvent.AddPlaylist(tracks, track.id))
                navController.navigateToPlayer()
            } else {
                showMessage(getString(R.string.error_unexpected))
            }
        }
    }

    private fun playPlaylist(shuffle: Boolean) {
        if (viewModel.uiState.value is PlaylistUiState.Success) {
            val tracks =
                (viewModel.uiState.value as PlaylistUiState.Success).playlist.tracks
            if (!tracks.isNullOrEmpty()) {
                mainViewModel.onEvent(MainUiEvent.ChangeShuffleModeEnabled(shuffle))
                mainViewModel.onEvent(MainUiEvent.AddPlaylist(tracks))
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
