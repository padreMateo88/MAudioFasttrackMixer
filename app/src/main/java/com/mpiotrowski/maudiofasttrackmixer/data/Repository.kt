package com.mpiotrowski.maudiofasttrackmixer.data

import androidx.annotation.WorkerThread
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDao
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

class Repository(private val presetsDao: PresetsDao) {

    val presetsWithScenes = presetsDao.getPresetsWithScenes()

    suspend fun getCurrentPresetId(): String {
        val currentPresetList = presetsDao.getCurrentPreset()
        return if(currentPresetList.isEmpty())
            ""
        else
            currentPresetList[0].presetId
    }

    val currentState = presetsDao.getPersistedState()

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
        presetsDao.updatePresetWithScenes(presetWithScenes, saveAll)
    }

    suspend fun saveCurrentPreset(currentState: PresetWithScenes) {
        val currentPreset = presetsDao.getCurrentPresetInstance()
        currentPreset.copyValues(currentState, currentState.preset.presetName)
        presetsDao.updatePresetWithScenes(currentPreset, true)
        presetsDao.updatePresetWithScenes(currentState, true)
    }

    fun saveSceneWithComponents(sceneWithComponents: SceneWithComponents, saveAll: Boolean) {
        presetsDao.updateSceneWithComponents(sceneWithComponents, updateAll = saveAll)
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

    suspend fun saveMasterChannel(vararg masterChannel: MasterChannel) {
        presetsDao.updateMasterChannel(*masterChannel)
    }

    suspend fun saveAudioChannel(vararg audioChannel: AudioChannel) {
        presetsDao.updateAudioChannel(*audioChannel)
    }

    suspend fun saveFxSend(vararg fxSend: FxSend) {
        presetsDao.updateFxSend(*fxSend)
    }
//endregion save

//endregion remove
    suspend fun deletePreset(preset: Preset) {
        presetsDao.deletePreset(preset)
    }

    suspend fun deleteScene(scene: Scene) {
        presetsDao.deleteScene(scene)
    }
//endregion remove
}