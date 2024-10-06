package com.encore.music.presentation.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.encore.music.databinding.FragmentProfileBinding
import com.encore.music.domain.model.authentication.User
import com.encore.music.presentation.navigation.navigateToOnboarding
import com.encore.music.presentation.utils.ImageUtils
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserObserver =
            Observer<User> { user ->
                binding.name.text = user.name
                binding.email.text = user.email

                ImageUtils.loadProfile(
                    context = requireContext(),
                    url = user.photoUrl,
                    onSuccess = { result ->
                        binding.profile.setImageDrawable(result)
                    },
                )
            }
        viewModel.currentUser.observe(viewLifecycleOwner, currentUserObserver)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        ProfileUiState.Logout -> {
                            navController.navigateToOnboarding()
                        }
                    }
                }
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        binding.logoutButton.setOnClickListener {
            viewModel.onEvent(ProfileUiEvent.Logout)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
