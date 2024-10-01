package com.encore.music.presentation.ui.fragments.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemTracksDetailedBinding
import com.encore.music.domain.model.spotify.tracks.Track

class TracksAdapter : RecyclerView.Adapter<TracksAdapter.ViewHolder>() {
    private val differ: AsyncListDiffer<Track> = AsyncListDiffer(this, DIFF_CALLBACK)
    var items: List<Track>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    class ViewHolder(
        val binding: ListItemTracksDetailedBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemTracksDetailedBinding.inflate(
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
            leadingImage.load(items[position].imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
            headlineText.text = items[position].name
            supportingText.text = items[position].name // TODO("Replace with artists")

            menuButton.setOnClickListener { /*TODO*/ }
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
