package com.encore.music.presentation.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.encore.music.core.Navigation
import com.encore.music.databinding.FragmentSearchBinding
import com.encore.music.domain.model.authentication.User
import com.encore.music.presentation.navigation.navigateToProfile
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.ImageUtils
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

        val currentUserObserver =
            Observer<User> { user ->
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
        val uiStateObserver =
            Observer<CategoriesUiState> { uiState ->
                when (uiState) {
                    is CategoriesUiState.Error -> {
                        binding.progressCircular.visibility = View.GONE
                        binding.errorView.apply {
                            root.visibility = View.VISIBLE
                            errorText.text = uiState.message.asString(requireContext())
                            retryButton.visibility = View.VISIBLE
                            retryButton.setOnClickListener {
                                viewModel.onEvent(SearchUiEvent.OnRetry)
                            }
                        }
                    }

                    is CategoriesUiState.Success -> {
                        binding.progressCircular.visibility = View.GONE
                        binding.nestedScrollView.visibility = View.VISIBLE
                        val categoriesAdapter = CategoriesAdapter(uiState.categories)
                        binding.recyclerView.apply {
                            addItemDecoration(AdaptiveSpacingItemDecoration(12, true))
                            adapter = categoriesAdapter
                        }
                    }

                    CategoriesUiState.Empty -> Unit

                    CategoriesUiState.Loading -> {
                        binding.errorView.root.visibility = View.GONE
                        binding.progressCircular.visibility = View.VISIBLE
                    }
                }
            }

        viewModel.currentUser.observe(viewLifecycleOwner, currentUserObserver)
        viewModel.uiState.observe(viewLifecycleOwner, uiStateObserver)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
