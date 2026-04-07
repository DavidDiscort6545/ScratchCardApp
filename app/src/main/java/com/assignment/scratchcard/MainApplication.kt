package com.assignment.scratchcard

import android.app.Application
import com.assignment.scratchcard.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // standard logging for Koin
            // can be used when analysing unexpected behavior with dependency injection
            androidLogger()
            //context used by Room
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}