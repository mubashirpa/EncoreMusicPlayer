package com.encore.music.domain.usecase.validation

import com.encore.music.core.UiText
import com.encore.music.domain.MailMatcher
import com.encore.music.R.string as Strings

class ValidateEmail(
    private val mailMatcher: MailMatcher,
) {
    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_blank_email),
            )
        }
        if (!mailMatcher.matches(email)) {
            return ValidationResult(
                successful = false,
                error = UiText.StringResource(Strings.error_invalid_email),
            )
        }
        return ValidationResult(
            successful = true,
        )
    }
}
