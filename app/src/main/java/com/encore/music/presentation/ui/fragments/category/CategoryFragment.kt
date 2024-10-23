package com.encore.music.presentation.ui.fragments.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.encore.music.R
import com.encore.music.core.PagingSourceException
import com.encore.music.core.ext.dpToPx
import com.encore.music.databinding.FragmentCategoryBinding
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.ui.adapters.LoaderStateAdapter
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.SpanCount
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.title = viewModel.title

        val adapter = initRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { loadStates ->
                    when (loadStates.refresh) {
                        is LoadState.Loading -> {
                            binding.apply {
                                if (adapter.itemCount == 0) {
                                    progressCircular.visibility = View.VISIBLE
                                }
                                errorView.root.visibility = View.GONE
                                recyclerView.visibility = View.GONE
                            }
                        }

                        is LoadState.Error -> {
                            binding.apply {
                                progressCircular.visibility = View.GONE
                                errorView.apply {
                                    val error =
                                        (loadStates.refresh as LoadState.Error).error as PagingSourceException
                                    errorText.text =
                                        error.localizedMessage.asString(requireContext())
                                    retryButton.visibility = View.VISIBLE
                                    root.visibility = View.VISIBLE

                                    retryButton.setOnClickListener {
                                        adapter.refresh()
                                    }
                                }
                            }
                        }

                        is LoadState.NotLoading -> {
                            binding.apply {
                                progressCircular.visibility = View.GONE
                                if (adapter.itemCount == 0) {
                                    recyclerView.visibility = View.GONE
                                    errorView.errorText.text =
                                        getString(R.string.no_playlists_found)
                                    errorView.root.visibility = View.VISIBLE
                                } else {
                                    errorView.root.visibility = View.GONE
                                    recyclerView.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }

        viewModel.playlists.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
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
        val loaderStateAdapter =
            LoaderStateAdapter {
                categoryAdapter.retry()
            }
        val concatAdapter = categoryAdapter.withLoadStateFooter(loaderStateAdapter)
        val gridLayoutManager =
            GridLayoutManager(
                requireContext(),
                SpanCount.adaptive(requireContext(), 120),
            )
        gridLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    when (concatAdapter.getItemViewType(position)) {
                        1 -> gridLayoutManager.spanCount
                        else -> 1
                    }
            }

        binding.recyclerView.apply {
            layoutManager = gridLayoutManager
            addItemDecoration(
                AdaptiveSpacingItemDecoration(
                    size = 12.dpToPx(requireContext()),
                    edgeEnabled = true,
                ),
            )
            adapter = concatAdapter
        }
        return categoryAdapter
    }
}
