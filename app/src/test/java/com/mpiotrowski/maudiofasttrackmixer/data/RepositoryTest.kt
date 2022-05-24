package com.mpiotrowski.maudiofasttrackmixer.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDao
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class RepositoryTest{

    private lateinit var repositoryWithMockDao: Repository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var presetsDaoMock: PresetsDao

    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        val lastPersistedState = MutableLiveData<PresetWithScenes>()
        lastPersistedState.value = PresetWithScenes.newInstance(Preset(presetId = LAST_PERSISTED_STATE_ID, presetName = LAST_PERSISTED_STATE_NAME))
        Mockito.`when`(presetsDaoMock.getPersistedState()).thenReturn(lastPersistedState)

        repositoryWithMockDao = Repository(presetsDaoMock)
    }


    @ExperimentalCoroutinesApi
    @Test
    fun  addPreset_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        repositoryWithMockDao.addPreset(testPreset)
        Mockito.verify(presetsDaoMock).addPreset(testPreset)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  addPresetWithScenes_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)
        repositoryWithMockDao.addPresetWithScenes(presetWithScenes)
        Mockito.verify(presetsDaoMock).insertPresetWithScenes(presetWithScenes)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  savePresetWithScenes_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)
        repositoryWithMockDao.savePresetWithScenes(presetWithScenes, true)
        Mockito.verify(presetsDaoMock).updatePresetWithScenes(presetWithScenes, true)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  saveSceneWithComponents_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val sceneWithComponents = PresetWithScenes.newInstance(testPreset).scenes[0]
        repositoryWithMockDao.saveSceneWithComponents(sceneWithComponents, true)
        Mockito.verify(presetsDaoMock).updateSceneWithComponents(sceneWithComponents, updateAll = true)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  saveCurrentPresetId_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)
        repositoryWithMockDao.saveCurrentPresetId(presetWithScenes)
        Mockito.verify(presetsDaoMock).updateCurrentPreset(CurrentPreset(presetId = presetWithScenes.preset.presetId))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  saveScene_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val testScene = PresetWithScenes.newInstance(testPreset).scenes[0]
        repositoryWithMockDao.saveScene(testScene.scene)
        Mockito.verify(presetsDaoMock).updateScene(testScene.scene)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  deletePreset_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        repositoryWithMockDao.deletePreset(testPreset)
        Mockito.verify(presetsDaoMock).deletePreset(testPreset)
    }
}