package com.encore.music.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val ktorModule =
    module {
        single {
            HttpClient {
                expectSuccess = true
                install(HttpTimeout) {
                    val timeout = 30000L
                    connectTimeoutMillis = timeout
                    requestTimeoutMillis = timeout
                    socketTimeoutMillis = timeout
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                            useAlternativeNames = false
                        },
                    )
                }
            }
        }
    }
