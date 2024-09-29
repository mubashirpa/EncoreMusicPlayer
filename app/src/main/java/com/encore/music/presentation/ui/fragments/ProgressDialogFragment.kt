package com.encore.music.presentation.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.encore.music.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProgressDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext(), R.style.ProgressDialog)
            .setView(R.layout.layout_progress_dialog)
            .create()

    companion object {
        const val TAG = "ProgressDialog"
    }
}
