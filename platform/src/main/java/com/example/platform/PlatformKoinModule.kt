package com.example.platform

import com.example.platform.navigation.Navigation
import com.example.platform.navigation.NavigationImpl
import com.example.platform.network.AndroidHttpClient
import com.example.platform.network.HttpClient
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val platformModule = module {

    single(named("android")) {
        io.ktor.client.HttpClient(Android) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    single { AndroidHttpClient(get(named("android"))) } bind HttpClient::class

    single { NavigationImpl() } bind Navigation::class
}