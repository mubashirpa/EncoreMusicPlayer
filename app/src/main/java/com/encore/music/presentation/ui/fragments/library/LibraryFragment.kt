package com.encore.music.presentation.ui.fragments.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.encore.music.R
import com.encore.music.databinding.FragmentLibraryBinding
import com.encore.music.domain.model.tracks.Track
import com.encore.music.presentation.navigation.navigateToArtist
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.navigation.navigateToProfile
import com.encore.music.presentation.ui.fragments.dialog.AddToPlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.CreatePlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.MenuItem
import com.encore.music.presentation.ui.fragments.dialog.TrackMenuBottomSheet
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModel()
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val libraryAdapter = initRecyclerView()

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
                LibraryUiState.Empty -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.errorView.apply {
                        root.visibility = View.VISIBLE
                        errorText.text = getString(R.string.no_recent_activity)
                    }
                }

                LibraryUiState.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                }

                LibraryUiState.Success -> {
                    binding.errorView.root.visibility = View.GONE
                    binding.progressCircular.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.savedArtists.observe(viewLifecycleOwner) { artists ->
            if (artists.isNotEmpty()) {
                (libraryAdapter.items[0] as LibraryListItem.ArtistsItem).artists = artists
                libraryAdapter.notifyItemChanged(0)
            }
        }

        viewModel.savedPlaylists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNotEmpty()) {
                (libraryAdapter.items[1] as LibraryListItem.PlaylistsItem).playlists = playlists
                libraryAdapter.notifyItemChanged(1)
            }
        }

        viewModel.recentTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                (libraryAdapter.items[2] as LibraryListItem.TracksItem).tracks = tracks
                libraryAdapter.notifyItemChanged(2)
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateToProfile()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_playlist -> {
                    showCreatePlaylistBottomSheet(null)
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

    private fun initRecyclerView(): LibraryAdapter {
        val items =
            mutableListOf(
                LibraryListItem.ArtistsItem(getString(R.string.artists), emptyList()),
                LibraryListItem.PlaylistsItem(getString(R.string.playlists), emptyList()),
                LibraryListItem.TracksItem(getString(R.string.songs), emptyList()),
            )
        val libraryAdapter =
            LibraryAdapter(
                context = requireContext(),
                items = items,
                onArtistClicked = { artist ->
                    artist.id?.let { id ->
                        navController.navigateToArtist(id)
                    }
                },
                onPlaylistClicked = { playlist ->
                    playlist.id?.let { id ->
                        navController.navigateToPlaylist(
                            playlistId = id,
                            isLocal = playlist.isLocal == true,
                        )
                    }
                },
                onTrackClicked = { track ->
                    track.id?.let { id ->
                        navController.navigateToPlayer(id)
                    }
                },
                onTrackMoreClicked = { track ->
                    showTrackMenuBottomSheet(track)
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
            adapter = libraryAdapter
        }
        return libraryAdapter
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
                        track.id?.let { navController.navigateToPlayer(it) }
                    }

                    1 -> { // TODO
                    }

                    2 -> { // TODO
                    }

                    3 -> {
                        showAddToPlaylistBottomSheet(track)
                    }

                    4 -> {
                        artist?.id?.let { navController.navigateToArtist(it) }
                    }
                }
            }.show(
                childFragmentManager,
                TrackMenuBottomSheet.TAG,
            )
    }

    private fun showAddToPlaylistBottomSheet(track: Track) {
        val playlists =
            viewModel.savedPlaylists.value
                .orEmpty()
                .filter { it.isLocal == true }
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
}
