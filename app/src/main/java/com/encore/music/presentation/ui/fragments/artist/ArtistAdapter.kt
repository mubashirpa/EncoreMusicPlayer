package com.encore.music.presentation.ui.fragments.artist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.LayoutArtistHeaderBinding
import com.encore.music.databinding.ListItemTracksDetailedBinding

class ArtistAdapter(
    private val context: Context,
    var items: MutableList<ArtistListItem>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class HeaderViewHolder(
        private val binding: LayoutArtistHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArtistListItem.HeaderItem) {
            binding.media.load(item.artist.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
            binding.title.text = item.artist.name
            binding.subtitle.text = item.artist.name // TODO
        }
    }

    class TracksViewHolder(
        private val binding: ListItemTracksDetailedBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArtistListItem.TracksItem) {
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
                    LayoutArtistHeaderBinding.inflate(
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
            is ArtistListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is ArtistListItem.TracksItem -> (holder as TracksViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is ArtistListItem.HeaderItem -> 0
            is ArtistListItem.TracksItem -> 1
        }
}
