package com.encore.music.domain.usecase.validation

import com.encore.music.core.UiText

data class ValidationResult(
    val successful: Boolean,
    val error: UiText? = null,
)
