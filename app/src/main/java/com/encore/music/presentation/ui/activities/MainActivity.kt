package com.encore.music.presentation.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.encore.music.core.Navigation
import com.encore.music.databinding.ActivityMainBinding
import com.encore.music.presentation.navigation.Graph
import com.encore.music.presentation.navigation.findNavController
import com.encore.music.presentation.navigation.setupWithNavController
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController =
            findNavController(
                viewId = binding.navHost.id,
                startDestination = if (viewModel.isLoggedIn) Graph.Main else Graph.Onboarding,
            )

        binding.bottomNavigation.setupWithNavController(navController)

        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle
            ?.getLiveData<Boolean>(Navigation.Args.MAIN_BOTTOM_NAVIGATION_VISIBILITY)
            ?.observe(this) { value ->
                value?.also {
                    binding.bottomNavigation.isVisible = it
                }
            }
    }
}
