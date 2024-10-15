package com.encore.music.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.media3.common.util.UnstableApi
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ActivityMainBinding
import com.encore.music.player.service.PlaybackService
import com.encore.music.presentation.navigation.Graph
import com.encore.music.presentation.navigation.findNavController
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.setupWithNavController
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private var isServiceRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startPlaybackService()

        val navController =
            findNavController(
                viewId = binding.navHost.id,
                startDestination = if (viewModel.isLoggedIn) Graph.Main else Graph.Onboarding,
            )

        binding.bottomNavigation.setupWithNavController(navController)

        viewModel.progress.observe(this) { progress ->
            binding.playerControls.progressCircular.progress = progress.toInt()
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            val iconRes =
                if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
            (binding.playerControls.playButton as MaterialButton).setIconResource(iconRes)
        }

        viewModel.currentSelectedAudio.observe(this) { track ->
            val isTrackNull = track == null
            binding.playerControls.apply {
                root.isVisible = !isTrackNull
                if (!isTrackNull) {
                    media.load(track.image) {
                        crossfade(true)
                        placeholder(R.drawable.bg_placeholder)
                    }
                    title.text = track.name.orEmpty()
                    subtitle.text =
                        track.artists
                            ?.firstOrNull()
                            ?.name
                            .orEmpty()
                }
            }
        }

        binding.playerControls.root.setOnClickListener {
            navController.navigateToPlayer("")
        }

        binding.playerControls.playButton.setOnClickListener {
            viewModel.onEvent(MainUiEvent.PlayPause)
        }
    }

    @OptIn(UnstableApi::class)
    private fun startPlaybackService() {
        if (!isServiceRunning) {
            val intent = Intent(this, PlaybackService::class.java)
            startForegroundService(intent)
            isServiceRunning = true
        }
    }
}
