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

    val currentPreset = presetsDao.getCurrentPreset()

    val currentState = presetsDao.getPersistedState()
//endregion get

//region add
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addPreset(preset: Preset) {
        presetsDao.addPreset(preset)
    }
//endregion add

//endregion save

    suspend fun loadPreset(presetToLoad: PresetWithScenes) {
        val currentStateCopy = currentState.value?.copy()
        currentStateCopy?.let {
            it.copyValues(presetToLoad, presetToLoad.preset.presetName)
            savePresetWithScenes(it, true)
            presetsDao.updateCurrentPreset(CurrentPreset(presetId = presetToLoad.preset.presetId))
        }
    }

    suspend fun savePreset(preset: Preset) {
        presetsDao.updatePreset(preset)
    }

    suspend fun saveScene(scene: Scene) {
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

    suspend fun saveSceneWithComponents(sceneWithComponents: SceneWithComponents, saveAll: Boolean) {
        presetsDao.updateSceneWithComponents(sceneWithComponents, updateAll = saveAll)
    }

    suspend fun saveCurrentDeviceState() {
        currentState.value?.let {
            savePresetWithScenes(it, false)
        }
    }

    suspend fun savePresetWithScenes(presetWithScenes: PresetWithScenes, saveAll: Boolean) {
        presetsDao.updatePresetWithScenes(presetWithScenes, saveAll)
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