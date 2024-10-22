package com.encore.music.core.utils

import com.encore.music.R
import com.encore.music.core.UiText
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T : Any> HttpResponse.toResult(): T =
    when (status.value) {
        200 -> body<T>()
        400 -> throw KtorException(
            status.description,
            UiText.StringResource(R.string.error_ktor_bad_request),
        )

        401 -> throw KtorException(
            status.description,
            UiText.StringResource(R.string.error_ktor_unauthorized),
        )

        500, 503 -> throw KtorException(
            status.description,
            UiText.StringResource(R.string.error_ktor_server_error),
        )

        504 -> throw KtorException(
            status.description,
            UiText.StringResource(R.string.error_ktor_gateway_timeout),
        )

        else -> throw KtorException(
            status.description,
            UiText.StringResource(R.string.error_unknown),
        )
    }

class KtorException(
    message: String?,
    val localizedMessage: UiText,
) : Exception(message)
