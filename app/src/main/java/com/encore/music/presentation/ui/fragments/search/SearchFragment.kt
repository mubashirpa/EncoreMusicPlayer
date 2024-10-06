package com.encore.music.presentation.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.encore.music.databinding.FragmentSearchBinding
import com.encore.music.domain.model.authentication.User
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.ImageUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
