package com.encore.music.presentation.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import androidx.navigation.navigation
import com.encore.music.R
import com.encore.music.databinding.ActivityMainBinding
import com.encore.music.presentation.navigation.Graph
import com.encore.music.presentation.navigation.Screen
import com.encore.music.presentation.ui.fragments.artist.ArtistFragment
import com.encore.music.presentation.ui.fragments.home.HomeFragment
import com.encore.music.presentation.ui.fragments.library.LibraryFragment
import com.encore.music.presentation.ui.fragments.onboarding.OnboardingFragment
import com.encore.music.presentation.ui.fragments.player.PlayerFragment
import com.encore.music.presentation.ui.fragments.playlist.PlaylistFragment
import com.encore.music.presentation.ui.fragments.resetPassword.ResetPasswordFragment
import com.encore.music.presentation.ui.fragments.search.SearchFragment
import com.encore.music.presentation.ui.fragments.signIn.SignInFragment
import com.encore.music.presentation.ui.fragments.signUp.SignUpFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isNavigationItemSelectedProgrammatically = false
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostContainer.id) as NavHostFragment
        val navController = navHostFragment.navController

        navController.graph =
            navController.createGraph(
                startDestination = if (viewModel.isLoggedIn) Graph.Main else Graph.Onboarding,
            ) {
                navigation<Graph.Onboarding>(startDestination = Screen.Onboarding) {
                    fragment<OnboardingFragment, Screen.Onboarding> {
                        label = getString(R.string.label_onboarding_screen)
                    }
                    fragment<SignInFragment, Screen.SignIn> {
                        label = getString(R.string.label_sign_in_screen)
                    }
                    fragment<SignUpFragment, Screen.SignUp> {
                        label = getString(R.string.label_sign_up_screen)
                    }
                    fragment<ResetPasswordFragment, Screen.ResetPassword> {
                        label = getString(R.string.label_reset_password_screen)
                    }
                }
                navigation<Graph.Main>(startDestination = Screen.Home) {
                    fragment<HomeFragment, Screen.Home> {
                        label = getString(R.string.label_home_screen)
                    }
                    fragment<SearchFragment, Screen.Search> {
                        label = getString(R.string.label_search_screen)
                    }
                    fragment<LibraryFragment, Screen.Library> {
                        label = getString(R.string.label_library_screen)
                    }
                }
                fragment<PlaylistFragment, Screen.Playlist> {
                    label = getString(R.string.label_playlist_screen)
                }
                fragment<ArtistFragment, Screen.Artist> {
                    label = getString(R.string.label_artist_screen)
                }
                fragment<PlayerFragment, Screen.Player> {
                    label = getString(R.string.label_player_screen)
                }
            }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hierarchy = destination.hierarchy
            binding.bottomNavigation.isVisible =
                when {
                    hierarchy.any { it.hasRoute(Screen.Home::class) } -> {
                        binding.bottomNavigation.apply {
                            if (selectedItemId != R.id.home) {
                                isNavigationItemSelectedProgrammatically = true
                                selectedItemId = R.id.home
                            }
                        }
                        true
                    }

                    hierarchy.any { it.hasRoute(Screen.Search::class) } -> {
                        binding.bottomNavigation.apply {
                            if (selectedItemId != R.id.search) {
                                isNavigationItemSelectedProgrammatically = true
                                selectedItemId = R.id.search
                            }
                        }
                        true
                    }

                    hierarchy.any { it.hasRoute(Screen.Library::class) } -> {
                        binding.bottomNavigation.apply {
                            if (selectedItemId != R.id.library) {
                                isNavigationItemSelectedProgrammatically = true
                                selectedItemId = R.id.library
                            }
                        }
                        true
                    }

                    else -> false
                }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            // Only navigate when the selection is not programmatically set
            if (!isNavigationItemSelectedProgrammatically) {
                when (item.itemId) {
                    R.id.home -> {
                        navController.navigateFromNavigationBar(Screen.Home)
                        true
                    }

                    R.id.search -> {
                        navController.navigateFromNavigationBar(Screen.Search)
                        true
                    }

                    R.id.library -> {
                        navController.navigateFromNavigationBar(Screen.Library)
                        true
                    }

                    else -> false
                }
            } else {
                // Reset the flag to allow normal interaction
                isNavigationItemSelectedProgrammatically = false
                true
            }
        }
    }

    private fun NavController.navigateFromNavigationBar(screen: Screen) {
        this.run {
            navigate(screen) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(Screen.Home) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // re-selecting the same item
                launchSingleTop = true
                // Restore state when re-selecting a previously selected item
                restoreState = true
            }
        }
    }
}
