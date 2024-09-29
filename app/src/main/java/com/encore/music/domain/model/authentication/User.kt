package com.encore.music.domain.model.authentication

data class User(
    val email: String,
    val id: String,
    val name: String,
    val photoUrl: String,
    val verified: Boolean,
)
