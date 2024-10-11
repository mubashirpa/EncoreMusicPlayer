package com.encore.music.presentation.ui.fragments.artist

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
import com.encore.music.presentation.navigation.navigateToArtist
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.ui.fragments.dialog.AddToPlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.CreatePlaylistBottomSheet
import com.encore.music.presentation.ui.fragments.dialog.MenuItem
import com.encore.music.presentation.ui.fragments.dialog.TrackMenuBottomSheet
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArtistFragment : Fragment() {
    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: ArtistViewModel by viewModel()

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
                        root.visibility = View.VISIBLE
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener {
                            viewModel.retry()
                        }
                    }
                }

                is ArtistUiState.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE

                    val items =
                        buildList {
                            add(
                                ArtistListItem.HeaderItem(
                                    artist = uiState.artist,
                                    isFollowed = uiState.isFollowed,
                                ),
                            )
                            uiState.artist.tracks?.let { tracks ->
                                if (tracks.isEmpty()) {
                                    add(ArtistListItem.EmptyTracksItem)
                                } else {
                                    addAll(tracks.map { ArtistListItem.TracksItem(it) })
                                }
                            }
                        }
                    artistAdapter.items = items
                }

                ArtistUiState.Loading -> {
                    binding.errorView.root.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }

        viewModel.isFollowed.observe(viewLifecycleOwner) { isFollowed ->
            artistAdapter.items.firstOrNull()?.let {
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
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
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
            adapter = artistAdapter
        }
        return artistAdapter
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
}
