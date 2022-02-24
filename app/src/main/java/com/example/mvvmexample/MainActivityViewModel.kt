package com.example.mvvmexample

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.platform.navigation.Navigation
import com.example.platform.navigation.ScreenState
import com.example.mvvmexample.screens.EmptyScreen
import com.example.platform.network.HttpClient
import com.example.platform.network.HttpResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val httpClient: HttpClient,
    private val navigation: Navigation
) : ViewModel() {

    val screenState = navigation.currentScreenState.distinctUntilChanged()

    private val sharedState = MutableStateFlow<Any?>(null)

    fun openEmptyScreen() {
        viewModelScope.launch {
            navigation.navigateTo(EmptyScreen.EmptyScreenState.Sample)
        }
    }

    suspend fun navigateBack(): Boolean {
        return navigation.navigateBack()
    }

    fun sendHttpRequest(
        initialState: ScreenState,
        params: HttpClient.RequestParams,
        request: suspend (HttpResult) -> ScreenState
    ) {
        viewModelScope.launch {
            navigation.navigateTo(initialState)
            val data = httpClient.request(params)
            navigation.navigateTo(request(data))
        }
    }
}