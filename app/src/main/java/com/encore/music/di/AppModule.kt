package com.encore.music.di

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
    }
