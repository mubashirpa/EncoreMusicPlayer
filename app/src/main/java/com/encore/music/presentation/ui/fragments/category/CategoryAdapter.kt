package com.encore.music.presentation.ui.fragments.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemCategoryPlaylistsBinding
import com.encore.music.domain.model.playlists.Playlist

class CategoryAdapter(
    private val onPlaylistClicked: (Playlist) -> Unit,
) : PagingDataAdapter<Playlist, CategoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(
        val binding: ListItemCategoryPlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Playlist) {
            binding.run {
                media.load(item.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                title.text = item.name
                subtitle.visibility = View.GONE

                root.setOnClickListener {
                    onPlaylistClicked(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemCategoryPlaylistsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun getItemViewType(position: Int): Int = 1

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
