package com.encore.music.presentation.ui.fragments.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.encore.music.R
import com.encore.music.core.ext.dpToPx
import com.encore.music.databinding.FragmentCategoryBinding
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.SpanCount
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: CategoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.title = viewModel.title

        val playlistAdapter = initRecyclerView()

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is CategoryUiState.Error -> {
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

                is CategoryUiState.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    playlistAdapter.items = uiState.playlists
                }

                CategoryUiState.Empty -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.errorView.apply {
                        root.visibility = View.VISIBLE
                        errorText.text = getString(R.string.no_playlists_found)
                    }
                }

                CategoryUiState.Loading -> {
                    binding.errorView.root.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
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

    private fun initRecyclerView(): CategoryAdapter {
        val categoryAdapter =
            CategoryAdapter(
                onPlaylistClicked = { playlist ->
                    playlist.id?.let { id ->
                        navController.navigateToPlaylist(
                            playlistId = id,
                            isLocal = playlist.isLocal == true,
                        )
                    }
                },
            )
        binding.recyclerView.apply {
            layoutManager =
                GridLayoutManager(
                    requireContext(),
                    SpanCount.adaptive(requireContext(), 120),
                )
            addItemDecoration(
                AdaptiveSpacingItemDecoration(
                    size = 12.dpToPx(requireContext()),
                    edgeEnabled = true,
                ),
            )
            adapter = categoryAdapter
        }
        return categoryAdapter
    }
}
