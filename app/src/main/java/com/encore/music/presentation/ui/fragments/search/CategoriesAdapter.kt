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
    private val onItemClick: ((Category) -> Unit)? = null,
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {
    inner class ViewHolder(
        val binding: ListItemCategoriesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            binding.run {
                media.load(item.icon) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                title.text = item.name

                root.setOnClickListener {
                    onItemClick?.invoke(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }
}
