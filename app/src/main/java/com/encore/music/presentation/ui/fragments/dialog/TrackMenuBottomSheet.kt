package com.encore.music.presentation.ui.fragments.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import coil.load
import com.encore.music.R
import com.encore.music.databinding.LayoutDialogTrackMenuBinding
import com.encore.music.databinding.ListItemMenuBinding
import com.encore.music.domain.model.tracks.Track
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuBottomSheet() : BottomSheetDialogFragment() {
    constructor(track: Track, items: List<MenuItem>) : this() {
        this.track = track
        this.items = items
    }

    private var _binding: LayoutDialogTrackMenuBinding? = null
    private val binding get() = _binding!!

    private var track: Track? = null
    private var items: List<MenuItem> = emptyList()
    var setOnMenuItemClickListener: ((position: Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LayoutDialogTrackMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val menuListAdapter = MenuListAdapter(requireContext(), R.layout.list_item_menu, items)

        binding.apply {
            track?.let {
                leadingImage.load(it.image) {
                    crossfade(true)
                    placeholder(R.drawable.bg_placeholder)
                }
                headlineText.text = it.name
                supportingText.text = it.artists?.joinToString { artist -> artist.name.orEmpty() }
            }

            listView.adapter = menuListAdapter
            listView.setOnItemClickListener { _, _, position, _ ->
                setOnMenuItemClickListener?.let {
                    it(position)
                    dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "TrackMenuBottomSheet"
    }
}

data class MenuItem(
    val title: String,
    @DrawableRes val icon: Int,
)

class MenuListAdapter(
    private val context: Context,
    @LayoutRes private val resource: Int,
    private val items: List<MenuItem>,
) : ArrayAdapter<MenuItem>(context, resource, items) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        val binding: ListItemMenuBinding =
            if (convertView == null) {
                ListItemMenuBinding.inflate(LayoutInflater.from(context), parent, false)
            } else {
                ListItemMenuBinding.bind(convertView)
            }
        val item = items[position]

        binding.run {
            leadingIcon.setImageResource(item.icon)
            headlineText.text = item.title
        }

        return binding.root
    }
}
