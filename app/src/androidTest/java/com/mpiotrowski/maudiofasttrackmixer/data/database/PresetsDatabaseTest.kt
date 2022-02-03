package com.mpiotrowski.maudiofasttrackmixer.data.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.CurrentPreset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.LAST_PERSISTED_STATE_NAME
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSettings
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

    //region @GET tests

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun addPreset_getPresetsWithScenes_containsAddedPreset() = runTest {
        val testPreset = Preset(presetName = "testPreset")
        val testPresetId = testPreset.presetId
        presetsDao.addPreset(testPreset)
        val presetsMap =  presetsDao.getPresetsWithScenes().getOrAwaitValue().map{it.preset.presetId to it}.toMap()
        MatcherAssert.assertThat( presetsMap.containsKey(testPresetId), CoreMatchers.`is`(true))
        MatcherAssert.assertThat( presetsMap[testPresetId]?.preset?.presetName, CoreMatchers.`is`("testPreset"))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun addPreset_getPreset_containsAddedPreset() = runTest {
        val testPreset = Preset(presetName = "testPreset")
        val testPresetId = testPreset.presetId
        presetsDao.addPreset(testPreset)
        val presetsMap =  presetsDao.getPreset(testPresetId).map{it.preset.presetId to it}.toMap()
        MatcherAssert.assertThat( presetsMap.containsKey(testPresetId), CoreMatchers.`is`(true))
        MatcherAssert.assertThat( presetsMap[testPresetId]?.preset?.presetName, CoreMatchers.`is`("testPreset"))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun insertCurrentPreset_getCurrentPreset_returnsInsertedValue() = runTest {
        presetsDao.insertCurrentPreset(CurrentPreset(presetId = "testId1"))
        val currentPreset = presetsDao.getCurrentPreset()

        MatcherAssert.assertThat( currentPreset.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat( currentPreset[0].presetId, CoreMatchers.`is`("testId1"))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun insertCurrentPreset_getCurrentPresetInstance_returnsInsertedValue() = runTest {
        val testPreset = Preset(presetName = "testPreset")
        val testPresetId = testPreset.presetId
        presetsDao.addPreset(testPreset)


        presetsDao.insertCurrentPreset(CurrentPreset(presetId = testPresetId))
        val currentPresetInstance = presetsDao.getCurrentPresetInstance()

        MatcherAssert.assertThat( currentPresetInstance.preset.presetName, CoreMatchers.`is`("testPreset"))
        MatcherAssert.assertThat( currentPresetInstance.preset.presetId, CoreMatchers.`is`(testPresetId))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun insertCurrentPreset_getCurrentPresetLiveData_returnsInsertedValue() = runTest {
        presetsDao.insertCurrentPreset(CurrentPreset(presetId = "testId1"))
        val currentPreset = presetsDao.getCurrentPresetLiveData().getOrAwaitValue()

        MatcherAssert.assertThat( currentPreset.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat( currentPreset[0].presetId, CoreMatchers.`is`("testId1"))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun populateDatabase_getPersistedState_returnsLastPersistedStatePreset() = runTest {
        presetsDatabase.populateDatabase()
        val currentPreset = presetsDao.getPersistedState().getOrAwaitValue()

        MatcherAssert.assertThat( currentPreset.preset.presetName, CoreMatchers.`is`(LAST_PERSISTED_STATE_NAME))
    }

    //endregion @GET tests

    //region @INSERT tests

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun addPresetWithScenes_getPreset_scenesInsertedCorrectly() = runTest {

        val testPreset = Preset(presetName = "TestPreset")
        val testPresetId = testPreset.presetId
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)

        val insertedScene = presetWithScenes.scenes[0]

        insertedScene.scene.sceneName = "TestScene"
        insertedScene.scene.sceneOrder = 5
        insertedScene.scene.fxSettings.duration = 40
        insertedScene.scene.fxSettings.feedback = 50
        insertedScene.scene.fxSettings.volume = 60
        insertedScene.scene.fxSettings.fxMute = true
        insertedScene.scene.fxSettings.fxType = FxSettings.FxType.ROOM3

        presetsDao.insertPresetWithScenes(presetWithScenes)

        val presetsMap =  presetsDao.getPreset(testPresetId).map{it.preset.presetId to it}.toMap()

        MatcherAssert.assertThat( presetsMap.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat( presetsMap.containsKey(testPresetId), CoreMatchers.`is`(true))

        val presetFromDatabase = presetsMap[testPreset.presetId]
        val sceneFromDatabase = presetFromDatabase?.scenes?.get(0)?.scene

        MatcherAssert.assertThat(sceneFromDatabase?.sceneName, CoreMatchers.`is`(insertedScene.scene.sceneName))
        MatcherAssert.assertThat(sceneFromDatabase?.sceneOrder, CoreMatchers.`is`( insertedScene.scene.sceneOrder))
        MatcherAssert.assertThat(sceneFromDatabase?.fxSettings?.duration, CoreMatchers.`is`( insertedScene.scene.fxSettings.duration))
        MatcherAssert.assertThat(sceneFromDatabase?.fxSettings?.feedback, CoreMatchers.`is`( insertedScene.scene.fxSettings.feedback))
        MatcherAssert.assertThat(sceneFromDatabase?.fxSettings?.volume, CoreMatchers.`is`( insertedScene.scene.fxSettings.volume))
        MatcherAssert.assertThat(sceneFromDatabase?.fxSettings?.fxMute, CoreMatchers.`is`( insertedScene.scene.fxSettings.fxMute))
        MatcherAssert.assertThat(sceneFromDatabase?.fxSettings?.fxType, CoreMatchers.`is`( insertedScene.scene.fxSettings.fxType))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun addPresetWithScenes_getPreset_fxSendsInsertedCorrectly() = runTest {
        val testPreset = Preset(presetName = "TestPreset")
        val testPresetId = testPreset.presetId
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)

        val insertedFxSend = presetWithScenes.scenes[0].fxSends[0]

        insertedFxSend.volume = 50
        insertedFxSend.isDirty = true
        insertedFxSend.inputIndex = 2

        presetsDao.insertPresetWithScenes(presetWithScenes)

        val presetsMap =  presetsDao.getPreset(testPresetId).map{it.preset.presetId to it}.toMap()

        MatcherAssert.assertThat( presetsMap.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat( presetsMap.containsKey(testPresetId), CoreMatchers.`is`(true))

        val presetFromDatabase = presetsMap[testPreset.presetId]
        val fxSendFromDatabase = presetFromDatabase?.scenes?.get(0)?.fxSends?.get(0)


        MatcherAssert.assertThat(fxSendFromDatabase?.volume, CoreMatchers.`is`( insertedFxSend.volume))
        MatcherAssert.assertThat(fxSendFromDatabase?.inputIndex, CoreMatchers.`is`( insertedFxSend.inputIndex))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun addPresetWithScenes_getPreset_masterChannelsInsertedCorrectly() = runTest {
        val testPreset = Preset(presetName = "TestPreset")
        val testPresetId = testPreset.presetId
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)

        val insertedMasterChannel = presetWithScenes.scenes[0].masterChannels[0]

        insertedMasterChannel.outputIndex = 1
        insertedMasterChannel.volume = 20
        insertedMasterChannel.fxReturn = 30
        insertedMasterChannel.panorama = 40
        insertedMasterChannel.mute = true

        presetsDao.insertPresetWithScenes(presetWithScenes)

        val presetsMap =  presetsDao.getPreset(testPresetId).map{it.preset.presetId to it}.toMap()

        MatcherAssert.assertThat( presetsMap.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat( presetsMap.containsKey(testPresetId), CoreMatchers.`is`(true))

        val presetFromDatabase = presetsMap[testPreset.presetId]
        val masterChannelFromDatabase = presetFromDatabase?.scenes?.get(0)?.masterChannels?.get(0)

        MatcherAssert.assertThat( masterChannelFromDatabase?.volume , CoreMatchers.`is`( insertedMasterChannel.volume))
        MatcherAssert.assertThat( masterChannelFromDatabase?.fxReturn , CoreMatchers.`is`( insertedMasterChannel.fxReturn))
        MatcherAssert.assertThat( masterChannelFromDatabase?.isDirty , CoreMatchers.`is`( insertedMasterChannel.isDirty))
        MatcherAssert.assertThat( masterChannelFromDatabase?.mute , CoreMatchers.`is`( insertedMasterChannel.mute))
        MatcherAssert.assertThat( masterChannelFromDatabase?.panorama , CoreMatchers.`is`( insertedMasterChannel.panorama))
        MatcherAssert.assertThat( masterChannelFromDatabase?.outputIndex , CoreMatchers.`is`( insertedMasterChannel.outputIndex))
    }

    @ExperimentalCoroutinesApi
    @Test
    @Throws(Exception::class)
    fun addPresetWithScenes_getPreset_audioChannelsInsertedCorrectly() = runTest {
        val testPreset = Preset(presetName = "TestPreset")
        val testPresetId = testPreset.presetId
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)

        val insertedAudioChannel = presetWithScenes.scenes[0].audioChannels[0]

        insertedAudioChannel.volume = 50
        insertedAudioChannel.outputIndex = 1
        insertedAudioChannel.inputIndex = 2
        insertedAudioChannel.panorama = 60
        insertedAudioChannel.mute = true
        insertedAudioChannel.solo = true

        presetsDao.insertPresetWithScenes(presetWithScenes)

        val presetsMap =  presetsDao.getPreset(testPresetId).map{it.preset.presetId to it}.toMap()

        MatcherAssert.assertThat( presetsMap.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat( presetsMap.containsKey(testPresetId), CoreMatchers.`is`(true))

        val presetFromDatabase = presetsMap[testPreset.presetId]
        val audioChannelFromDatabase = presetFromDatabase?.scenes?.get(0)?.audioChannels?.get(0)

        MatcherAssert.assertThat( audioChannelFromDatabase?.volume, CoreMatchers.`is`(insertedAudioChannel.volume))
        MatcherAssert.assertThat( audioChannelFromDatabase?.outputIndex, CoreMatchers.`is`(insertedAudioChannel.outputIndex ))
        MatcherAssert.assertThat( audioChannelFromDatabase?.inputIndex, CoreMatchers.`is`(insertedAudioChannel.inputIndex ))
        MatcherAssert.assertThat( audioChannelFromDatabase?.panorama, CoreMatchers.`is`(insertedAudioChannel.panorama))
        MatcherAssert.assertThat( audioChannelFromDatabase?.mute, CoreMatchers.`is`(insertedAudioChannel.mute))
        MatcherAssert.assertThat( audioChannelFromDatabase?.solo, CoreMatchers.`is`(insertedAudioChannel.solo))
    }


    //endregion @INSERT tests

}
