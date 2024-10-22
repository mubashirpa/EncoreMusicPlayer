package com.encore.music.presentation.ui.fragments.searchItems

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemArtistsHorizontalBinding
import com.encore.music.databinding.ListItemCategoryPlaylistsBinding
import com.encore.music.databinding.ListItemTracksDetailedBinding
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.search.SearchItem
import com.encore.music.domain.model.tracks.Track

class SearchItemsAdapter(
    private val context: Context,
    private val onArtistClicked: (Artist) -> Unit = {},
    private val onPlaylistClicked: (Playlist) -> Unit = {},
    private val onTrackClicked: (Track) -> Unit = {},
    private val onTrackMoreClicked: (Track) -> Unit = {},
) : PagingDataAdapter<SearchItem, RecyclerView.ViewHolder>(
        DIFF_CALLBACK,
    ) {
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

                root.setOnClickListener {
                    onArtistClicked(item)
                }
            }
        }
    }

    inner class PlaylistsViewHolder(
        private val binding: ListItemCategoryPlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Playlist) {
            binding.apply {
                media.load(item.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                title.text = item.name
                subtitle.text = item.owner

                root.setOnClickListener {
                    onPlaylistClicked(item)
                }
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

                root.setOnClickListener {
                    onTrackClicked(item)
                }

                menuButton.setOnClickListener {
                    onTrackMoreClicked(item)
                }
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
                    ListItemCategoryPlaylistsBinding.inflate(
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

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        getItem(position)?.let { item ->
            when (item) {
                is SearchItem.ArtistItem -> (holder as ArtistsViewHolder).bind(item.artist)
                is SearchItem.PlaylistItem -> (holder as PlaylistsViewHolder).bind(item.playlist)
                is SearchItem.TrackItem -> (holder as TracksViewHolder).bind(item.track)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is SearchItem.ArtistItem -> 0
            is SearchItem.PlaylistItem -> 1
            is SearchItem.TrackItem -> 2
            null -> -1
        }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<SearchItem>() {
                override fun areItemsTheSame(
                    oldItem: SearchItem,
                    newItem: SearchItem,
                ): Boolean =
                    when {
                        oldItem is SearchItem.TrackItem && newItem is SearchItem.TrackItem -> oldItem.track.id == newItem.track.id
                        oldItem is SearchItem.ArtistItem && newItem is SearchItem.ArtistItem -> oldItem.artist.id == newItem.artist.id
                        oldItem is SearchItem.PlaylistItem && newItem is SearchItem.PlaylistItem ->
                            oldItem.playlist.id ==
                                newItem.playlist.id

                        else -> false
                    }

                override fun areContentsTheSame(
                    oldItem: SearchItem,
                    newItem: SearchItem,
                ): Boolean = oldItem == newItem
            }
    }
}
