package com.encore.music.di

import com.encore.music.presentation.ui.activities.MainViewModel
import com.encore.music.presentation.ui.fragments.home.HomeViewModel
import com.encore.music.presentation.ui.fragments.library.LibraryViewModel
import com.encore.music.presentation.ui.fragments.resetPassword.ResetPasswordViewModel
import com.encore.music.presentation.ui.fragments.signIn.SignInViewModel
import com.encore.music.presentation.ui.fragments.signUp.SignUpViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::MainViewModel)
        viewModelOf(::SignUpViewModel)
        viewModelOf(::SignInViewModel)
        viewModelOf(::ResetPasswordViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::LibraryViewModel)
    }
