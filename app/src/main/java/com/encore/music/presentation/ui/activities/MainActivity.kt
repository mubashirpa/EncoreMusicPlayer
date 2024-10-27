package com.encore.music.presentation.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private var isTrackNotNull = false
    private var isPlayerVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        DynamicColors.applyToActivityIfAvailable(this)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (!isGranted) {
                    showMessage(getString(R.string.notification_permission_denied))
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is granted
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.dialog_title_notification_permission)
                        .setMessage(R.string.dialog_message_permission_notification)
                        .setNegativeButton(resources.getString(R.string.decline)) { _, _ ->
                            showMessage(getString(R.string.notification_permission_denied))
                        }.setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }.show()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

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
                launch {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is MainEvent.ShowMessage -> {
                                showMessage(event.message.asString(this@MainActivity))
                            }

                            MainEvent.StartService -> {
                                startPlaybackService()
                            }
                        }
                    }
                }

                launch {
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
                        (binding.playerControls.playButton as MaterialButton).setIconResource(
                            playIcon,
                        )
                    }
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.playerControls.root) { _, windowInsets ->
            val insets =
                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.playerControls.rootLayout.setPadding(
                0,
                0,
                0,
                if (binding.bottomNavigation.isVisible) 0 else insets.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    @OptIn(UnstableApi::class)
    private fun startPlaybackService() {
        startForegroundService(Intent(this, PlaybackService::class.java))
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.navHost, message, Snackbar.LENGTH_LONG).show()
    }
}
