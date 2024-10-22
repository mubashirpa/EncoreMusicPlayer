package com.encore.music.presentation.ui.fragments.searchItems

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.encore.music.R
import com.encore.music.core.PagingSourceException
import com.encore.music.core.ext.dpToPx
import com.encore.music.databinding.FragmentSearchItemsBinding
import com.encore.music.domain.model.search.SearchType
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.SpanCount
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchItemsFragment : Fragment() {
    private var _binding: FragmentSearchItemsBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: SearchItemsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchItemsBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.title = viewModel.searchQuery

        val adapter = initRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Loading -> {
                        if (adapter.itemCount == 0) {
                            binding.progressCircular.visibility = View.VISIBLE
                        }
                    }

                    is LoadState.Error -> {
                        binding.progressCircular.visibility = View.GONE
                        binding.errorView.apply {
                            val error =
                                (loadStates.refresh as LoadState.Error).error as PagingSourceException
                            errorText.text = error.localizedMessage.asString(requireContext())
                            retryButton.visibility = View.VISIBLE
                            root.visibility = View.VISIBLE

                            retryButton.setOnClickListener {
                                adapter.refresh()
                            }
                        }
                    }

                    is LoadState.NotLoading -> {
                        binding.progressCircular.visibility = View.GONE

                        if (adapter.itemCount == 0) {
                            binding.errorView.errorText.text =
                                getString(R.string.no_results_for_, viewModel.searchQuery)
                            binding.errorView.root.visibility = View.VISIBLE
                        } else {
                            binding.errorView.root.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        viewModel.searchItems.observe(viewLifecycleOwner) { items ->
            adapter.submitData(viewLifecycleOwner.lifecycle, items)
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(): SearchItemsAdapter {
        val searchItemsAdapter =
            SearchItemsAdapter(
                context = requireContext(),
            )
        val loaderStateAdapter =
            LoaderStateAdapter {
                searchItemsAdapter.retry()
            }

        binding.recyclerView.apply {
            layoutManager =
                if (viewModel.searchType == SearchType.PLAYLIST) {
                    addItemDecoration(
                        AdaptiveSpacingItemDecoration(
                            size = 12.dpToPx(requireContext()),
                            edgeEnabled = true,
                        ),
                    )
                    GridLayoutManager(
                        requireContext(),
                        SpanCount.adaptive(requireContext(), 160),
                    )
                } else {
                    LinearLayoutManager(requireContext())
                }
            adapter = searchItemsAdapter.withLoadStateFooter(loaderStateAdapter)
        }
        return searchItemsAdapter
    }
}
