package com.encore.music.presentation.ui.fragments.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemTopTracksBinding
import com.encore.music.domain.model.playlists.Playlist

class TopTracksAdapter(
    private val items: List<Playlist>,
) : RecyclerView.Adapter<TopTracksAdapter.ViewHolder>() {
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
        }
    }
}
