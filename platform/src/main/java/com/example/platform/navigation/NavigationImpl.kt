package com.example.platform.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

internal class NavigationImpl : Navigation {

    private val screenStack = hashMapOf<String, ScreenState>()

    private val mutableScreenState = MutableSharedFlow<ScreenState>()

    override val currentScreenState: Flow<ScreenState> = mutableScreenState

    override suspend fun navigateTo(screenState: ScreenState) {
        screenStack[screenState.screenId] = screenState
        mutableScreenState.emit(screenStack.values.last())
    }

    override suspend fun navigateBack(): Boolean {
        val lastScreen = screenStack.keys.last()
        screenStack.remove(lastScreen)
        return if (screenStack.isEmpty()) {
            false
        } else {
            mutableScreenState.emit(screenStack.values.last())
            true
        }
    }
}