package com.encore.music.presentation.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.encore.music.core.Navigation
import com.encore.music.core.ext.dpToPx
import com.encore.music.databinding.FragmentSearchBinding
import com.encore.music.domain.model.search.SearchType
import com.encore.music.presentation.navigation.navigateToCategory
import com.encore.music.presentation.navigation.navigateToProfile
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.SpanCount
import com.encore.music.presentation.utils.setNavigationResult
import com.google.android.material.search.SearchView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.chipGroup.check(
            when (viewModel.searchType) {
                SearchType.ARTIST -> binding.artistsFilter.id
                SearchType.PLAYLIST -> binding.playlistsFilter.id
                SearchType.TRACK -> binding.songsFilter.id
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
                is CategoriesUiState.Error -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.errorView.apply {
                        root.visibility = View.VISIBLE
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener {
                            viewModel.getCategories()
                        }
                    }
                }

                is CategoriesUiState.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.nestedScrollView.visibility = View.VISIBLE

                    val categoriesAdapter =
                        CategoriesAdapter(
                            uiState.categories,
                            onItemClick = { category ->
                                category.id?.let { categoryId ->
                                    navController.navigateToCategory(
                                        categoryId,
                                        category.name.orEmpty(),
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
                        adapter = categoriesAdapter
                    }
                }

                CategoriesUiState.Loading -> {
                    binding.errorView.root.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }

        viewModel.searchState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is SearchUiState.Empty -> {
                    binding.searchProgressCircular.visibility = View.GONE
                    binding.searchRecyclerView.visibility = View.GONE
                    uiState.message?.let { message ->
                        binding.searchErrorView.apply {
                            root.visibility = View.VISIBLE
                            errorText.text = message.asString(requireContext())
                        }
                    }
                }

                is SearchUiState.Error -> {
                    binding.searchProgressCircular.visibility = View.GONE
                    binding.searchErrorView.apply {
                        root.visibility = View.VISIBLE
                        errorText.text = uiState.message.asString(requireContext())
                        retryButton.visibility = View.VISIBLE
                        retryButton.setOnClickListener {
                            // TODO: Implement retry logic
                        }
                    }
                }

                SearchUiState.Loading -> {
                    binding.searchRecyclerView.visibility = View.GONE
                    binding.searchErrorView.root.visibility = View.GONE
                    binding.searchProgressCircular.visibility = View.VISIBLE
                }

                is SearchUiState.Success -> {
                    binding.searchProgressCircular.visibility = View.GONE
                    binding.searchRecyclerView.visibility = View.VISIBLE

                    val searchAdapter =
                        SearchAdapter(requireContext(), uiState.searchListItem)
                    binding.searchRecyclerView.adapter = searchAdapter
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateToProfile()
        }

        binding.searchView.addTransitionListener { _, _, newState ->
            when (newState) {
                SearchView.TransitionState.HIDING -> {
                    navController.setNavigationResult(
                        Navigation.Args.MAIN_BOTTOM_NAVIGATION_VISIBILITY,
                        true,
                    )
                }

                SearchView.TransitionState.SHOWING -> {
                    navController.setNavigationResult(
                        Navigation.Args.MAIN_BOTTOM_NAVIGATION_VISIBILITY,
                        false,
                    )
                }

                else -> Unit
            }
        }

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            viewModel.searchType =
                when (checkedIds.firstOrNull()) {
                    binding.playlistsFilter.id -> SearchType.PLAYLIST
                    binding.artistsFilter.id -> SearchType.ARTIST
                    else -> SearchType.TRACK
                }
            viewModel.search(binding.searchView.text.toString(), viewModel.searchType, 500)
        }

        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            viewModel.search(text.toString(), viewModel.searchType, 500)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
