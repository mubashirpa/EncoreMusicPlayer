package com.encore.music.domain.usecase.playlists

import com.encore.music.data.local.dao.PlaylistDao
import com.encore.music.data.local.entity.playlists.Playlist
import com.encore.music.data.local.entity.tracks.Track
import com.encore.music.domain.model.playlists.Playlist as PlaylistDomainModel

class InsertPlaylistUseCase(
    private val playlistDao: PlaylistDao,
) {
    suspend operator fun invoke(playlistDomainModel: PlaylistDomainModel) {
        val playlist =
            Playlist(
                playlistId = playlistDomainModel.id!!,
                description = playlistDomainModel.description,
                image = playlistDomainModel.image,
                name = playlistDomainModel.name,
                owner = playlistDomainModel.owner,
            )
        val tracks =
            playlistDomainModel.tracks
                ?.map {
                    Track(
                        trackId = it.id!!,
                        image = it.image,
                        name = it.name,
                        mediaUrl = it.mediaUrl,
                    )
                }

        playlistDao.insertPlaylist(playlist)
        tracks?.let {
            playlistDao.insertTracks(tracks)
        }
    }
}
