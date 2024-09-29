package com.encore.music.presentation.ui.fragments.home

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
import com.encore.music.R
import com.encore.music.databinding.FragmentHomeBinding
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    val homeAdapter =
                        HomeAdapter(
                            listOf(
                                HomeListItem.TopTracksItem(uiState.popularPlaylists.take(6)),
                                HomeListItem.PlaylistsItem(
                                    title = "Popular",
                                    playlists = uiState.popularPlaylists,
                                ),
                                HomeListItem.PlaylistsItem(
                                    title = "Trending",
                                    playlists = uiState.popularPlaylists,
                                ),
                                HomeListItem.PlaylistsItem(
                                    title = "Top Charts",
                                    playlists = uiState.popularPlaylists,
                                ),
                                HomeListItem.PlaylistsItem(
                                    title = "New Releases",
                                    playlists = uiState.popularPlaylists,
                                ),
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
                                verticalSpacing = 12,
                            ),
                        )
                        adapter = homeAdapter
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
