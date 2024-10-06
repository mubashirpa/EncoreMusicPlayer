package com.encore.music.domain.repository

import com.encore.music.data.remote.dto.categories.CategoriesDto

interface CategoriesRepository {
    suspend fun getCategories(
        accessToken: String,
        locale: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): CategoriesDto
}
