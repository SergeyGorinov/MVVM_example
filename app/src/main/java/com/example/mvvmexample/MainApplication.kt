package com.example.mvvmexample

import android.app.Application
import com.example.platform.platformModule

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DependencyInjectionProvider.init(application = this, mainActivityModule, platformModule)
    }
}