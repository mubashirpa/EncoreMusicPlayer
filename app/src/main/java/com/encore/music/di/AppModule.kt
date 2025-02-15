package com.encore.music.di

import com.encore.music.data.local.ContentResolverHelper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule =
    module {
        includes(
            firebaseModule,
            ktorModule,
            playerModule,
            repositoryModule,
            roomModule,
            useCaseModule,
            viewModelModule,
        )
        singleOf(::ContentResolverHelper)
    }
