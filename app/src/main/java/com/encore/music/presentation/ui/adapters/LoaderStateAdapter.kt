package com.encore.music.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.encore.music.databinding.LayoutPagingLoaderBinding

class LoaderStateAdapter(
    private val onRetry: (() -> Unit)? = null,
) : LoadStateAdapter<LoaderStateAdapter.LoaderViewHolder>() {
    inner class LoaderViewHolder(
        private val binding: LayoutPagingLoaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.apply {
                when (loadState) {
                    is LoadState.Loading -> motionLayout.transitionToEnd()
                    else -> motionLayout.transitionToStart()
                }

                retryButton.setOnClickListener {
                    onRetry?.invoke()
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: LoaderViewHolder,
        loadState: LoadState,
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): LoaderViewHolder {
        val binding =
            LayoutPagingLoaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoaderViewHolder(binding)
    }
}
