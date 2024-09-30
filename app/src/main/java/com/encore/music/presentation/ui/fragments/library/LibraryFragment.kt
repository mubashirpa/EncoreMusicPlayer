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
import com.encore.music.databinding.FragmentLibraryBinding
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModel()

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

        val libraryAdapter =
            LibraryAdapter(
                context = requireContext(),
                items =
                    mutableListOf(
                        LibraryListItem.ArtistsItem("Artists", viewModel.artists.value),
                        LibraryListItem.PlaylistsItem("Playlists", viewModel.playlists.value),
                        LibraryListItem.TracksItem("Songs", viewModel.tracks.value),
                    ),
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
                launch {
                    viewModel.artists.collect {
                        libraryAdapter.notifyArtistsDataChange(it)
                    }
                }

                launch {
                    viewModel.playlists.collect {
                        libraryAdapter.notifyPlaylistsDataChange(it)
                    }
                }

                launch {
                    viewModel.tracks.collect {
                        libraryAdapter.notifyTracksDataChange(it)
                    }
                }

                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        LibraryUiState.Error -> {
                        }

                        LibraryUiState.Loading -> {
                            binding.progressCircular.visibility = View.VISIBLE
                        }
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
