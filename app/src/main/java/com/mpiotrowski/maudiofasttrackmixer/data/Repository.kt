package com.mpiotrowski.maudiofasttrackmixer.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDao
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.*
import com.mpiotrowski.maudiofasttrackmixer.util.Event
import com.mpiotrowski.maudiofasttrackmixer.util.LogUtil
import com.mpiotrowski.maudiofasttrackmixer.util.mutation

class Repository(private val presetsDao: PresetsDao) {

    private val _sceneLoadedEvent = MutableLiveData<Event<Int>>()
    private val _currentModelState = MediatorLiveData<PresetWithScenes>()
    private val _currentScene = MediatorLiveData<SceneWithComponents>()
    private val _persistedState = presetsDao.getPersistedState()

    val presetsWithScenes
        get() = presetsDao.getPresetsWithScenes()

    val currentModelState: LiveData<PresetWithScenes>
        get() = _currentModelState

    val currentScene: LiveData<SceneWithComponents>
        get() = _currentScene

    init {

        _currentScene.addSource(_currentModelState) { value ->
            if(value != null) {
                val scenesById = value.scenes.associateBy { it.scene.sceneId }

                if (scenesById.containsKey(currentScene.value?.scene?.sceneId))
                    _currentScene.value = scenesById[currentScene.value?.scene?.sceneId]
                else
                    _currentScene.value = value.scenesByOrder[1]
            }
        }

        _currentModelState.addSource(_persistedState) {
            if(_currentModelState.value == null)
                _currentModelState.value = it
        }

        _currentScene.addSource(_sceneLoadedEvent) { sceneSelectedEvent ->
            sceneSelectedEvent.getContentIfNotHandled()?.let {
                    sceneIndex ->
                _currentScene.value = currentModelState.value?.scenesByOrder?.get(sceneIndex)
            }
        }
    }

    suspend fun getCurrentPresetId(): String {
        val currentPresetList = presetsDao.getCurrentPreset()
        return currentPresetList?.presetId ?: ""
    }

    fun setSelectedScene(sceneIndex :Int) {
        _sceneLoadedEvent.value = Event(sceneIndex)
    }

    fun setCurrentSceneName(sceneName: String) {
        _currentScene.mutation {
            it.value?.scene?.sceneName = sceneName
        }
    }

//endregion get

//region add
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addPreset(preset: Preset) {
        presetsDao.addPreset(preset)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addPresetWithScenes(presetWithScenes: PresetWithScenes) {
        presetsDao.insertPresetWithScenes(presetWithScenes)
    }
//endregion add

//endregion save
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun savePresetWithScenes(presetWithScenes: PresetWithScenes, saveAll: Boolean) {
        LogUtil.d( "savePresetWithScenes")
        presetsDao.updatePresetWithScenes(presetWithScenes, saveAll)
    }

    fun saveSceneWithComponents(sceneWithComponents: SceneWithComponents, saveAll: Boolean) {
        presetsDao.updateSceneWithComponents(sceneWithComponents, updateAll = saveAll)
    }

    fun notifyCurrentStateChanged() {
        _currentModelState.mutation {}
    }

    suspend fun setCurrentPreset(presetToLoad: PresetWithScenes) {
        if(_currentModelState.value != null) {
            _currentModelState.value?.copyValues(presetToLoad, presetToLoad.preset.presetName)
        } else {
            val lastPersistedState = presetsDao.getPersistedStateBlocking()

            if(lastPersistedState != null) {
                lastPersistedState.copyValues(presetToLoad, presetToLoad.preset.presetName)
                _currentModelState.value = lastPersistedState
            } else {
                val createdLastPersistedState = presetsDao.addPreset(Preset(presetId = LAST_PERSISTED_STATE_ID, presetName = DEFAULT_PRESET_NAME))
                _currentModelState.value = createdLastPersistedState
            }
        }
    }

    suspend fun saveCurrentPreset(currentState: PresetWithScenes) {
        val currentPreset = presetsDao.getCurrentPresetInstance()
        currentPreset.copyValues(currentState, currentState.preset.presetName)
        presetsDao.updatePresetWithScenes(currentPreset, true)
        presetsDao.updatePresetWithScenes(currentState, true)
    }

    suspend fun saveSceneOfCurrentPresetAs(sceneWithComponents: SceneWithComponents) {
        val sceneOfCurrentPreset = presetsDao.getCurrentPresetInstance().scenesByOrder[sceneWithComponents.scene.sceneOrder]
        presetsDao.updateSceneWithComponents(sceneWithComponents, updateAll = true)
        sceneOfCurrentPreset?.let {
            it.copyValues(sceneWithComponents, sceneWithComponents.scene.sceneName)
            presetsDao.updateSceneWithComponents(it, updateAll = true)
        }
    }

    suspend fun saveCurrentPresetId(currentPreset: PresetWithScenes) {
        presetsDao.updateCurrentPreset(CurrentPreset(presetId = currentPreset.preset.presetId))
    }

    fun saveScene(scene: Scene) {
        presetsDao.updateScene(scene)
    }
//endregion save

//endregion remove
    fun deletePreset(preset: Preset) {
        presetsDao.deletePreset(preset)
    }
//endregion remove
}