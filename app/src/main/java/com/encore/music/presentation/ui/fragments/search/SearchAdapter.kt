package com.encore.music.presentation.ui.fragments.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemArtistsHorizontalBinding
import com.encore.music.databinding.ListItemCategoryPlaylistsBinding
import com.encore.music.databinding.ListItemTracksDetailedBinding
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

class SearchAdapter(
    private val context: Context,
    private val onArtistClicked: ((Artist) -> Unit)? = null,
    private val onPlaylistClicked: ((Playlist) -> Unit)? = null,
    private val onTrackClicked: ((Track) -> Unit)? = null,
    private val onTrackMoreClicked: ((Track) -> Unit)? = null,
) : ListAdapter<SearchListItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
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
                    onArtistClicked?.invoke(item)
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
                    onPlaylistClicked?.invoke(item)
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
                    onTrackClicked?.invoke(item)
                }

                menuButton.setOnClickListener {
                    onTrackMoreClicked?.invoke(item)
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
        getItem(position).let { item ->
            when (item) {
                is SearchListItem.ArtistItem -> (holder as ArtistsViewHolder).bind(item.artist)
                is SearchListItem.PlaylistItem -> (holder as PlaylistsViewHolder).bind(item.playlist)
                is SearchListItem.TrackItem -> (holder as TracksViewHolder).bind(item.track)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is SearchListItem.ArtistItem -> 0
            is SearchListItem.PlaylistItem -> 1
            is SearchListItem.TrackItem -> 2
            null -> -1
        }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<SearchListItem>() {
                override fun areItemsTheSame(
                    oldItem: SearchListItem,
                    newItem: SearchListItem,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: SearchListItem,
                    newItem: SearchListItem,
                ): Boolean = oldItem.id == newItem.id
            }
    }
}
