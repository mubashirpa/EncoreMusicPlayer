package com.encore.music.presentation.ui.fragments.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.encore.music.databinding.FragmentArtistBinding
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArtistFragment : Fragment() {
    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val viewModel: ArtistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val artistAdapter = initRecyclerView()

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is ArtistUiState.Error -> {
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

                is ArtistUiState.Success -> {
                    binding.progressCircular.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE

                    val items =
                        buildList {
                            add(
                                ArtistListItem.HeaderItem(
                                    artist = uiState.artist,
                                    isFollowed = uiState.isFollowed,
                                ),
                            )
                            uiState.artist.tracks?.let { tracks ->
                                if (tracks.isEmpty()) {
                                    add(ArtistListItem.EmptyTracksItem)
                                } else {
                                    addAll(tracks.map { ArtistListItem.TracksItem(it) })
                                }
                            }
                        }
                    artistAdapter.items = items
                }

                ArtistUiState.Loading -> {
                    binding.errorView.root.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                }
            }
        }

        viewModel.isFollowed.observe(viewLifecycleOwner) { isFollowed ->
            artistAdapter.items.firstOrNull()?.let {
                if (it is ArtistListItem.HeaderItem) {
                    it.isFollowed = isFollowed
                    artistAdapter.notifyItemChanged(0)
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

    private fun initRecyclerView(): ArtistAdapter {
        val artistAdapter =
            ArtistAdapter(
                context = requireContext(),
                onFollowArtistClicked = { artist, isFollowed ->
                    if (isFollowed) {
                        viewModel.unfollowArtist(artist)
                    } else {
                        viewModel.followArtist(artist)
                    }
                },
                onTrackClicked = { track ->
                    track.id?.let { id ->
                        navController.navigateToPlayer(id)
                    }
                },
            )

        binding.recyclerView.apply {
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                if (itemDecorationCount == 0) {
                    addItemDecoration(
                        VerticalItemDecoration(
                            contentPadding =
                                PaddingValues(
                                    start = insets.left,
                                    top = 0,
                                    end = insets.right,
                                    bottom = insets.bottom,
                                    convertToDp = false,
                                ),
                            verticalSpacing = 0,
                        ),
                    )
                }
                WindowInsetsCompat.CONSUMED
            }
            adapter = artistAdapter
        }
        return artistAdapter
    }
}
