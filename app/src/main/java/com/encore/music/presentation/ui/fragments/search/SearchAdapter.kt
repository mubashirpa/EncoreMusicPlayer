package com.encore.music.presentation.ui.fragments.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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
    private val onArtistClick: (Artist) -> Unit = {},
    private val onPlaylistClick: (Playlist) -> Unit = {},
    private val onTrackClick: (Track) -> Unit = {},
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List<SearchListItem> = emptyList()

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
                    onArtistClick(item)
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
                    onPlaylistClick(item)
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
                    onTrackClick(item)
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

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = items[position]) {
            is SearchListItem.ArtistItem -> (holder as ArtistsViewHolder).bind(item.artist)
            is SearchListItem.PlaylistItem -> (holder as PlaylistsViewHolder).bind(item.playlist)
            is SearchListItem.TrackItem -> (holder as TracksViewHolder).bind(item.track)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is SearchListItem.ArtistItem -> 0
            is SearchListItem.PlaylistItem -> 1
            is SearchListItem.TrackItem -> 2
        }
}
