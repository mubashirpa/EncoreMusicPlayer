package com.encore.music.di

import androidx.room.Room
import com.encore.music.data.local.dao.PlaylistDao
import com.encore.music.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val roomModule =
    module {
        single<AppDatabase> {
            Room
                .databaseBuilder(
                    context = androidContext(),
                    klass = AppDatabase::class.java,
                    name = "app-database",
                ).build()
        }
        single<PlaylistDao> {
            get<AppDatabase>().playlistDao()
        }
    }
