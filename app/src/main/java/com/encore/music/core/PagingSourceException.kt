package com.encore.music.core

class PagingSourceException(
    message: String?,
    val localizedMessage: UiText,
) : Exception(message)
