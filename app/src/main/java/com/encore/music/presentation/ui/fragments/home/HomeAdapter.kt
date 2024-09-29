package com.encore.music.presentation.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.encore.music.databinding.LayoutHomePlaylistsBinding
import com.encore.music.databinding.LayoutHomeTopTracksBinding
import com.encore.music.presentation.utils.AdaptiveSpacingItemDecoration
import com.encore.music.presentation.utils.HorizontalItemDecoration
import com.encore.music.presentation.utils.PaddingValues

class HomeAdapter(
    private val items: List<HomeListItem>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class TopTracksViewHolder(
        private val binding: LayoutHomeTopTracksBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeListItem.TopTracksItem) {
            val topTracksAdapter = TopTracksAdapter(item.tracks)
            binding.recyclerView.apply {
                addItemDecoration(AdaptiveSpacingItemDecoration(10, false))
                adapter = topTracksAdapter
            }
        }
    }

    class PlaylistsViewHolder(
        private val binding: LayoutHomePlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeListItem.PlaylistsItem) {
            val playlistsAdapter = PlaylistsAdapter(item.playlists)
            binding.title.text = item.title
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
                adapter = playlistsAdapter
            }
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

            else -> throw IllegalArgumentException("Invalid view type")
        }

    override fun getItemCount(): Int = items.count()

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
