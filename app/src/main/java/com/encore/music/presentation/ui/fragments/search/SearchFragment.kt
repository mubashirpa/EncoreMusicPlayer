package com.encore.music.presentation.ui.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.encore.music.databinding.FragmentSearchBinding
import com.encore.music.domain.model.categories.Category
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.ImageUtils

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

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

        val categories: MutableList<Category> = mutableListOf()
        repeat(20) {
            categories.add(
                Category(
                    "$it",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    "Category $it",
                ),
            )
        }
        val categoriesAdapter = CategoriesAdapter(categories)
        binding.recyclerView.apply {
            addItemDecoration(AdaptiveSpacingItemDecoration(12, true))
            adapter = categoriesAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
