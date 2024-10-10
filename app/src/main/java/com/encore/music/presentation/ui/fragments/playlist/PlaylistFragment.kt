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
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.ui.fragments.dialog.CreatePlaylistBottomSheet
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: PlaylistViewModel by viewModel()

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
                        root.visibility = View.VISIBLE
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener {
                            viewModel.retry()
                        }
                    }
                }

                is PlaylistUiState.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE

                    val items =
                        buildList {
                            add(PlaylistListItem.HeaderItem(uiState.playlist))
                            uiState.playlist.tracks?.let { tracks ->
                                if (tracks.isEmpty()) {
                                    add(PlaylistListItem.EmptyTracksItem)
                                } else {
                                    addAll(tracks.map { PlaylistListItem.TracksItem(it) })
                                }
                            }
                        }
                    playlistAdapter.items = items
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
                        PlaylistUiEvent.NavigateUp -> navController.navigateUp()
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
                        viewModel.deletePlaylist()
                    } else {
                        viewModel.savePlaylist()
                    }
                    true
                }

                R.id.edit_playlist -> {
                    if (viewModel.uiState.value is PlaylistUiState.Success) {
                        val playlist = (viewModel.uiState.value as PlaylistUiState.Success).playlist
                        val createPlaylistBottomSheet =
                            CreatePlaylistBottomSheet(
                                onCreatePlaylist = { dialog, name, description ->
                                    viewModel.editPlaylist(name, description)
                                    dialog.dismiss()
                                },
                            ).apply {
                                name = playlist.name.orEmpty()
                                description = playlist.description.orEmpty()
                            }
                        createPlaylistBottomSheet.show(
                            childFragmentManager,
                            CreatePlaylistBottomSheet.TAG,
                        )
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
                            viewModel.deletePlaylist()
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
                onTrackClicked = { track ->
                    track.id?.let { id ->
                        viewModel.insertRecentTrack(track)
                        navController.navigateToPlayer(id)
                    }
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
}
