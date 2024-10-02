package com.encore.music.presentation.ui.fragments.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.encore.music.databinding.FragmentArtistBinding
import com.encore.music.domain.model.spotify.artists.Artist
import com.encore.music.domain.model.spotify.tracks.Track
import com.encore.music.presentation.utils.PaddingValues
import com.encore.music.presentation.utils.VerticalItemDecoration

class ArtistFragment : Fragment() {
    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

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

        val items =
            mutableListOf(
                ArtistListItem.HeaderItem(
                    Artist(
                        name = "Artist Name",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                ),
                ArtistListItem.TracksItem(
                    Track(
                        name = "Track Name",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                ),
                ArtistListItem.TracksItem(
                    Track(
                        name = "Track Name",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                ),
                ArtistListItem.TracksItem(
                    Track(
                        name = "Track Name",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                ),
                ArtistListItem.TracksItem(
                    Track(
                        name = "Track Name",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                ),
                ArtistListItem.TracksItem(
                    Track(
                        name = "Track Name",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                ),
                ArtistListItem.TracksItem(
                    Track(
                        name = "Track Name",
                        imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm2-IiCQnnEHH1dk5HN2K60xrv8Wyu8VRW7Q&s",
                    ),
                ),
            )
        val artistAdapter = ArtistAdapter(requireContext(), items)

        binding.recyclerView.apply {
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                if (itemDecorationCount == 0) {
                    addItemDecoration(
                        VerticalItemDecoration(
                            contentPadding =
                                PaddingValues(
                                    start = insets.left,
                                    top = insets.top,
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}