package com.encore.music.di

import com.encore.music.data.repository.AuthenticationRepositoryImpl
import com.encore.music.data.repository.CategoriesRepositoryImpl
import com.encore.music.data.repository.DatastoreRepositoryImpl
import com.encore.music.data.repository.PlaylistsRepositoryImpl
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.CategoriesRepository
import com.encore.music.domain.repository.DatastoreRepository
import com.encore.music.domain.repository.PlaylistsRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::AuthenticationRepositoryImpl) { bind<AuthenticationRepository>() }
        singleOf(::CategoriesRepositoryImpl) { bind<CategoriesRepository>() }
        singleOf(::DatastoreRepositoryImpl) { bind<DatastoreRepository>() }
        singleOf(::PlaylistsRepositoryImpl) { bind<PlaylistsRepository>() }
    }
