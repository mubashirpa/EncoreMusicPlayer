package com.encore.music.presentation.navigation

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavOptions
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import androidx.navigation.navigation
import com.encore.music.R
import com.encore.music.domain.model.search.SearchType
import com.encore.music.presentation.ui.fragments.artist.ArtistFragment
import com.encore.music.presentation.ui.fragments.category.CategoryFragment
import com.encore.music.presentation.ui.fragments.home.HomeFragment
import com.encore.music.presentation.ui.fragments.library.LibraryFragment
import com.encore.music.presentation.ui.fragments.onboarding.OnboardingFragment
import com.encore.music.presentation.ui.fragments.player.PlayerFragment
import com.encore.music.presentation.ui.fragments.playlist.PlaylistFragment
import com.encore.music.presentation.ui.fragments.profile.ProfileFragment
import com.encore.music.presentation.ui.fragments.resetPassword.ResetPasswordFragment
import com.encore.music.presentation.ui.fragments.search.SearchFragment
import com.encore.music.presentation.ui.fragments.searchItems.SearchItemsFragment
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
            fragment<CategoryFragment, Screen.Category> {
                label = getString(R.string.label_category_screen)
            }
            fragment<SearchItemsFragment, Screen.SearchItems> {
                label = getString(R.string.label_search_items_screen)
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

    setOnItemSelectedListener { item ->
        if (isNavigationItemSelectedProgrammatically) {
            isNavigationItemSelectedProgrammatically = false
            true
        } else {
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

        isVisible = matchingNavItemId?.let {
            selectNavigationItem(matchingNavItemId)
            true
        } ?: false
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

private val defaultNavOptions =
    NavOptions
        .Builder()
        .setEnterAnim(android.R.anim.slide_in_left)
        .setExitAnim(android.R.anim.slide_out_right)
        .setPopEnterAnim(android.R.anim.slide_in_left)
        .setPopExitAnim(android.R.anim.slide_out_right)

private val graphNavOptions =
    NavOptions
        .Builder()
        .setEnterAnim(R.anim.pop_in)
        .setExitAnim(R.anim.pop_out)
        .setPopEnterAnim(R.anim.pop_in)
        .setPopExitAnim(R.anim.pop_out)

private val loginNavOptions =
    NavOptions
        .Builder()
        .setPopUpTo(Screen.Onboarding, false)
        .setLaunchSingleTop(true)
        .setLaunchSingleTop(true)
        .setEnterAnim(R.anim.fade_in)
        .setExitAnim(R.anim.fade_out)
        .setPopEnterAnim(R.anim.fade_in)
        .setPopExitAnim(R.anim.fade_out)

fun NavController.navigateToMain() {
    popBackStack(Graph.Onboarding, true)
    navigate(route = Graph.Main, navOptions = graphNavOptions.build())
}

fun NavController.navigateToOnboarding() {
    popBackStack(Graph.Main, true)
    navigate(route = Graph.Onboarding, navOptions = graphNavOptions.build())
}

fun NavController.navigateToSignIn() {
    navigate(route = Screen.SignIn, navOptions = loginNavOptions.build())
}

fun NavController.navigateToSignUp() {
    navigate(route = Screen.SignUp, navOptions = loginNavOptions.build())
}

fun NavController.navigateToResetPassword(email: String) {
    navigate(
        route = Screen.ResetPassword(email = email),
        navOptions = defaultNavOptions.build(),
    )
}

fun NavController.navigateToPlaylist(
    playlistId: String,
    isLocal: Boolean,
) {
    navigate(
        route = Screen.Playlist(id = playlistId, isLocal = isLocal),
        navOptions = defaultNavOptions.build(),
    )
}

fun NavController.navigateToArtist(artistId: String) {
    navigate(
        route = Screen.Artist(id = artistId),
        navOptions = defaultNavOptions.build(),
    )
}

fun NavController.navigateToPlayer() {
    val navOptions =
        NavOptions
            .Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_bottom)
            .setExitAnim(R.anim.slide_out_bottom)
            .setPopEnterAnim(R.anim.slide_in_bottom)
            .setPopExitAnim(R.anim.slide_out_bottom)
    navigate(route = Screen.Player, navOptions = navOptions.build())
}

fun NavController.navigateToProfile() {
    navigate(route = Screen.Profile, navOptions = defaultNavOptions.build())
}

fun NavController.navigateToCategory(
    categoryId: String,
    title: String,
) {
    navigate(
        route = Screen.Category(id = categoryId, title = title),
        navOptions = defaultNavOptions.build(),
    )
}

fun NavController.navigateToSearchItems(
    query: String,
    type: SearchType,
) {
    navigate(
        route = Screen.SearchItems(query = query, type = type),
        navOptions = defaultNavOptions.build(),
    )
}
