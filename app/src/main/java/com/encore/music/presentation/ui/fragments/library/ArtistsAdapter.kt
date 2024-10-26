package com.encore.music.presentation.ui.fragments.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemArtistsBinding
import com.encore.music.domain.model.artists.Artist

class ArtistsAdapter(
    private val onArtistClicked: ((Artist) -> Unit)? = null,
) : ListAdapter<Artist, ArtistsAdapter.ViewHolder>(DIFF_CALLBACK) {
    inner class ViewHolder(
        val binding: ListItemArtistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artist) {
            binding.run {
                media.load(item.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                title.text = item.name

                root.setOnClickListener {
                    onArtistClicked?.invoke(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemArtistsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Artist>() {
                override fun areItemsTheSame(
                    oldItem: Artist,
                    newItem: Artist,
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: Artist,
                    newItem: Artist,
                ): Boolean = oldItem == newItem
            }
    }
}
