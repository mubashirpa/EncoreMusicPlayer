package com.encore.music.presentation.ui.fragments.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemArtistsBinding
import com.encore.music.domain.model.artists.Artist

class ArtistsAdapter(
    private val onArtistClicked: (Artist) -> Unit,
) : RecyclerView.Adapter<ArtistsAdapter.ViewHolder>() {
    private val differ: AsyncListDiffer<Artist> = AsyncListDiffer(this, DIFF_CALLBACK)
    var items: List<Artist>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    class ViewHolder(
        val binding: ListItemArtistsBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemArtistsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            root.setOnClickListener {
                onArtistClicked(items[position])
            }
        }
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
