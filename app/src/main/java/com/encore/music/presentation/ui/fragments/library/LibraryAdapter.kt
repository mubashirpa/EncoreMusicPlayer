package com.encore.music.presentation.ui.fragments.library

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.encore.music.R
import com.encore.music.databinding.LayoutLibraryArtistsBinding
import com.encore.music.databinding.LayoutLibraryPlaylistsBinding
import com.encore.music.databinding.LayoutLibraryTracksBinding
import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track
import com.encore.music.presentation.utils.HorizontalItemDecoration
import com.encore.music.presentation.utils.PaddingValues

class LibraryAdapter(
    private val context: Context,
    var items: MutableList<LibraryListItem>,
    private val onArtistClicked: (Artist) -> Unit,
    private val onPlaylistClicked: (Playlist) -> Unit,
    private val onTrackClicked: (Track) -> Unit,
    private val onTrackMoreClicked: (Track) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val horizontalItemDecoration =
        HorizontalItemDecoration(
            contentPadding =
                PaddingValues(
                    start = 16,
                    top = 0,
                    end = 16,
                    bottom = 0,
                ),
            horizontalSpacing = 10,
        )

    inner class ArtistsViewHolder(
        private val binding: LayoutLibraryArtistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val artistsAdapter = ArtistsAdapter(onArtistClicked)

        fun bind(item: LibraryListItem.ArtistsItem) {
            artistsAdapter.items = item.artists
            binding.apply {
                title.text = item.title
                val hasArtists = item.artists.isNotEmpty()
                recyclerView.isVisible = hasArtists
                errorView.root.isVisible = !hasArtists

                if (!hasArtists) {
                    errorView.errorText.text =
                        context.getString(R.string.you_are_not_following_any_artists)
                } else {
                    recyclerView.apply {
                        if (itemDecorationCount == 0) addItemDecoration(horizontalItemDecoration)
                        adapter = artistsAdapter
                    }
                }
            }
        }
    }

    inner class PlaylistsViewHolder(
        private val binding: LayoutLibraryPlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val playlistsAdapter = PlaylistsAdapter(onPlaylistClicked)

        fun bind(item: LibraryListItem.PlaylistsItem) {
            playlistsAdapter.items = item.playlists
            binding.apply {
                title.text = item.title
                val hasPlaylists = item.playlists.isNotEmpty()
                recyclerView.isVisible = hasPlaylists
                errorView.root.isVisible = !hasPlaylists

                if (!hasPlaylists) {
                    errorView.errorText.text =
                        context.getString(R.string.you_don_t_have_any_playlists)
                } else {
                    recyclerView.apply {
                        if (itemDecorationCount == 0) addItemDecoration(horizontalItemDecoration)
                        adapter = playlistsAdapter
                    }
                }
            }
        }
    }

    inner class TracksViewHolder(
        private val binding: LayoutLibraryTracksBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val tracksAdapter =
            TracksAdapter(
                onTrackClicked = onTrackClicked,
                onTrackMoreClicked = onTrackMoreClicked,
            )

        fun bind(item: LibraryListItem.TracksItem) {
            tracksAdapter.items = item.tracks
            binding.apply {
                title.text = item.title
                val hasTracks = item.tracks.isNotEmpty()
                recyclerView.isVisible = hasTracks
                errorView.root.isVisible = !hasTracks

                if (!hasTracks) {
                    errorView.errorText.text =
                        context.getString(R.string.no_recent_activity)
                } else {
                    recyclerView.adapter = tracksAdapter
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val binding =
                    LayoutLibraryArtistsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                return ArtistsViewHolder(binding)
            }

            1 -> {
                val binding =
                    LayoutLibraryPlaylistsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                return PlaylistsViewHolder(binding)
            }

            2 -> {
                val binding =
                    LayoutLibraryTracksBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false,
                    )
                return TracksViewHolder(binding)
            }

            else -> throw IllegalArgumentException(context.getString(R.string.invalid_view_type))
        }
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (val item = items[position]) {
            is LibraryListItem.ArtistsItem -> (holder as ArtistsViewHolder).bind(item)
            is LibraryListItem.PlaylistsItem -> (holder as PlaylistsViewHolder).bind(item)
            is LibraryListItem.TracksItem -> (holder as TracksViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is LibraryListItem.ArtistsItem -> 0
            is LibraryListItem.PlaylistsItem -> 1
            is LibraryListItem.TracksItem -> 2
        }
}
