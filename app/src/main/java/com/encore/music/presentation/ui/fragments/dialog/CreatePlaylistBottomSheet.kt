package com.encore.music.presentation.ui.fragments.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.encore.music.databinding.LayoutDialogCreatePlaylistBinding
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CreatePlaylistBottomSheet : BottomSheetDialogFragment() {
    private var _binding: LayoutDialogCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private var playlist: Playlist? = null
    private var tracks: List<Track>? = null
    private var onCreatePlaylistClickListener: ((dialog: CreatePlaylistBottomSheet, playlist: Playlist) -> Unit)? =
        null
    private var onUpdatePlaylistClickListener: ((dialog: CreatePlaylistBottomSheet, playlist: Playlist) -> Unit)? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LayoutDialogCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        playlist?.let {
            val name = it.name.orEmpty()
            binding.nameField.editText?.setText(name)
            binding.descriptionField.editText?.setText(it.description.orEmpty())
            binding.saveButton.isEnabled = name.isNotBlank()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            val name =
                binding.nameField.editText
                    ?.text
                    .toString()
            val description =
                binding.descriptionField.editText
                    ?.text
                    .toString()

            if (playlist != null) {
                val updatedPlaylist =
                    playlist!!.copy(description = description, name = name, tracks = tracks)
                onUpdatePlaylistClickListener?.invoke(this, updatedPlaylist)
            } else {
                val newPlaylist =
                    Playlist(
                        description = description,
                        name = name,
                        tracks = tracks,
                        isLocal = true,
                    )
                onCreatePlaylistClickListener?.invoke(this, newPlaylist)
            }
        }

        binding.nameField.editText?.doOnTextChanged { text, _, _, _ ->
            binding.saveButton.isEnabled = text?.isNotBlank() == true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "CreatePlaylistBottomSheet"
    }

    fun setPlaylist(playlist: Playlist): CreatePlaylistBottomSheet {
        this.playlist = playlist
        return this
    }

    fun setTracks(tracks: List<Track>): CreatePlaylistBottomSheet {
        this.tracks = tracks
        return this
    }

    fun setOnCreatePlaylistClickListener(
        listener: (dialog: CreatePlaylistBottomSheet, playlist: Playlist) -> Unit,
    ): CreatePlaylistBottomSheet {
        onCreatePlaylistClickListener = listener
        return this
    }

    fun setOnUpdatePlaylistClickListener(
        listener: (dialog: CreatePlaylistBottomSheet, playlist: Playlist) -> Unit,
    ): CreatePlaylistBottomSheet {
        onUpdatePlaylistClickListener = listener
        return this
    }
}
