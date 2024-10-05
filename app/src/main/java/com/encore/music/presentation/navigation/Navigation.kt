package com.encore.music.presentation.navigation

import androidx.annotation.IdRes
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
import com.encore.music.presentation.ui.fragments.artist.ArtistFragment
import com.encore.music.presentation.ui.fragments.home.HomeFragment
import com.encore.music.presentation.ui.fragments.library.LibraryFragment
import com.encore.music.presentation.ui.fragments.onboarding.OnboardingFragment
import com.encore.music.presentation.ui.fragments.player.PlayerFragment
import com.encore.music.presentation.ui.fragments.playlist.PlaylistFragment
import com.encore.music.presentation.ui.fragments.profile.ProfileFragment
import com.encore.music.presentation.ui.fragments.resetPassword.ResetPasswordFragment
import com.encore.music.presentation.ui.fragments.search.SearchFragment
import com.encore.music.presentation.ui.fragments.signIn.SignInFragment
import com.encore.music.presentation.ui.fragments.signUp.SignUpFragment
import com.google.android.material.navigation.NavigationBarView

fun AppCompatActivity.findNavController(
    @IdRes viewId: Int,
    startDestination: Any,
): NavController {
    val navHostFragment =
        supportFragmentManager.findFragmentById(viewId) as NavHostFragment
    val navController = navHostFragment.navController

    navController.graph =
        navController.createGraph(startDestination = startDestination) {
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
            fragment<ProfileFragment, Screen.Profile> {
                label = getString(R.string.label_profile_screen)
            }
        }

    return navController
}

fun NavigationBarView.setupWithNavController(navController: NavController) {
    var isNavigationItemSelectedProgrammatically = false
    val routeToNavItemId =
        mapOf(
            Screen.Home::class to R.id.home,
            Screen.Search::class to R.id.search,
            Screen.Library::class to R.id.library,
        )

    fun selectNavigationItem(itemId: Int) {
        if (selectedItemId != itemId) {
            isNavigationItemSelectedProgrammatically = true
            selectedItemId = itemId
        }
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        val matchingNavItemId =
            routeToNavItemId.entries
                .firstOrNull { entry ->
                    destination.hierarchy.any {
                        it.hasRoute(entry.key)
                    }
                }?.value

        isVisible =
            when (matchingNavItemId) {
                R.id.home -> {
                    selectNavigationItem(R.id.home)
                    true
                }

                R.id.search -> {
                    selectNavigationItem(R.id.search)
                    true
                }

                R.id.library -> {
                    selectNavigationItem(R.id.library)
                    true
                }

                else -> false
            }
    }

    setOnItemSelectedListener { item ->
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
            isNavigationItemSelectedProgrammatically = false
            false
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

fun NavController.navigateToMain() {
    popBackStack(Graph.Onboarding, true)
    navigate(Graph.Main)
}

fun NavController.navigateToOnboarding() {
    popBackStack(Graph.Main, true)
    navigate(Graph.Onboarding)
}

fun NavController.navigateToSignIn() {
    navigate(Screen.SignIn) {
        popUpTo(Screen.SignUp) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavController.navigateToSignUp() {
    navigate(Screen.SignUp) {
        popUpTo(Screen.SignIn) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavController.navigateToResetPassword(email: String) {
    navigate(route = Screen.ResetPassword(email = email))
}

fun NavController.navigateToPlaylist(playlistId: String) {
    navigate(route = Screen.Playlist(id = playlistId))
}

fun NavController.navigateToArtist(artistId: String) {
    navigate(route = Screen.Artist(id = artistId))
}

fun NavController.navigateToPlayer(trackId: String) {
    navigate(route = Screen.Player(id = trackId)) {
        launchSingleTop = true
    }
}

fun NavController.navigateToProfile() {
    navigate(Screen.Profile)
}