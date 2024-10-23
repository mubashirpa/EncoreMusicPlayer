package com.encore.music.presentation.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.encore.music.R
import com.encore.music.databinding.FragmentProfileBinding
import com.encore.music.presentation.navigation.navigateToOnboarding
import com.encore.music.presentation.navigation.navigateToPlaylist
import com.encore.music.presentation.ui.adapters.PlaylistsAdapter
import com.encore.music.presentation.utils.HorizontalItemDecoration
import com.encore.music.presentation.utils.ImageUtils
import com.encore.music.presentation.utils.PaddingValues
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

        val playlistAdapter =
            PlaylistsAdapter { playlist ->
                playlist.id?.let { id ->
                    navController.navigateToPlaylist(
                        playlistId = id,
                        isLocal = playlist.isLocal == true,
                    )
                }
            }
        binding.recyclerView.apply {
            addItemDecoration(
                HorizontalItemDecoration(
                    contentPadding =
                        PaddingValues(
                            start = 16,
                            top = 0,
                            end = 16,
                            bottom = 0,
                        ),
                    horizontalSpacing = 10,
                ),
            )
            adapter = playlistAdapter
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
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

        viewModel.createdPlaylists.observe(viewLifecycleOwner) { playlists ->
            binding.apply {
                if (playlists.isNotEmpty()) {
                    errorView.root.visibility = View.GONE
                    playlistAdapter.submitList(playlists)
                    recyclerView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.GONE
                    errorView.apply {
                        errorText.text = getString(R.string.no_playlists_found)
                        root.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { uiEvent ->
                    when (uiEvent) {
                        ProfileEvent.Logout -> navController.navigateToOnboarding()
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
