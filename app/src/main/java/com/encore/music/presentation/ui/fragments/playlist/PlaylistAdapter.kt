package com.encore.music.presentation.ui.fragments.playlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.LayoutErrorBinding
import com.encore.music.databinding.LayoutPlaylistHeaderBinding
import com.encore.music.databinding.ListItemTracksDetailedBinding
import com.encore.music.domain.model.tracks.Track

class PlaylistAdapter(
    private val context: Context,
    var items: MutableList<PlaylistListItem>,
    private val onTrackClicked: (Track) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                val description = item.playlist.description
                if (!description.isNullOrEmpty()) {
                    subtitle.text = description
                } else {
                    subtitle.visibility = View.GONE
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
                    onTrackClicked(item.track)
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

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = items[position]) {
            is PlaylistListItem.HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is PlaylistListItem.TracksItem -> (holder as TracksViewHolder).bind(item)
            PlaylistListItem.EmptyTracksItem -> (holder as EmptyTracksViewHolder).bind()
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is PlaylistListItem.HeaderItem -> 0
            is PlaylistListItem.TracksItem -> 1
            is PlaylistListItem.EmptyTracksItem -> 2
        }
}
