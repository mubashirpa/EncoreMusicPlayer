package com.encore.music.presentation.ui.fragments.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.LayoutErrorBinding
import com.encore.music.databinding.LayoutPlaylistHeaderBinding
import com.encore.music.databinding.ListItemTracksDetailedBinding
import com.encore.music.domain.model.tracks.Track

class PlaylistAdapter(
    private val context: Context,
    private val onShuffleClicked: (() -> Unit)? = null,
    private val onPlayClicked: (() -> Unit)? = null,
    private val onTrackClicked: ((Track) -> Unit)? = null,
    private val onTrackMoreClicked: ((Track) -> Unit)? = null,
) : ListAdapter<PlaylistListItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    inner class HeaderViewHolder(
        private val binding: LayoutPlaylistHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlaylistListItem.HeaderItem) {
            binding.apply {
                media.load(item.playlist.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                title.text = item.playlist.name
                val description = item.playlist.description.orEmpty()
                subtitle.text = description
                subtitle.isVisible = description.isNotBlank()

                shuffleButton.setOnClickListener {
                    onShuffleClicked?.invoke()
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
        fun bind(item: PlaylistListItem.TracksItem) {
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
                    context.getString(R.string.start_building_your_playlist_by_adding_some_songs)
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
                is PlaylistListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
                is PlaylistListItem.TracksItem -> (holder as TracksViewHolder).bind(item)
                PlaylistListItem.EmptyTracksItem -> (holder as EmptyTracksViewHolder).bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is PlaylistListItem.HeaderItem -> 0
            is PlaylistListItem.TracksItem -> 1
            is PlaylistListItem.EmptyTracksItem -> 2
        }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<PlaylistListItem>() {
                override fun areItemsTheSame(
                    oldItem: PlaylistListItem,
                    newItem: PlaylistListItem,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: PlaylistListItem,
                    newItem: PlaylistListItem,
                ): Boolean = oldItem == newItem
            }
    }
}
