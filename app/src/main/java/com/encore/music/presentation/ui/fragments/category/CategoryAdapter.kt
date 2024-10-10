package com.encore.music.presentation.ui.fragments.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemCategoryPlaylistsBinding
import com.encore.music.domain.model.playlists.Playlist

class CategoryAdapter(
    private val onPlaylistClicked: (Playlist) -> Unit,
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private val differ: AsyncListDiffer<Playlist> = AsyncListDiffer(this, DIFF_CALLBACK)
    var items: List<Playlist>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    class ViewHolder(
        val binding: ListItemCategoryPlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root)

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

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.binding.run {
            media.load(items[position].image) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
            title.text = items[position].name
            subtitle.visibility = View.GONE

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
