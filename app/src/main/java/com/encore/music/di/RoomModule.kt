package com.encore.music.di

import androidx.room.Room
import com.encore.music.data.local.dao.ArtistsDao
import com.encore.music.data.local.dao.PlaylistsDao
import com.encore.music.data.local.dao.TracksDao
import com.encore.music.data.local.database.SongsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val roomModule =
    module {
        single<SongsDatabase> {
            Room
                .databaseBuilder(
                    context = androidContext(),
                    klass = SongsDatabase::class.java,
                    name = "songs-database",
                ).build()
        }
        single<ArtistsDao> {
            get<SongsDatabase>().artistsDao()
        }
        single<PlaylistsDao> {
            get<SongsDatabase>().playlistsDao()
        }
        single<TracksDao> {
            get<SongsDatabase>().tracksDao()
        }
    }
