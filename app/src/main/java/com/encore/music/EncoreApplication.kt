package com.encore.music

import android.app.Application
import com.encore.music.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EncoreApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@EncoreApplication)
            modules(appModule)
        }
    }
}
