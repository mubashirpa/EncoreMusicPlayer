package com.encore.music.domain.model.preferences

data class LoginPreferences(
    val email: String,
    val password: String,
    val remember: Boolean,
)
