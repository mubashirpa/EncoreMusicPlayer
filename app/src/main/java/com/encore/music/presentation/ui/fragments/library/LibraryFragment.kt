package com.encore.music.presentation.ui.fragments.library

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
import com.encore.music.databinding.FragmentLibraryBinding
import com.encore.music.domain.model.tracks.Track
import com.encore.music.presentation.navigation.navigateToArtist
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.navigation.navigateToProfile
import com.encore.music.presentation.ui.fragments.dialog.CreatePlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.MenuBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.MenuItem
import com.encore.music.presentation.ui.fragments.dialog.ProgressDialogFragment
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModel()
    private val navController by lazy { findNavController() }
    private val progressDialog by lazy { ProgressDialogFragment() }
    private val createPlaylistBottomSheet by lazy {
        CreatePlaylistBottomSheet(
            onCreatePlaylist = { name, description ->
                viewModel.createPlaylist(name, description)
            },
        )
    }

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

        val libraryAdapter =
            LibraryAdapter(
                context = requireContext(),
                items = mutableListOf(),
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
                    showTrackMenu(track)
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
                LibraryUiState.Empty -> {
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
                }
            }
        }

        viewModel.savedArtists.observe(viewLifecycleOwner) { artists ->
            if (artists.isNotEmpty()) {
                libraryAdapter.items.add(
                    0,
                    LibraryListItem.ArtistsItem(getString(R.string.artists), artists),
                )
            }

            if (artists.isNotEmpty()) {
                val libraryItems = libraryAdapter.items
                val firstItem = libraryItems.firstOrNull()
                if (firstItem is LibraryListItem.ArtistsItem) {
                    libraryAdapter.notifyArtistsDataChange(artists)
                } else {
                    libraryItems.add(
                        0,
                        LibraryListItem.ArtistsItem(
                            title = getString(R.string.artists),
                            artists = artists,
                        ),
                    )
                    libraryAdapter.notifyItemInserted(0)
                }
            }
        }

        viewModel.savedPlaylists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNotEmpty()) {
                val libraryItems = libraryAdapter.items
                val secondItem = libraryItems.getOrNull(1)
                if (secondItem is LibraryListItem.PlaylistsItem) {
                    libraryAdapter.notifyPlaylistsDataChange(playlists)
                } else {
                    val trackIndex =
                        libraryItems.indexOfFirst { it is LibraryListItem.TracksItem }
                    val index =
                        if (trackIndex == -1) libraryItems.size else trackIndex
                    libraryItems.add(
                        index,
                        LibraryListItem.PlaylistsItem(
                            title = getString(R.string.playlists),
                            playlists = playlists,
                        ),
                    )
                    libraryAdapter.notifyItemInserted(index)
                }
            }
        }

        viewModel.recentTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                val libraryItems = libraryAdapter.items
                val thirdItem = libraryItems.getOrNull(2)
                if (thirdItem is LibraryListItem.TracksItem) {
                    libraryAdapter.notifyTracksDataChange(tracks)
                } else {
                    libraryItems.add(
                        LibraryListItem.TracksItem(
                            title = getString(R.string.songs),
                            tracks = tracks,
                        ),
                    )
                    libraryAdapter.notifyItemInserted(libraryItems.size - 1)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        is LibraryUiEvent.OnOpenCreatePlaylistBottomSheetChange -> {
                            if (uiEvent.open) {
                                if (!createPlaylistBottomSheet.isAdded) {
                                    createPlaylistBottomSheet.show(
                                        childFragmentManager,
                                        CreatePlaylistBottomSheet.TAG,
                                    )
                                }
                            } else {
                                if (createPlaylistBottomSheet.isAdded) createPlaylistBottomSheet.dismiss()
                            }
                        }

                        is LibraryUiEvent.OnOpenProgressDialogChange -> {
                            if (uiEvent.open) {
                                if (!progressDialog.isAdded) {
                                    progressDialog.show(
                                        childFragmentManager,
                                        ProgressDialogFragment.TAG,
                                    )
                                }
                            } else {
                                if (progressDialog.isAdded) progressDialog.dismiss()
                            }
                        }

                        is LibraryUiEvent.OnShowSnackBar -> {
                            Snackbar
                                .make(
                                    binding.root,
                                    uiEvent.message.asString(requireContext()),
                                    Snackbar.LENGTH_LONG,
                                ).show()
                        }
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateToProfile()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_playlist -> {
                    createPlaylistBottomSheet.show(
                        childFragmentManager,
                        CreatePlaylistBottomSheet.TAG,
                    )
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

    private fun showTrackMenu(track: Track) {
        val artist = track.artists?.firstOrNull()
        val items =
            listOf(
                MenuItem(
                    getString(R.string.play_now),
                    R.drawable.baseline_play_arrow_24,
                ),
                MenuItem(
                    getString(R.string.play_next),
                    R.drawable.baseline_skip_next_24,
                ),
                MenuItem(
                    getString(R.string.add_to_queue),
                    R.drawable.baseline_add_to_queue_24,
                ),
                MenuItem(
                    getString(R.string.add_to_playlist),
                    R.drawable.baseline_playlist_add_24,
                ),
                MenuItem(
                    getString(
                        R.string.more_from_,
                        artist?.name.orEmpty(),
                    ),
                    R.drawable.baseline_person_search_24,
                ),
            )
        MenuBottomSheet(track, items)
            .apply {
                setOnMenuItemClickListener = { position ->
                    when (position) {
                        0 -> {
                            track.id?.let { id ->
                                navController.navigateToPlayer(id)
                            }
                        }

                        1 -> { // TODO
                        }

                        2 -> { // TODO
                        }

                        3 -> { // TODO
                        }

                        4 -> {
                            artist?.id?.let { navController.navigateToArtist(it) }
                        }
                    }
                }
            }.show(
                childFragmentManager,
                MenuBottomSheet.TAG,
            )
    }
}
