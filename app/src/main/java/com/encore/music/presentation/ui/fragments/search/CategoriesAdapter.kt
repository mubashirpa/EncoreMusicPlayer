package com.encore.music.presentation.ui.fragments.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ListItemCategoriesBinding
import com.encore.music.domain.model.categories.Category

class CategoriesAdapter(
    private val items: List<Category>,
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {
    class ViewHolder(
        val binding: ListItemCategoriesBinding,
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
