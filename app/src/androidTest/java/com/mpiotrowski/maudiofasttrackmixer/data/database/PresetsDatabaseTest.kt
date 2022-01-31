package com.mpiotrowski.maudiofasttrackmixer.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.CurrentPreset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PresetsDatabaseTest {
    private lateinit var userDao: PresetsDao
    private lateinit var db: PresetsDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, PresetsDatabase::class.java
        ).build()
        userDao = db.presetsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runTest {
        userDao.insertCurrentPreset(CurrentPreset(presetId = "testId1"))
        val currentPreset = userDao.getCurrentPreset()

        MatcherAssert.assertThat( currentPreset.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat( currentPreset[0].presetId, CoreMatchers.`is`("testId1"))
    }

}
