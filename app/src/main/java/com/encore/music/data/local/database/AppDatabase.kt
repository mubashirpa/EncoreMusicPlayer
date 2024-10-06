package com.encore.music.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.encore.music.data.local.dao.PlaylistDao
import com.encore.music.data.local.entity.artist.Artist
import com.encore.music.data.local.entity.playlists.Playlist
import com.encore.music.data.local.entity.playlists.PlaylistTrackCrossRef
import com.encore.music.data.local.entity.tracks.Track

@Database(
    entities = [Playlist::class, Track::class, Artist::class, PlaylistTrackCrossRef::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}
