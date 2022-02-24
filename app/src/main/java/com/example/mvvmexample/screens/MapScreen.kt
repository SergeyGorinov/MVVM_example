package com.example.mvvmexample.screens

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mvvmexample.*
import com.example.mvvmexample.geocoding.GeocodingData
import com.example.mvvmexample.geocoding.Point
import com.example.platform.network.HttpClient
import com.example.platform.network.HttpResult
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.MapStyle
import com.here.sdk.mapviewlite.MapViewLite
import io.ktor.client.call.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

object MapScreen {

    @Composable
    fun MapView() {
        Box(modifier = Modifier.fillMaxSize()) {
            AddressMapView(modifier = Modifier.fillMaxSize())

            Pin(modifier = Modifier.align(Alignment.Center))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                AddressWithButtonView(modifier = Modifier.fillMaxWidth())
            }
        }
    }

    @Composable
    fun AddressMapView(modifier: Modifier = Modifier) {
        val scope = rememberCoroutineScope()
        val viewModel = getViewModel<MainActivityViewModel>()
        val handleChange = remember {
            val flow = MutableStateFlow<Point?>(null)
            flow.debounce(1000L).filterNotNull().onEach { point ->
                requestAddressData(point = point, viewModel = viewModel)
            }.launchIn(scope)
            flow
        }
        AndroidView(
            update = {},
            factory = { context ->
                MapViewLite(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    onCreate(null)

                    camera.addObserver {
                        scope.launch {
                            handleChange.emit(
                                Point(
                                    it.target.latitude.toFloat(),
                                    it.target.longitude.toFloat()
                                )
                            )
                        }
                    }
                    mapScene.loadScene(MapStyle.NORMAL_DAY) { errorCode ->
                        if (errorCode == null) {
                            camera.target = GeoCoordinates(55.751244, 37.618423)

                            camera.zoomLevel = 14.0
                        } else {
                            Log.d("MapView", "onLoadScene failed: $errorCode")
                        }
                    }
                }
            },
            modifier = modifier
        )
    }

    @Composable
    fun AddressWithButtonView(modifier: Modifier = Modifier) {
        val viewModel = getViewModel<MainActivityViewModel>()
        val addressText = when (
            val screenState = viewModel.screenState.collectAsState(AddressScreenState.Empty).value
        ) {
            is AddressScreenState.AddressData -> screenState.address
            is AddressScreenState.Loading -> "Загрузка..."
            is AddressScreenState.Error -> "Ошибка получения адреса"
            is AddressScreenState.Empty -> ""
            else -> "Не определён"
        }

        Column(modifier = modifier) {
            Text(
                text = addressText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Button(onClick = { viewModel.openEmptyScreen() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Привезти сюда", textAlign = TextAlign.Center)
            }
        }
    }

    private suspend fun requestAddressData(
        point: Point,
        limit: Int = 24,
        radius: Int = 200,
        viewModel: MainActivityViewModel
    ) {
        val apiKey = "QhIRUoC1dYf1prIgKmJgH5v6rxOGssFSPnCikBR8lJ4"
        val url = "https://discover.search.hereapi.com/v1"
        val requestUrl =
            "${url}/revgeocode?in=circle:${point.lat.toBigDecimal()},${point.lng.toBigDecimal()};r=$radius&lang=ru&apiKey=${apiKey}&show=streetInfo&limit=$limit"
        val params = HttpClient.RequestParams(
            method = HttpClient.Method.GET,
            url = requestUrl
        )



        viewModel.sendHttpRequest(
            initialState = AddressScreenState.Loading,
            params = params
        ) { data ->
            when (data) {
                is HttpResult.Success -> {
                    val address =
                        data.response.receive<GeocodingData>().items.firstOrNull {
                            it.address.city != null && it.address.street != null
                        }?.toAddress()
                    if (address != null) {
                        AddressScreenState.AddressData(address.concatAddress())
                    } else {
                        AddressScreenState.Empty
                    }
                }
                is HttpResult.Error -> {
                    AddressScreenState.Error
                }
            }
        }
    }

    sealed class AddressScreenState(override val screenId: String = id) :
        com.example.platform.navigation.ScreenState {

        object Empty : AddressScreenState()
        object Loading : AddressScreenState()
        object Error : AddressScreenState()

        data class AddressData(val address: String) : AddressScreenState()

        private companion object {
            const val id = "AddressScreen"
        }
    }
}