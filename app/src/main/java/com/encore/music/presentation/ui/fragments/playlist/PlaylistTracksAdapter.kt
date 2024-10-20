package com.encore.music.presentation.ui.fragments.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemTracksDetailedBinding
import com.encore.music.domain.model.tracks.Track

class PlaylistTracksAdapter(
    private val onTrackClicked: (Track) -> Unit,
    private val onTrackMoreClicked: (Track) -> Unit,
) : PagingDataAdapter<Track, PlaylistTracksAdapter.TracksViewHolder>(
        DIFF_CALLBACK,
    ) {
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
    ): PlaylistTracksAdapter.TracksViewHolder {
        val binding =
            ListItemTracksDetailedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return TracksViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlaylistTracksAdapter.TracksViewHolder,
        position: Int,
    ) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Track>() {
                override fun areItemsTheSame(
                    oldItem: Track,
                    newItem: Track,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Track,
                    newItem: Track,
                ): Boolean = oldItem == newItem
            }
    }
}
