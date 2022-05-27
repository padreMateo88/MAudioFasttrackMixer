package com.mpiotrowski.maudiofasttrackmixer.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDao
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.util.mutation
import io.mockk.*
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

    private val presetsDaoMockk: PresetsDao = mockk(relaxed = true)

    @ExperimentalCoroutinesApi
    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        val lastPersistedState = MutableLiveData<PresetWithScenes>()
        lastPersistedState.value = PresetWithScenes.newInstance(Preset(presetId = LAST_PERSISTED_STATE_ID, presetName = LAST_PERSISTED_STATE_NAME))

        every { presetsDaoMockk.getPersistedState() } returns lastPersistedState

        repositoryWithMockDao = Repository(presetsDaoMockk)
    }


    @ExperimentalCoroutinesApi
    @Test
    fun  addPreset_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        repositoryWithMockDao.addPreset(testPreset)
        coVerify {presetsDaoMockk.addPreset(testPreset)}
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  addPresetWithScenes_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)
        repositoryWithMockDao.addPresetWithScenes(presetWithScenes)
        coVerify {presetsDaoMockk.insertPresetWithScenes(presetWithScenes)}
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  savePresetWithScenes_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)
        repositoryWithMockDao.savePresetWithScenes(presetWithScenes, true)
        coVerify {presetsDaoMockk.updatePresetWithScenes(presetWithScenes, true)}
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  saveSceneWithComponents_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val sceneWithComponents = PresetWithScenes.newInstance(testPreset).scenes[0]
        repositoryWithMockDao.saveSceneWithComponents(sceneWithComponents, true)
        coVerify {presetsDaoMockk.updateSceneWithComponents(sceneWithComponents, updateAll = true)}
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  saveCurrentPresetId_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val presetWithScenes = PresetWithScenes.newInstance(testPreset)
        repositoryWithMockDao.saveCurrentPresetId(presetWithScenes)
        coVerify {presetsDaoMockk.updateCurrentPreset(CurrentPreset(presetId = presetWithScenes.preset.presetId))}
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  saveScene_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        val testScene = PresetWithScenes.newInstance(testPreset).scenes[0]
        repositoryWithMockDao.saveScene(testScene.scene)
        coVerify {presetsDaoMockk.updateScene(testScene.scene)}
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  deletePreset_whenCalled_daoMethodCalled() = runTest {
        val testPreset = Preset(presetName = "TestPreset", presetId = "testPresetId")
        repositoryWithMockDao.deletePreset(testPreset)
        coVerify {presetsDaoMockk.deletePreset(testPreset)}
    }

    @ExperimentalCoroutinesApi
    @Test
    fun  getCurrentPresetId_whenCalled_daoMethodCalled() = runTest {
        repositoryWithMockDao.getCurrentPresetId()
        coVerify { presetsDaoMockk.getCurrentPreset() }
    }
}