package com.encore.music.presentation.ui.fragments.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemPlaylistsBinding
import com.encore.music.domain.model.spotify.playlists.Playlist
import com.encore.music.presentation.ui.fragments.library.ArtistsAdapter.Companion.DIFF_CALLBACK

class PlaylistsAdapter(
    private val onPlaylistClicked: (Playlist) -> Unit,
) : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {
    private val differ: AsyncListDiffer<Playlist> = AsyncListDiffer(this, DIFF_CALLBACK)
    var items: List<Playlist>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    class ViewHolder(
        val binding: ListItemPlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemPlaylistsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.binding.run {
            media.load(items[position].imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
            title.text = items[position].name
            subtitle.text = items[position].ownerDisplayName

            root.setOnClickListener {
                onPlaylistClicked(items[position])
            }
        }
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