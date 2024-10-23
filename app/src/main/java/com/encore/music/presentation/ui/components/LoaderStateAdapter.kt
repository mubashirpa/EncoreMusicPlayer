package com.encore.music.presentation.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.encore.music.databinding.LayoutPagingLoaderBinding

class LoaderStateAdapter(
    private val onRetry: () -> Unit,
) : LoadStateAdapter<LoaderStateAdapter.LoaderViewHolder>() {
    inner class LoaderViewHolder(
        private val binding: LayoutPagingLoaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.apply {
                if (loadState is LoadState.Loading) {
                    motionLayout.transitionToEnd()
                } else {
                    motionLayout.transitionToStart()
                }

                retryButton.setOnClickListener {
                    onRetry()
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
