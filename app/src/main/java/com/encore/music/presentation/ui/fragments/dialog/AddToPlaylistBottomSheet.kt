package com.encore.music.presentation.ui.fragments.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import coil.load
import com.encore.music.R
import com.encore.music.databinding.LayoutDialogAddToPlaylistBinding
import com.encore.music.databinding.ListItemAddToPlaylistsBinding
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddToPlaylistBottomSheet() : BottomSheetDialogFragment() {
    constructor(track: Track, playlists: List<Playlist>) : this() {
        this.track = track
        this.playlists = playlists
    }

    private var _binding: LayoutDialogAddToPlaylistBinding? = null
    private val binding get() = _binding!!

    private var track: Track? = null
    private var playlists: List<Playlist> = emptyList()
    private var onCreatePlaylistClickListener: ((dialog: AddToPlaylistBottomSheet, track: Track) -> Unit)? =
        null
    private var onAddToPlaylistClickListener: ((dialog: AddToPlaylistBottomSheet, playlist: Playlist) -> Unit)? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LayoutDialogAddToPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val addToPlaylistAdapter =
            AddToPlaylistAdapter(requireContext(), R.layout.list_item_add_to_playlists, playlists)

        binding.apply {
            listView.adapter = addToPlaylistAdapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val playlist = playlists[position]
                track?.let {
                    onAddToPlaylistClickListener?.invoke(
                        this@AddToPlaylistBottomSheet,
                        playlist.copy(tracks = listOf(it)),
                    )
                }
            }

            createPlaylist.setOnClickListener {
                track?.let {
                    onCreatePlaylistClickListener?.invoke(this@AddToPlaylistBottomSheet, it)
                }
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "AddToPlaylistBottomSheet"
    }

    fun setOnCreatePlaylistClickListener(listener: (dialog: AddToPlaylistBottomSheet, track: Track) -> Unit): AddToPlaylistBottomSheet {
        onCreatePlaylistClickListener = listener
        return this
    }

    fun setOnAddToPlaylistClickListener(
        listener: (dialog: AddToPlaylistBottomSheet, playlist: Playlist) -> Unit,
    ): AddToPlaylistBottomSheet {
        onAddToPlaylistClickListener = listener
        return this
    }
}

private class AddToPlaylistAdapter(
    private val context: Context,
    @LayoutRes private val resource: Int,
    private val items: List<Playlist>,
) : ArrayAdapter<Playlist>(context, resource, items) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val binding: ListItemAddToPlaylistsBinding =
            if (convertView == null) {
                ListItemAddToPlaylistsBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false,
                )
            } else {
                ListItemAddToPlaylistsBinding.bind(convertView)
            }
        val item = items[position]

        binding.run {
            leadingImage.load(item.image) {
                crossfade(true)
                placeholder(R.drawable.bg_placeholder)
            }
            headlineText.text = item.name
        }

        return binding.root
    }
}
