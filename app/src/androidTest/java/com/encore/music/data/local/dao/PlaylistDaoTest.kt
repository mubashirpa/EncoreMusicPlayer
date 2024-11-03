package com.encore.music.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.encore.music.data.local.database.SongsDatabase
import com.encore.music.data.local.entity.playlists.PlaylistEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class PlaylistDaoTest {
    private lateinit var playlistsDao: PlaylistsDao
    private lateinit var database: SongsDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room
                .inMemoryDatabaseBuilder(context, SongsDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        playlistsDao = database.playlistsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertPlaylist() =
        runBlocking {
            val playlistEntity =
                PlaylistEntity(
                    playlistId = "0",
                    addedAt = 0,
                    description = "Test Playlist",
                    externalUrl = "test_url",
                    image = "test_image",
                    isLocal = true,
                    name = "Test Playlist",
                    owner = "test_owner",
                    ownerId = "test_owner_id",
                )
            playlistsDao.insertPlaylist(playlistEntity)
            val playlists = playlistsDao.getPlaylists().firstOrNull().orEmpty()
            assertThat(playlists).contains(playlistEntity)
        }
}
