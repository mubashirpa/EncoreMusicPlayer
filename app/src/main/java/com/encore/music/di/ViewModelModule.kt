package com.encore.music.di

import com.encore.music.presentation.ui.activities.MainViewModel
import com.encore.music.presentation.ui.fragments.artist.ArtistViewModel
import com.encore.music.presentation.ui.fragments.category.CategoryViewModel
import com.encore.music.presentation.ui.fragments.home.HomeViewModel
import com.encore.music.presentation.ui.fragments.library.LibraryViewModel
import com.encore.music.presentation.ui.fragments.playlist.PlaylistViewModel
import com.encore.music.presentation.ui.fragments.profile.ProfileViewModel
import com.encore.music.presentation.ui.fragments.resetPassword.ResetPasswordViewModel
import com.encore.music.presentation.ui.fragments.search.SearchViewModel
import com.encore.music.presentation.ui.fragments.searchItems.SearchItemsViewModel
import com.encore.music.presentation.ui.fragments.signIn.SignInViewModel
import com.encore.music.presentation.ui.fragments.signUp.SignUpViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::ArtistViewModel)
        viewModelOf(::CategoryViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::LibraryViewModel)
        viewModelOf(::MainViewModel)
        viewModelOf(::PlaylistViewModel)
        viewModelOf(::ProfileViewModel)
        viewModelOf(::ResetPasswordViewModel)
        viewModelOf(::SearchItemsViewModel)
        viewModelOf(::SearchViewModel)
        viewModelOf(::SignInViewModel)
        viewModelOf(::SignUpViewModel)
    }
