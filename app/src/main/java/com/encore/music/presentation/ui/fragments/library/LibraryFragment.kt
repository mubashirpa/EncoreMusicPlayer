package com.encore.music.presentation.ui.fragments.library

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
import com.encore.music.databinding.FragmentLibraryBinding
import com.encore.music.presentation.navigation.Screen
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModel()
    private val libraryAdapter by lazy {
        LibraryAdapter(
            context = requireContext(),
            items = mutableListOf(),
            onArtistClicked = { /*TODO*/ },
            onPlaylistClicked = { playlist ->
                findNavController().navigate(Screen.Playlist(playlist.id))
            },
            onTrackClicked = { /*TODO*/ },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        ImageUtils.loadProfile(
            context = requireContext(),
            url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val libraryItems = libraryAdapter.items

                launch {
                    viewModel.artists.collect { artists ->
                        if (artists.isNotEmpty()) {
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
                }

                launch {
                    viewModel.playlists.collect { playlists ->
                        if (playlists.isNotEmpty()) {
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
                }

                launch {
                    viewModel.tracks.collect { tracks ->
                        if (tracks.isNotEmpty()) {
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
                }

                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        LibraryUiState.Error -> TODO()

                        LibraryUiState.Loading -> {
                            binding.progressCircular.visibility = View.VISIBLE
                        }

                        LibraryUiState.Success -> {
                            binding.progressCircular.visibility = View.GONE
                        }

                        LibraryUiState.Empty -> TODO()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
