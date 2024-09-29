package com.encore.music.domain.repository

import com.encore.music.data.remote.dto.playlists.PlaylistsDto

interface EncoreRepository {
    suspend fun getFeaturedPlaylists(
        accessToken: String,
        locale: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): PlaylistsDto
}
