package com.encore.music.presentation.ui.fragments.artist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.LayoutArtistHeaderBinding
import com.encore.music.databinding.LayoutErrorBinding
import com.encore.music.databinding.ListItemTracksDetailedBinding
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.tracks.Track

class ArtistAdapter(
    private val context: Context,
    private val onFollowArtistClicked: ((artist: Artist, isFollowed: Boolean) -> Unit)? = null,
    private val onPlayClicked: (() -> Unit)? = null,
    private val onTrackClicked: ((Track) -> Unit)? = null,
    private val onTrackMoreClicked: ((Track) -> Unit)? = null,
) : ListAdapter<ArtistListItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    inner class HeaderViewHolder(
        private val binding: LayoutArtistHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArtistListItem.HeaderItem) {
            binding.apply {
                media.load(item.artist.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                title.text = item.artist.name
                val followers = item.artist.followers ?: 0
                subtitle.text =
                    context.resources.getQuantityString(R.plurals.followers, followers, followers)
                followButton.isSelected = item.isFollowed

                followButton.setOnClickListener {
                    onFollowArtistClicked?.invoke(item.artist, item.isFollowed)
                }

                playButton.setOnClickListener {
                    onPlayClicked?.invoke()
                }
            }
        }
    }

    inner class TracksViewHolder(
        private val binding: ListItemTracksDetailedBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArtistListItem.TracksItem) {
            binding.apply {
                leadingImage.load(item.track.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                headlineText.text = item.track.name
                supportingText.text = item.track.artists?.joinToString { it.name.orEmpty() }

                root.setOnClickListener {
                    onTrackClicked?.invoke(item.track)
                }

                menuButton.setOnClickListener {
                    onTrackMoreClicked?.invoke(item.track)
                }
            }
        }
    }

    inner class EmptyTracksViewHolder(
        private val binding: LayoutErrorBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                binding.root.layoutParams =
                    ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    )
                errorText.text =
                    context.getString(R.string.no_recent_activity)
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

            2 -> {
                val binding =
                    LayoutErrorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                EmptyTracksViewHolder(binding)
            }

            else -> throw IllegalArgumentException(context.getString(R.string.invalid_view_type))
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        getItem(position)?.let { item ->
            when (item) {
                is ArtistListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
                is ArtistListItem.TracksItem -> (holder as TracksViewHolder).bind(item)
                ArtistListItem.EmptyTracksItem -> (holder as EmptyTracksViewHolder).bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is ArtistListItem.HeaderItem -> 0
            is ArtistListItem.TracksItem -> 1
            ArtistListItem.EmptyTracksItem -> 2
        }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ArtistListItem>() {
                override fun areItemsTheSame(
                    oldItem: ArtistListItem,
                    newItem: ArtistListItem,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ArtistListItem,
                    newItem: ArtistListItem,
                ): Boolean = oldItem == newItem
            }
    }
}
