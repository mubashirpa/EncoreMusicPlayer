package com.encore.music.domain.model.spotify.categories

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String = "",
    val imageUrl: String = "",
    val name: String = "",
)