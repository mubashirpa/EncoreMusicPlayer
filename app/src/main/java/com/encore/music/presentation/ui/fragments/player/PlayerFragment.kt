package com.encore.music.presentation.ui.fragments.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.encore.music.R
import com.encore.music.databinding.FragmentPlayerBinding
import com.encore.music.player.RepeatMode
import com.encore.music.presentation.ui.activities.MainUiEvent
import com.encore.music.presentation.ui.activities.MainViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.playerUiState.collect { uiState ->
                    uiState.currentPlayingTrack?.let { track ->
                        binding.apply {
                            media.load(track.image) {
                                crossfade(true)
                                placeholder(R.drawable.bg_placeholder)
                            }

                            title.text = track.name
                            title.setSelected(true)
                            subtitle.text = track.artists?.joinToString { it.name.orEmpty() }
                        }
                    }

                    val shuffleIcon =
                        if (uiState.shuffleModeEnabled) R.drawable.baseline_shuffle_on_24 else R.drawable.baseline_shuffle_24
                    val playIcon =
                        if (uiState.isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                    val repeatIcon =
                        when (uiState.repeatMode) {
                            RepeatMode.REPEAT_MODE_OFF -> R.drawable.baseline_repeat_24
                            RepeatMode.REPEAT_MODE_ONE -> R.drawable.baseline_repeat_one_on_24
                            RepeatMode.REPEAT_MODE_ALL -> R.drawable.baseline_repeat_on_24
                        }

                    (binding.shuffleButton as MaterialButton).setIconResource(shuffleIcon)
                    binding.playButton.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            playIcon,
                        ),
                    )
                    (binding.repeatButton as MaterialButton).setIconResource(repeatIcon)
                }
            }
        }

        binding.progress.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                mainViewModel.onEvent(MainUiEvent.SeekTo(value))
            }
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.shuffleButton.setOnClickListener {
            mainViewModel.onEvent(MainUiEvent.ChangeShuffleModeEnabled(true))
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

        binding.repeatButton.setOnClickListener {
            mainViewModel.onEvent(MainUiEvent.ChangeRepeatMode)
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
