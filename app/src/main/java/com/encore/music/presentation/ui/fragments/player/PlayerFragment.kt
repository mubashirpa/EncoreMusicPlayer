package com.encore.music.presentation.ui.fragments.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.encore.music.R
import com.encore.music.databinding.FragmentPlayerBinding
import com.encore.music.presentation.ui.activities.MainUiEvent
import com.encore.music.presentation.ui.activities.MainViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.currentSelectedAudio.observe(viewLifecycleOwner) { track ->
            track?.let {
                binding.apply {
                    media.load(track.image) {
                        crossfade(true)
                        placeholder(R.drawable.bg_placeholder)
                    }

                    title.text = track.name
                    subtitle.text = track.artists?.joinToString { it.name.orEmpty() }
                }
            }
        }

        mainViewModel.progress.observe(viewLifecycleOwner) { progress ->
            binding.progress.value =
                if (progress <= 100.0) {
                    progress
                } else {
                    100.0f
                }
        }

        mainViewModel.progressString.observe(viewLifecycleOwner) { progressString ->
            binding.elapsed.text = progressString
        }

        mainViewModel.duration.observe(viewLifecycleOwner) { duration ->
            binding.duration.text = timeStampToDuration(duration)
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.previousButton.setOnClickListener {
            mainViewModel.onEvent(MainUiEvent.SeekToPrevious)
        }

        binding.playButton.setOnClickListener {
            mainViewModel.onEvent(MainUiEvent.PlayPause)
        }

        binding.nextButton.setOnClickListener {
            mainViewModel.onEvent(MainUiEvent.SeekToNext)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun timeStampToDuration(position: Long): String =
        if (position < 0) {
            "--:--"
        } else {
            val totalSeconds = (position / 1000).toInt()
            val minutes = totalSeconds / 60
            val remainingSeconds = totalSeconds % 60
            "%d:%02d".format(minutes, remainingSeconds)
        }
}
