package com.encore.music.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.encore.music.R
import com.encore.music.core.PagingSourceException
import com.encore.music.core.UiText
import com.encore.music.core.utils.KtorException
import com.encore.music.data.remote.dto.search.SearchItem
import com.encore.music.data.repository.AuthenticationRepositoryImpl
import com.encore.music.data.repository.SearchRepositoryImpl
import com.encore.music.domain.model.search.SearchType
import com.encore.music.domain.repository.AuthenticationRepository
import com.encore.music.domain.repository.SearchRepository
import org.koin.java.KoinJavaComponent.inject
import java.net.ConnectException

class SearchPagingSource(
    private val query: String,
    private val type: List<SearchType>,
    private val market: String? = null,
    private val offset: Int = 0,
    private val includeExternal: String? = null,
) : PagingSource<Int, SearchItem>() {
    private val authenticationRepository: AuthenticationRepository by inject(
        AuthenticationRepositoryImpl::class.java,
    )
    private val searchRepository: SearchRepository by inject(
        SearchRepositoryImpl::class.java,
    )

    override fun getRefreshKey(state: PagingState<Int, SearchItem>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchItem> =
        try {
            val limit = params.loadSize
            val currentOffset = params.key ?: offset

            val accessToken = authenticationRepository.getIdToken().orEmpty()
            val response =
                searchRepository.searchForItem(
                    accessToken = accessToken,
                    query = query,
                    type = type,
                    market = market,
                    limit = limit,
                    offset = currentOffset,
                    includeExternal = includeExternal,
                )
            val data: List<SearchItem>
            val total: Int
            when {
                response.tracks != null -> {
                    data =
                        response.tracks.items
                            .orEmpty()
                            .map { SearchItem.TrackItem(it) }
                    total = response.tracks.total ?: data.size
                }

                response.artists != null -> {
                    data =
                        response.artists.items
                            .orEmpty()
                            .map { SearchItem.ArtistItem(it) }
                    total = response.artists.total ?: data.size
                }

                response.playlists != null -> {
                    data =
                        response.playlists.items
                            .orEmpty()
                            .map { SearchItem.PlaylistItem(it) }
                    total = response.playlists.total ?: data.size
                }

                else -> {
                    data = emptyList()
                    total = 0
                }
            }

            val prevKey =
                if (currentOffset > offset) (currentOffset - limit).coerceAtLeast(offset) else null
            val nextKey = if (currentOffset + limit < total) currentOffset + limit else null

            LoadResult.Page(
                data = data,
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
