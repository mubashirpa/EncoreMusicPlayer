package com.encore.music.presentation.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemTopTracksBinding
import com.encore.music.domain.model.spotify.tracks.Track

class TopTracksAdapter(
    private val onTrackClicked: (Track) -> Unit,
) : RecyclerView.Adapter<TopTracksAdapter.ViewHolder>() {
    private val differ: AsyncListDiffer<Track> = AsyncListDiffer(this, DIFF_CALLBACK)
    var items: List<Track>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    class ViewHolder(
        val binding: ListItemTopTracksBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemTopTracksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            root.setOnClickListener {
                onTrackClicked(items[position])
            }
        }
    }

    companion object {
        val DIFF_CALLBACK =
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
