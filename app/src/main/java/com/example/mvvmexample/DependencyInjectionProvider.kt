package com.example.mvvmexample

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.module.Module

object DependencyInjectionProvider : KoinComponent {

    fun init(application: Application, vararg modules: Module) {
        startKoin {
            androidContext(application)
            modules(modules = modules)
        }
    }
}