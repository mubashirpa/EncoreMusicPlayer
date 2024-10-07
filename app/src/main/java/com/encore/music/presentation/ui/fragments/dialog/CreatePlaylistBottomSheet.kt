package com.encore.music.presentation.ui.fragments.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.encore.music.databinding.LayoutCreatePlaylistBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CreatePlaylistBottomSheet(
    private val onCreatePlaylist: (name: String, description: String) -> Unit,
) : BottomSheetDialogFragment() {
    constructor() : this({ _, _ -> })

    private var _binding: LayoutCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LayoutCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.saveButton.setOnClickListener {
            onCreatePlaylist(
                binding.nameField.editText
                    ?.text
                    .toString(),
                binding.descriptionField.editText
                    ?.text
                    .toString(),
            )
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
}
