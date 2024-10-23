package com.encore.music.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.encore.music.R
import com.encore.music.core.PagingSourceException
import com.encore.music.core.UiText
import com.encore.music.core.utils.KtorException
import com.encore.music.data.remote.dto.playlists.Playlist
import com.encore.music.data.repository.AuthenticationRepositoryImpl
import com.encore.music.data.repository.PlaylistsRepositoryImpl
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.PlaylistsRepository
import org.koin.java.KoinJavaComponent.inject
import java.net.ConnectException

class CategoryPlaylistsPagingSource(
    private val categoryId: String,
    private val offset: Int = 0,
) : PagingSource<Int, Playlist>() {
    private val authenticationRepository: AuthenticationRepository by inject(
        AuthenticationRepositoryImpl::class.java,
    )
    private val playlistsRepository: PlaylistsRepository by inject(
        PlaylistsRepositoryImpl::class.java,
    )

    override fun getRefreshKey(state: PagingState<Int, Playlist>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Playlist> =
        try {
            val limit = params.loadSize
            val currentOffset = params.key ?: offset

            val accessToken = authenticationRepository.getIdToken().orEmpty()
            val response =
                playlistsRepository.getCategoryPlaylists(accessToken, categoryId, limit, offset)
            val playlists = response.items.orEmpty()
            val total = response.total ?: playlists.size

            val prevKey =
                if (currentOffset > offset) (currentOffset - limit).coerceAtLeast(offset) else null
            val nextKey = if (currentOffset + limit < total) currentOffset + limit else null

            LoadResult.Page(
                data = playlists,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: ConnectException) {
            LoadResult.Error(
                PagingSourceException(
                    message = e.message,
                    localizedMessage = UiText.StringResource(R.string.error_connect),
                ),
            )
        } catch (e: KtorException) {
            LoadResult.Error(
                PagingSourceException(
                    message = e.message,
                    localizedMessage = e.localizedMessage,
                ),
            )
        } catch (e: Exception) {
            LoadResult.Error(
                PagingSourceException(
                    message = e.message,
                    localizedMessage = UiText.StringResource(R.string.error_unknown),
                ),
            )
        }
}
