package com.encore.music.domain.usecase.validation

import com.encore.music.core.UiText
import com.encore.music.R.string as Strings

class ValidateName {
    fun execute(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_blank_name),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
