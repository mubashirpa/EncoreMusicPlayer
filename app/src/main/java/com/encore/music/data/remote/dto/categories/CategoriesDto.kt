package com.encore.music.data.remote.dto.categories

import kotlinx.serialization.Serializable

@Serializable
data class CategoriesDto(
    val limit: Int? = null,
    val offset: Int? = null,
    val total: Int? = null,
    val items: List<Category>? = null,
)
