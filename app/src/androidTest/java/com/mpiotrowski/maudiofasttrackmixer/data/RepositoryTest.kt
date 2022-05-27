package com.mpiotrowski.maudiofasttrackmixer.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDao
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.CurrentPreset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.SceneWithComponents
import com.mpiotrowski.maudiofasttrackmixer.getOrAwaitValue
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS

@RunWith(AndroidJUnit4::class)
class RepositoryAndroidTest {

    private lateinit var presetsDao: PresetsDao
    private lateinit var presetsDatabase: PresetsDatabase
    private lateinit var repository: Repository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        presetsDatabase = Room.inMemoryDatabaseBuilder(
            context, PresetsDatabase::class.java
        )
        .setTransactionExecutor(Executors.newSingleThreadExecutor())
        .build()
        runTest {
            presetsDatabase.populateDatabase()
        }
        presetsDao = presetsDatabase.presetsDao()
        repository = Repository(presetsDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        presetsDatabase.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  getCurrentPresetId_returnsInsertedValue() = runTest {
        val currentPreset = CurrentPreset(presetId = "testPreset")
        presetsDao.updateCurrentPreset(currentPreset)
        val currentPresetFormRepository = repository.getCurrentPresetId()
        MatcherAssert.assertThat( currentPresetFormRepository, CoreMatchers.`is`("testPreset"))
    }

    @ExperimentalCoroutinesApi
    @Test
    @MediumTest
    fun  setCurrentPreset_currentModelStateUpdated() = runTest {
        val presetNameTestValue = "TestPreset"

        val currentPresetId = presetsDao.getCurrentPreset()?.presetId
        var currentPresetName = ""
        currentPresetId?.let {
            currentPresetName = presetsDao.getPreset(it)?.preset?.presetName.toString()
        }
        MatcherAssert.assertThat( currentPresetName, CoreMatchers.not(presetNameTestValue))

        val presetToLoad = PresetWithScenes.newInstance(Preset(presetName = presetNameTestValue))
        repository.setCurrentPreset(presetToLoad)
        val presetNameInRepository = repository.currentModelState.getOrAwaitValue().preset.presetName
        MatcherAssert.assertThat( presetNameInRepository, CoreMatchers.`is`(presetNameTestValue))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  setCurrentPresetName_currentPresetNameEqualsSetValue() = runTest {
        val testCurrentSceneName = "TestSceneName"

        val initialCurrentSceneName = repository.currentScene.getOrAwaitValue().scene.sceneName
        MatcherAssert.assertThat( initialCurrentSceneName, CoreMatchers.not(testCurrentSceneName))

        repository.setCurrentSceneName(testCurrentSceneName)
        val currentSceneName = repository.currentScene.getOrAwaitValue().scene.sceneName
        MatcherAssert.assertThat( currentSceneName, CoreMatchers.`is`(testCurrentSceneName))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  notifyCurrentStateChanged_whenCalled_observerNotified() = runTest {

        // CountDownLatch counts to 2. First event is triggered ofter oserver is registered,
        // second event is triggered when notifyCurrentStateChanged() is invoked.
        val latch = CountDownLatch(2)
        repository.currentModelState.observeForever {
            latch.countDown()
        }
        repository.notifyCurrentStateChanged()
        val countDownSuccessFull = latch.await(2, SECONDS)
        MatcherAssert.assertThat( countDownSuccessFull, CoreMatchers.`is`(true))

    }

    @ExperimentalCoroutinesApi
    @Test
    fun  notifyCurrentStateChanged_whenNotCalled_observerNotNotified() = runTest {

        // CountDownLatch counts to 2. First event is triggered ofter oserver is registered,
        // second event is triggered when notifyCurrentStateChanged() is invoked.
        val latch = CountDownLatch(2)
        repository.currentModelState.observeForever {
            latch.countDown()
        }
        val countDownSuccessFull = latch.await(2, SECONDS)
        MatcherAssert.assertThat( countDownSuccessFull, CoreMatchers.`is`(false))

    }

    @ExperimentalCoroutinesApi
    @Test
    @MediumTest
    fun  saveCurrentPreset_currentModelStateUpdated() = runTest {

        val testPresetID = "TestPresetID"
        val originalTestPresetName = "OriginalTestPresetName"
        val updatedTestPresetName = "UpdatedTestPresetName"
        val testPreset = PresetWithScenes.newInstance(Preset(presetId = testPresetID, presetName = originalTestPresetName))

        val savedPreset = presetsDao.getPreset(testPresetID)
        val currentPreset = presetsDao.getCurrentPresetInstance()

        presetsDao.insertPresetWithScenes(testPreset)

        //assert that the values after update are not in database initially
        MatcherAssert.assertThat( savedPreset?.preset?.presetName, CoreMatchers.not(updatedTestPresetName))
        MatcherAssert.assertThat( currentPreset.preset.presetName, CoreMatchers.not(updatedTestPresetName))

        //update name of the tested preset
        testPreset.preset.presetName = updatedTestPresetName

        //run method under test
        repository.saveCurrentPreset(testPreset)


        //get updated presets
        val savedPresetAfterUpdate = presetsDao.getPreset(testPresetID)
        val currentPresetAfterUpdate = presetsDao.getCurrentPresetInstance()

        //assert updated presets in from database contain updated names
        MatcherAssert.assertThat(savedPresetAfterUpdate?.preset?.presetName, CoreMatchers.`is`(updatedTestPresetName))
        MatcherAssert.assertThat(currentPresetAfterUpdate.preset.presetName, CoreMatchers.`is`(updatedTestPresetName))
    }

    @ExperimentalCoroutinesApi
    @Test
    @MediumTest
    fun  saveSceneOfCurrentPresetAs_currentModelStateUpdated() = runTest {
        val testSceneName = "test Scene 5"
        val testSceneOrder = 5

        val testScene = SceneWithComponents.newInstance(testSceneName, "test Preset", testSceneOrder)
        repository.saveSceneOfCurrentPresetAs(testScene)

        val updatedSceneName = presetsDao.getCurrentPresetInstance().scenesByOrder[testSceneOrder]?.scene?.sceneName
        MatcherAssert.assertThat(updatedSceneName, CoreMatchers.`is`(testSceneName))
    }
}