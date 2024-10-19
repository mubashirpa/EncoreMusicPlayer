package com.encore.music.core.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T : Any> HttpResponse.toResult(): T =
    when (status.value) {
        200 -> body<T>()
        400 -> throw NetworkException("Check your credentials and try again!")
        401 -> throw NetworkException("Authorization Failed! Try Logging In again.")
        500, 503 -> throw NetworkException("Server Disruption! We are on fixing it.")
        504 -> throw NetworkException("Too much load at this time, try again later!")
        else -> throw NetworkException("Something went wrong! Please try again or contact support.")
    }

class NetworkException(
    message: String,
) : Exception(message)
