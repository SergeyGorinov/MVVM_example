package com.example.mvvmexample.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.platform.navigation.ScreenState

object EmptyScreen {

    @Composable
    fun EmptyView() {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Sample text", modifier = Modifier.align(Alignment.Center))
        }
    }

    sealed class EmptyScreenState(override val screenId: String = id) : ScreenState {

        object Sample : EmptyScreenState()

        private companion object {
            const val id = "EmptyScreen"
        }
    }
}