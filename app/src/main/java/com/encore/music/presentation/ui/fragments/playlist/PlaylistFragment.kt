package com.encore.music.presentation.ui.fragments.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.encore.music.R
import com.encore.music.databinding.FragmentPlaylistBinding
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
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

        val uiStateObserver =
            Observer<PlaylistUiState> { uiState ->
                when (uiState) {
                    is PlaylistUiState.Error -> {
                        binding.progressCircular.visibility = View.GONE
                        binding.errorView.apply {
                            root.visibility = View.VISIBLE
                            errorText.text = uiState.message.asString(requireContext())
                            retryButton.visibility = View.VISIBLE
                            retryButton.setOnClickListener {
                                viewModel.onEvent(PlaylistUiEvent.OnRetry)
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
                                    addAll(tracks.map { PlaylistListItem.TracksItem(it) })
                                }
                            }.toMutableList()
                        initRecyclerView(items)
                    }

                    PlaylistUiState.Empty -> Unit

                    PlaylistUiState.Loading -> {
                        binding.errorView.root.visibility = View.GONE
                        binding.progressCircular.visibility = View.VISIBLE
                    }
                }
            }
        viewModel.uiState.observe(viewLifecycleOwner, uiStateObserver)

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorite -> {
                    viewModel.onEvent(PlaylistUiEvent.SavePlaylist)
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

    private fun initRecyclerView(items: MutableList<PlaylistListItem>) {
        val playlistAdapter =
            PlaylistAdapter(
                context = requireContext(),
                items = items,
                onTrackClicked = { track ->
                    track.id?.let { id ->
                        viewModel.onEvent(PlaylistUiEvent.AddTrackToPlaylist(track))
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
    }
}
