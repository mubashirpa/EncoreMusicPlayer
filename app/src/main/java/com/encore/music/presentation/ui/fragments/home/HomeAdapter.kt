package com.encore.music.presentation.ui.fragments.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.encore.music.R
import com.encore.music.core.ext.dpToPx
import com.encore.music.databinding.LayoutHomePlaylistsBinding
import com.encore.music.databinding.LayoutHomeTopTracksBinding
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track
import com.encore.music.presentation.ui.adapters.PlaylistsAdapter
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.HorizontalItemDecoration
import com.encore.music.presentation.utils.PaddingValues

class HomeAdapter(
    private val context: Context,
    private val onTrackClicked: ((Track) -> Unit)? = null,
    private val onPlaylistClicked: ((Playlist) -> Unit)? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: MutableList<HomeListItem> = mutableListOf()

    inner class TopTracksViewHolder(
        binding: LayoutHomeTopTracksBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val topTracksAdapter = TopTracksAdapter(onTrackClicked)

        init {
            binding.recyclerView.apply {
                if (itemDecorationCount == 0) {
                    addItemDecoration(AdaptiveSpacingItemDecoration(10.dpToPx(context), false))
                }
                adapter = topTracksAdapter
            }
        }

        fun bind(item: HomeListItem.TopTracksItem) {
            topTracksAdapter.submitList(item.tracks)
        }
    }

    inner class PlaylistsViewHolder(
        private val binding: LayoutHomePlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val playlistsAdapter = PlaylistsAdapter(onPlaylistClicked)

        init {
            binding.recyclerView.apply {
                if (itemDecorationCount == 0) {
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
                }
                adapter = playlistsAdapter
            }
        }

        fun bind(item: HomeListItem.PlaylistsItem) {
            playlistsAdapter.submitList(item.playlists)
            binding.title.text = item.title
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> {
                val binding =
                    LayoutHomeTopTracksBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                TopTracksViewHolder(binding)
            }

            1 -> {
                val binding =
                    LayoutHomePlaylistsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                PlaylistsViewHolder(binding)
            }

            else -> throw IllegalArgumentException(context.getString(R.string.invalid_view_type))
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = items[position]) {
            is HomeListItem.TopTracksItem -> (holder as TopTracksViewHolder).bind(item)
            is HomeListItem.PlaylistsItem -> (holder as PlaylistsViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is HomeListItem.TopTracksItem -> 0
            is HomeListItem.PlaylistsItem -> 1
        }
}
