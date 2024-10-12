package com.encore.music.presentation.ui.fragments.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemArtistsHorizontalBinding
import com.encore.music.databinding.ListItemPlaylistsBinding
import com.encore.music.databinding.ListItemTracksDetailedBinding
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

class SearchAdapter(
    private val context: Context,
    private val item: SearchListItem,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ArtistsViewHolder(
        private val binding: ListItemArtistsHorizontalBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artist) {
            binding.apply {
                media.load(item.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                title.text = item.name
            }
        }
    }

    inner class PlaylistsViewHolder(
        private val binding: ListItemPlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Playlist) {
            binding.apply {
                media.load(item.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                title.text = item.name
                subtitle.text = item.owner
            }
        }
    }

    inner class TracksViewHolder(
        private val binding: ListItemTracksDetailedBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Track) {
            binding.apply {
                leadingImage.load(item.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                headlineText.text = item.name
                supportingText.text = item.artists?.joinToString { it.name.orEmpty() }
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
                    ListItemArtistsHorizontalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                ArtistsViewHolder(binding)
            }

            1 -> {
                val binding =
                    ListItemPlaylistsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                PlaylistsViewHolder(binding)
            }

            2 -> {
                val binding =
                    ListItemTracksDetailedBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                TracksViewHolder(binding)
            }

            else -> throw IllegalArgumentException(context.getString(R.string.invalid_view_type))
        }

    override fun getItemCount(): Int =
        when (item) {
            is SearchListItem.ArtistsItem -> item.artists.count()
            is SearchListItem.PlaylistsItem -> item.playlists.count()
            is SearchListItem.TracksItem -> item.tracks.count()
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (item) {
            is SearchListItem.ArtistsItem -> (holder as ArtistsViewHolder).bind(item.artists[position])
            is SearchListItem.PlaylistsItem -> (holder as PlaylistsViewHolder).bind(item.playlists[position])
            is SearchListItem.TracksItem -> (holder as TracksViewHolder).bind(item.tracks[position])
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (item) {
            is SearchListItem.ArtistsItem -> 0
            is SearchListItem.PlaylistsItem -> 1
            is SearchListItem.TracksItem -> 2
        }
}
