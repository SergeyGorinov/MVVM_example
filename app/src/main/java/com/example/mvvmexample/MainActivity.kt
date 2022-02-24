package com.example.mvvmexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvvmexample.screens.EmptyScreen
import com.example.mvvmexample.screens.MapScreen
import com.example.mvvmexample.ui.theme.MVVMExampleTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVVMExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Content()
                }
            }
        }
    }

    override fun onBackPressed() {
        lifecycleScope.launchWhenResumed {
            if (!viewModel.navigateBack()) {
                super.onBackPressed()
            }
        }
    }

    @Composable
    fun Content() {
        val navController = rememberNavController()
        val viewModel = getViewModel<MainActivityViewModel>()
        val screenState = viewModel.screenState.collectAsState(
            initial = MapScreen.AddressScreenState.Empty
        ).value

        navController.enableOnBackPressed(false)

        NavHost(
            navController = navController,
            startDestination = MapScreen.AddressScreenState.Empty.screenId
        ) {
            composable(MapScreen.AddressScreenState.Empty.screenId) { MapScreen.MapView() }
            composable(EmptyScreen.EmptyScreenState.Sample.screenId) { EmptyScreen.EmptyView() }
        }

        if (navController.currentDestination?.route != screenState.screenId) {
            navController.navigate(screenState.screenId)
        }
    }
}