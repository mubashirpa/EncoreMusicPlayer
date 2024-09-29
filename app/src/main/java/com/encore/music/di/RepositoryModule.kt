package com.encore.music.di

import com.encore.music.data.repository.AuthenticationRepositoryImpl
import com.encore.music.data.repository.DatastoreRepositoryImpl
import com.encore.music.data.repository.EncoreRepositoryImpl
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.DatastoreRepository
import com.encore.music.domain.repository.EncoreRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::EncoreRepositoryImpl) { bind<EncoreRepository>() }
        singleOf(::AuthenticationRepositoryImpl) { bind<AuthenticationRepository>() }
        singleOf(::DatastoreRepositoryImpl) { bind<DatastoreRepository>() }
    }
