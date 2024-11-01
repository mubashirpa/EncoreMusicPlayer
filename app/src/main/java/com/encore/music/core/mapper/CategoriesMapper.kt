package com.encore.music.core.mapper

import com.encore.music.data.remote.dto.categories.CategoriesDto
import com.encore.music.data.remote.dto.categories.Category
import com.encore.music.domain.model.categories.Category as CategoryDomainModel

fun CategoriesDto.toCategoryDomainModelList(): List<CategoryDomainModel> = items?.map { it.toCategoryDomainModel() }.orEmpty()

fun Category.toCategoryDomainModel(): CategoryDomainModel =
    CategoryDomainModel(
        icon = icon,
        id = id,
        name = name,
        playlists = playlists?.map { it.toPlaylistDomainModel() },
    )
