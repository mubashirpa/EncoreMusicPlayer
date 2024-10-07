package com.encore.music.di

import com.encore.music.data.AndroidMailMatcher
import com.encore.music.domain.MailMatcher
import com.encore.music.domain.usecase.authentication.CreateUserUseCase
import com.encore.music.domain.usecase.authentication.GetCurrentUserIdUseCase
import com.encore.music.domain.usecase.authentication.GetCurrentUserUseCase
import com.encore.music.domain.usecase.authentication.GoogleSignInUseCase
import com.encore.music.domain.usecase.authentication.HasUserUseCase
import com.encore.music.domain.usecase.authentication.SendPasswordResetEmailUseCase
import com.encore.music.domain.usecase.authentication.SignInUseCase
import com.encore.music.domain.usecase.authentication.SignOutUseCase
import com.encore.music.domain.usecase.categories.GetCategoriesUseCase
import com.encore.music.domain.usecase.datastore.GetLoginPreferencesUseCase
import com.encore.music.domain.usecase.playlists.GetHomePlaylistsUseCase
import com.encore.music.domain.usecase.playlists.GetPlaylistUseCase
import com.encore.music.domain.usecase.songs.CreatePlaylistUseCase
import com.encore.music.domain.usecase.songs.FollowArtistUseCase
import com.encore.music.domain.usecase.songs.GetFollowedArtistsUseCase
import com.encore.music.domain.usecase.songs.GetRecentTracksUseCase
import com.encore.music.domain.usecase.songs.GetSavedPlaylistsUseCase
import com.encore.music.domain.usecase.songs.InsertPlaylistUseCase
import com.encore.music.domain.usecase.songs.InsertRecentTrackUseCase
import com.encore.music.domain.usecase.validation.ValidateEmail
import com.encore.music.domain.usecase.validation.ValidateName
import com.encore.music.domain.usecase.validation.ValidatePassword
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule =
    module {
        singleOf(::CreatePlaylistUseCase)
        singleOf(::FollowArtistUseCase)
        singleOf(::GetCategoriesUseCase)
        singleOf(::GetHomePlaylistsUseCase)
        singleOf(::GetLoginPreferencesUseCase)
        singleOf(::GetPlaylistUseCase)
        singleOf(::GetRecentTracksUseCase)
        singleOf(::GetFollowedArtistsUseCase)
        singleOf(::GetSavedPlaylistsUseCase)
        singleOf(::InsertPlaylistUseCase)
        singleOf(::InsertRecentTrackUseCase)

        // Authentication
        singleOf(::CreateUserUseCase)
        singleOf(::GetCurrentUserIdUseCase)
        singleOf(::GetCurrentUserUseCase)
        singleOf(::GoogleSignInUseCase)
        singleOf(::HasUserUseCase)
        singleOf(::SendPasswordResetEmailUseCase)
        singleOf(::SignInUseCase)
        singleOf(::SignOutUseCase)

        // Validation
        singleOf(::AndroidMailMatcher) { bind<MailMatcher>() }
        singleOf(::ValidateEmail)
        singleOf(::ValidateName)
        singleOf(::ValidatePassword)
    }
