package com.example.platform.navigation

import kotlinx.coroutines.flow.Flow

interface Navigation {

    val currentScreenState: Flow<ScreenState>

    suspend fun navigateTo(screenState: ScreenState)

    suspend fun navigateBack(): Boolean
}