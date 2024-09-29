package com.encore.music.core

sealed class Result<T>(
    val data: T? = null,
    val message: UiText? = null,
) {
    class Empty<T> : Result<T>()

    class Error<T>(
        message: UiText,
        data: T? = null,
    ) : Result<T>(data, message)

    class Loading<T>(
        data: T? = null,
    ) : Result<T>(data)

    class Success<T>(
        data: T?,
    ) : Result<T>(data)
}
