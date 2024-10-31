package com.encore.music.presentation.ui.fragments.player

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.encore.music.R
import com.encore.music.core.ext.dpToPx
import com.encore.music.databinding.FragmentPlayerBinding
import com.encore.music.player.RepeatMode
import com.encore.music.presentation.ui.activities.MainUiEvent
import com.encore.music.presentation.ui.activities.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

private const val SWIPE_THRESHOLD = 50f
private const val VOLUME_INCREMENT = 0.1f

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModel()
    private var minSwipeY: Float = 0f
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val carouselAdapter = initCarousel()

        val gestureDetector =
            GestureDetector(
                requireContext(),
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDown(e: MotionEvent): Boolean {
                        minSwipeY = 0f
                        return true
                    }

                    override fun onScroll(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        distanceX: Float,
                        distanceY: Float,
                    ): Boolean {
                        minSwipeY += distanceY
                        if (abs(distanceX) < abs(distanceY) && abs(minSwipeY) > SWIPE_THRESHOLD) {
                            val currentVolume = mainViewModel.playerUiState.value.volume
                            val newVolume =
                                when {
                                    distanceY > 0 -> min(currentVolume + VOLUME_INCREMENT, 1.0f)
                                    else -> max(currentVolume - VOLUME_INCREMENT, 0.0f)
                                }
                            mainViewModel.onEvent(MainUiEvent.SetVolume(newVolume))
                            val progress = round(newVolume * 10).toInt()
                            binding.volumeIndicator.progress = progress
                            binding.volumeText.text = "$progress"
                            binding.volumeController.resetAutoHide()
                            minSwipeY = 0f
                        }
                        return true
                    }
                },
            )
        binding.pager.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        mainViewModel.trackList.observe(viewLifecycleOwner) { trackList ->
            carouselAdapter.submitList(trackList)
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.playerUiState.collect { uiState ->
                    if (binding.pager.currentItem != uiState.currentTrackIndex) {
                        binding.pager.post {
                            binding.pager.setCurrentItem(uiState.currentTrackIndex, true)
                        }
                    }

                    uiState.currentPlayingTrack?.let { track ->
                        binding.apply {
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

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.equalizer -> {
                    openEqualizer()
                    true
                }

                else -> false
            }
        }

        binding.shuffleButton.setOnClickListener {
            val enable = !mainViewModel.playerUiState.value.shuffleModeEnabled
            mainViewModel.onEvent(MainUiEvent.ChangeShuffleModeEnabled(enable))
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
            "00:00"
        } else {
            val totalSeconds = (position / 1000).toInt()
            val minutes = totalSeconds / 60
            val remainingSeconds = totalSeconds % 60
            "%d:%02d".format(minutes, remainingSeconds)
        }

    private fun initCarousel(): TrackCarouselAdapter {
        val trackCarouselAdapter = TrackCarouselAdapter()
        binding.pager.apply {
            offscreenPageLimit = 3
            clipToPadding = false
            clipChildren = false
            isUserInputEnabled = false

            val transformer = CompositePageTransformer()
            transformer.addTransformer(MarginPageTransformer(16.dpToPx(requireContext())))
            transformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.14f
            }
            setPageTransformer(transformer)

            adapter = trackCarouselAdapter
        }
        return trackCarouselAdapter
    }

    private fun openEqualizer() {
        val intent =
            Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    mainViewModel.playerUiState.value.audioSessionId,
                )
                putExtra(AudioEffect.EXTRA_PACKAGE_NAME, requireContext().packageName)
                putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
            }

        try {
            activityResultLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Snackbar
                .make(
                    binding.root,
                    getString(R.string.unable_to_open_equalizer),
                    Snackbar.LENGTH_SHORT,
                ).show()
        }
    }
}
