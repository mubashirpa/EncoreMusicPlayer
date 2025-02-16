package com.encore.music.presentation.ui.fragments.artist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.encore.music.R
import com.encore.music.databinding.FragmentArtistBinding
import com.encore.music.domain.model.tracks.Track
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.ui.activities.MainUiEvent
import com.encore.music.presentation.ui.activities.MainViewModel
import com.encore.music.presentation.ui.fragments.dialog.AddToPlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.CreatePlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.MenuItem
import com.encore.music.presentation.ui.fragments.dialog.TrackMenuBottomSheet
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArtistFragment : Fragment() {
    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: ArtistViewModel by viewModel()
    private val mainViewModel: MainViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val artistAdapter = initRecyclerView()

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is ArtistUiState.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.errorView.apply {
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE
                        root.visibility = View.VISIBLE

                        retryButton.setOnClickListener {
                            viewModel.retry()
                        }
                    }
                }

                is ArtistUiState.Success -> {
                    val items =
                        buildList {
                            add(
                                ArtistListItem.HeaderItem(
                                    artist = uiState.artist,
                                    isFollowed = uiState.isFollowed,
                                ),
                            )
                            if (uiState.artist.tracks.isNullOrEmpty()) {
                                add(ArtistListItem.EmptyTracksItem)
                            } else {
                                addAll(uiState.artist.tracks.map { ArtistListItem.TracksItem(it) })
                            }
                        }
                    artistAdapter.submitList(items) {
                        binding.progressCircular.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                }

                ArtistUiState.Loading -> {
                    binding.errorView.root.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }

        viewModel.isFollowed.observe(viewLifecycleOwner) { isFollowed ->
            artistAdapter.currentList.firstOrNull()?.let {
                if (it is ArtistListItem.HeaderItem) {
                    it.isFollowed = isFollowed
                    artistAdapter.notifyItemChanged(0)
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(): ArtistAdapter {
        val artistAdapter =
            ArtistAdapter(
                context = requireContext(),
                onFollowArtistClicked = { artist, isFollowed ->
                    if (isFollowed) {
                        viewModel.unfollowArtist(artist)
                    } else {
                        viewModel.followArtist(artist)
                    }
                },
                onOpenInClicked = { externalUrl ->
                    externalUrl?.let {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        try {
                            startActivity(intent)
                        } catch (_: Exception) {
                            showMessage(getString(R.string.unable_to_open_url))
                        }
                    } ?: run {
                        showMessage(getString(R.string.error_unexpected))
                    }
                },
                onPlayClicked = {
                    playPlaylist()
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
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
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
                WindowInsetsCompat.CONSUMED
            }
            adapter = artistAdapter
        }
        return artistAdapter
    }

    private fun showTrackMenuBottomSheet(track: Track) {
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
                viewModel.savePlaylist(playlist)
                dialog.dismiss()
            }.show(
                childFragmentManager,
                AddToPlaylistBottomSheet.TAG,
            )
    }

    private fun showCreatePlaylistBottomSheet(track: Track?) {
        CreatePlaylistBottomSheet()
            .apply {
                track?.let { setTracks(listOf(it)) }
            }.setOnCreatePlaylistClickListener { dialog, playlist ->
                viewModel.createPlaylist(playlist)
                dialog.dismiss()
            }.show(
                childFragmentManager,
                CreatePlaylistBottomSheet.TAG,
            )
    }

    private fun playTrack(track: Track) {
        if (viewModel.uiState.value is ArtistUiState.Success) {
            if (track.id != null && track.mediaUrl != null) {
                val tracks =
                    (viewModel.uiState.value as ArtistUiState.Success).artist.tracks!!
                mainViewModel.onEvent(MainUiEvent.AddPlaylist(tracks, track.id))
                navController.navigateToPlayer()
            } else {
                showMessage(getString(R.string.error_unexpected))
            }
        }
    }

    private fun playPlaylist() {
        if (viewModel.uiState.value is ArtistUiState.Success) {
            val tracks =
                (viewModel.uiState.value as ArtistUiState.Success).artist.tracks
            if (!tracks.isNullOrEmpty()) {
                mainViewModel.onEvent(MainUiEvent.ChangeShuffleModeEnabled(false))
                mainViewModel.onEvent(MainUiEvent.AddPlaylist(tracks))
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
