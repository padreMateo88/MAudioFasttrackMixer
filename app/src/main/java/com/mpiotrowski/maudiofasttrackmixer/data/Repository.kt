package com.mpiotrowski.maudiofasttrackmixer.data

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDao
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.*
import com.mpiotrowski.maudiofasttrackmixer.util.Event
import com.mpiotrowski.maudiofasttrackmixer.util.mutation

class Repository(private val presetsDao: PresetsDao) {

    private val _sceneLoadedEvent = MutableLiveData<Event<Int>>()
    private val _currentState = MediatorLiveData<PresetWithScenes>()
    private val _currentScene = MediatorLiveData<SceneWithComponents>()

    val presetsWithScenes
        get() = presetsDao.getPresetsWithScenes()

    val currentState: LiveData<PresetWithScenes>
        get() = _currentState

    val currentScene: LiveData<SceneWithComponents>
        get() = _currentScene

    init {
        _currentScene.addSource(_currentState) { value ->
            val scenesById = value.scenes.map {it.scene.sceneId to it}.toMap()

            if(scenesById.containsKey(currentScene.value?.scene?.sceneId))
                _currentScene.value = scenesById[currentScene.value?.scene?.sceneId]
            else
                _currentScene.value = value?.scenesByOrder?.get(1)
        }

        _currentState.addSource(presetsDao.getPersistedState()) {
            if(_currentState.value == null)
                _currentState.value = it
        }

        _currentScene.addSource(_sceneLoadedEvent) { sceneSelectedEvent ->
            sceneSelectedEvent.getContentIfNotHandled()?.let {
                    sceneIndex ->
                _currentScene.value = currentState.value?.scenesByOrder?.get(sceneIndex)
            }
        }
    }

    suspend fun getCurrentPresetId(): String {
        val currentPresetList = presetsDao.getCurrentPreset()
        return if(currentPresetList.isEmpty())
            ""
        else
            currentPresetList[0].presetId
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
        Log.d("MPdebug", "savePresetWithScenes")
        presetsDao.updatePresetWithScenes(presetWithScenes, saveAll)
    }

    fun saveSceneWithComponents(sceneWithComponents: SceneWithComponents, saveAll: Boolean) {
        presetsDao.updateSceneWithComponents(sceneWithComponents, updateAll = saveAll)
    }

    fun notifyCurrentStateChanged() {
        _currentState.mutation {}
    }
    fun setCurrentPreset(presetToLoad: PresetWithScenes) {
        _currentState.mutation {
            it.value?.copyValues(presetToLoad, presetToLoad.preset.presetName)
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
    suspend fun deletePreset(preset: Preset) {
        presetsDao.deletePreset(preset)
    }
//endregion remove
}