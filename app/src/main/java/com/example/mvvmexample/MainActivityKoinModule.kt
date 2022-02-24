package com.example.mvvmexample

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainActivityModule = module {
    viewModel {
        MainActivityViewModel(
            httpClient = get(),
            navigation = get()
        )
    }
}