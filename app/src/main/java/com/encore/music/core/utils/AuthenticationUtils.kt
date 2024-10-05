package com.encore.music.core.utils

import com.encore.music.R

object AuthenticationUtils {
    val authErrors =
        mapOf(
            "ERROR_INVALID_CUSTOM_TOKEN" to R.string.auth_error_custom_token,
            "ERROR_CUSTOM_TOKEN_MISMATCH" to R.string.auth_error_custom_token_mismatch,
            "ERROR_INVALID_CREDENTIAL" to R.string.auth_error_credential_malformed_or_expired,
            "ERROR_INVALID_EMAIL" to R.string.auth_error_invalid_email,
            "ERROR_WRONG_PASSWORD" to R.string.auth_error_wrong_password,
            "ERROR_USER_MISMATCH" to R.string.auth_error_user_mismatch,
            "ERROR_REQUIRES_RECENT_LOGIN" to R.string.auth_error_requires_recent_login,
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" to R.string.auth_error_accounts_exits_with_different_credential,
            "ERROR_EMAIL_ALREADY_IN_USE" to R.string.auth_error_email_already_in_use,
            "ERROR_CREDENTIAL_ALREADY_IN_USE" to R.string.auth_error_credential_already_in_use,
            "ERROR_USER_DISABLED" to R.string.auth_error_user_disabled,
            "ERROR_USER_TOKEN_EXPIRED" to R.string.auth_error_user_token_expired,
            "ERROR_USER_NOT_FOUND" to R.string.auth_error_user_not_found,
            "ERROR_INVALID_USER_TOKEN" to R.string.auth_error_invalid_user_token,
            "ERROR_OPERATION_NOT_ALLOWED" to R.string.auth_error_operation_not_allowed,
            "ERROR_WEAK_PASSWORD" to R.string.auth_error_password_is_weak,
            "ERROR_TOO_MANY_REQUESTS" to R.string.auth_error_too_many_requests,
        )
}
