package com.mpiotrowski.maudiofasttrackmixer.data

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDao
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDatabase
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.CurrentPreset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
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
import org.mockito.MockitoAnnotations
import java.io.IOException
import java.util.concurrent.Executors

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
        MockitoAnnotations.initMocks(this)
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
    fun  setCurrentPreset_currentModelStateUpdated() = runTest {
        val presetNameTestValue = "TestPreset"
        val presetToLoad = PresetWithScenes.newInstance(Preset(presetName = presetNameTestValue))
        Log.d("MPdebug", "TEST presisted sate ${presetsDao.getPersistedState().getOrAwaitValue()?.preset?.presetName}")
        repository.setCurrentPreset(presetToLoad)
        val presetNameInRepository = repository.currentModelState.getOrAwaitValue().preset.presetName
        MatcherAssert.assertThat( presetNameInRepository, CoreMatchers.`is`(presetNameTestValue))
    }

//    suspend fun saveCurrentPreset(currentState: PresetWithScenes) {
//        val currentPreset = presetsDao.getCurrentPresetInstance()
//        currentPreset.copyValues(currentState, currentState.preset.presetName)
//        presetsDao.updatePresetWithScenes(currentPreset, true)
//        presetsDao.updatePresetWithScenes(currentState, true)
//    }
//
//    suspend fun saveSceneOfCurrentPresetAs(sceneWithComponents: SceneWithComponents) {
//        val sceneOfCurrentPreset = presetsDao.getCurrentPresetInstance().scenesByOrder[sceneWithComponents.scene.sceneOrder]
//        presetsDao.updateSceneWithComponents(sceneWithComponents, updateAll = true)
//        sceneOfCurrentPreset?.let {
//            it.copyValues(sceneWithComponents, sceneWithComponents.scene.sceneName)
//            presetsDao.updateSceneWithComponents(it, updateAll = true)
//        }
//    }
}