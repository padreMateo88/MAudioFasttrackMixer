package com.mpiotrowski.maudiofasttrackmixer.data.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.CurrentPreset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
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
    private lateinit var userDao: PresetsDao
    private lateinit var db: PresetsDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, PresetsDatabase::class.java
        ).setTransactionExecutor(Executors.newSingleThreadExecutor())
        .build()
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
    fun insertCurrentPreset_getCurrentPreset_returnsInsertedValue() = runTest {
        userDao.insertCurrentPreset(CurrentPreset(presetId = "testId1"))
        val currentPreset = userDao.getCurrentPreset()

        MatcherAssert.assertThat( currentPreset.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat( currentPreset[0].presetId, CoreMatchers.`is`("testId1"))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun addPreset_getPreset_returnsInsertedValue() = runTest {
        val testPreset = Preset(presetName = "testPreset")
        val testPresetId = testPreset.presetId
        userDao.addPreset(testPreset)
        val presetsMap =  userDao.getPresetsWithScenes().getOrAwaitValue().map{it.preset.presetId to it}.toMap()
        MatcherAssert.assertThat( presetsMap.containsKey(testPresetId), CoreMatchers.`is`(true))
        MatcherAssert.assertThat( presetsMap[testPresetId]?.preset?.presetName, CoreMatchers.`is`("testPreset"))
    }

}
