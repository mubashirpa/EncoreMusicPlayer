package com.encore.music.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemPlaylistsBinding
import com.encore.music.domain.model.playlists.Playlist

class PlaylistsAdapter(
    private val onPlaylistClicked: ((Playlist) -> Unit)? = null,
) : ListAdapter<Playlist, PlaylistsAdapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(
        private val binding: ListItemPlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Playlist) {
            binding.apply {
                media.load(item.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                title.apply {
                    maxLines = 2
                    text = item.name
                }
                subtitle.visibility = GONE

                root.setOnClickListener {
                    onPlaylistClicked?.invoke(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemPlaylistsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Playlist>() {
                override fun areItemsTheSame(
                    oldItem: Playlist,
                    newItem: Playlist,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Playlist,
                    newItem: Playlist,
                ): Boolean = oldItem == newItem
            }
    }
}
