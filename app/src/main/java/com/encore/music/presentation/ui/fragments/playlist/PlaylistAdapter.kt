package com.encore.music.presentation.ui.fragments.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.LayoutPlaylistHeaderBinding
import com.encore.music.databinding.ListItemTracksDetailedBinding

class PlaylistAdapter(
    private val context: Context,
    var items: MutableList<PlaylistListItem>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class HeaderViewHolder(
        private val binding: LayoutPlaylistHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaylistListItem.HeaderItem) {
            binding.media.load(item.playlist.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
            binding.title.text = item.playlist.name
            binding.subtitle.text = item.playlist.description
        }
    }

    class TracksViewHolder(
        private val binding: ListItemTracksDetailedBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaylistListItem.TracksItem) {
            binding.leadingImage.load(item.track.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
            binding.headlineText.text = item.track.name
            binding.supportingText.text = item.track.name // TODO("Replace with artists")

            binding.menuButton.setOnClickListener { /*TODO*/ }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> {
                val binding =
                    LayoutPlaylistHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                HeaderViewHolder(binding)
            }

            1 -> {
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
            is PlaylistListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is PlaylistListItem.TracksItem -> (holder as TracksViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is PlaylistListItem.HeaderItem -> 0
            is PlaylistListItem.TracksItem -> 1
        }
}
