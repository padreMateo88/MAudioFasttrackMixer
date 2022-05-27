package com.mpiotrowski.maudiofasttrackmixer.data.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings
import com.mpiotrowski.maudiofasttrackmixer.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class PresetsDatabaseTest {
    private lateinit var presetsDao: PresetsDao
    private lateinit var presetsDatabase: PresetsDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        presetsDatabase = Room.inMemoryDatabaseBuilder(
            context, PresetsDatabase::class.java
        ).setTransactionExecutor(Executors.newSingleThreadExecutor())
        .build()
        presetsDao = presetsDatabase.presetsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        presetsDatabase.close()
    }

    //region @Query tests

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun populateDatabase_getPersistedState_returnsLastPersistedStatePreset() = runTest {
        presetsDatabase.populateDatabase()
        val currentPreset = presetsDao.getPersistedState().getOrAwaitValue()
        MatcherAssert.assertThat( currentPreset.preset.presetId, CoreMatchers.`is`(LAST_PERSISTED_STATE_ID))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun populateDatabase_getCurrentPreset_returnsDefaultPreset() = runTest {
        presetsDatabase.populateDatabase()
        val currentPreset = presetsDao.getCurrentPreset()

        MatcherAssert.assertThat( currentPreset?.presetId, CoreMatchers.`is`(DEFAULT_PRESET_ID))
    }

}
