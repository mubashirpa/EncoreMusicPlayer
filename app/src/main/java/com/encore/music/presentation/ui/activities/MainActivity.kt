package com.encore.music.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import coil.load
import com.encore.music.R
import com.encore.music.databinding.ActivityMainBinding
import com.encore.music.player.service.PlaybackService
import com.encore.music.presentation.navigation.Graph
import com.encore.music.presentation.navigation.Screen
import com.encore.music.presentation.navigation.findNavController
import com.encore.music.presentation.navigation.navigateToPlayer
import com.encore.music.presentation.navigation.setupWithNavController
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private var isServiceRunning = false
    private var isTrackNotNull = false
    private var isPlayerVisible = false

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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerUiState.collect { playerUiState ->
                    val track = playerUiState.currentPlayingTrack
                    isTrackNotNull = track != null
                    binding.playerControls.apply {
                        root.isVisible = isTrackNotNull && !isPlayerVisible
                        if (isTrackNotNull) {
                            media.load(track!!.image) {
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

                    val playIcon =
                        if (playerUiState.isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24
                    (binding.playerControls.playButton as MaterialButton).setIconResource(playIcon)
                }
            }
        }

        binding.playerControls.root.setOnClickListener {
            navController.navigateToPlayer()
        }

        binding.playerControls.playButton.setOnClickListener {
            viewModel.onEvent(MainUiEvent.PlayPause)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            isPlayerVisible = destination.hierarchy.any { it.hasRoute(Screen.Player::class) }
            binding.playerControls.root.isVisible = !isPlayerVisible && isTrackNotNull
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
