package com.encore.music.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.encore.music.data.local.dao.ArtistsDao
import com.encore.music.data.local.dao.PlaylistsDao
import com.encore.music.data.local.dao.TracksDao
import com.encore.music.data.local.entity.artist.ArtistEntity
import com.encore.music.data.local.entity.playlists.PlaylistEntity
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.tracks.TrackArtistCrossRef
import com.encore.music.data.local.entity.tracks.TrackEntity

@Database(
    entities = [
        ArtistEntity::class,
        PlaylistEntity::class, PlaylistTrackCrossRef::class,
        TrackArtistCrossRef::class, TrackEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class SongsDatabase : RoomDatabase() {
    abstract fun playlistsDao(): PlaylistsDao

    abstract fun tracksDao(): TracksDao

    abstract fun artistsDao(): ArtistsDao
}
