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

    val currentPreset = presetsDao.getCurrentPresetLiveData()

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
    fun savePresetWithScenes(presetWithScenes: PresetWithScenes, saveAll: Boolean) {
        presetsDao.updatePresetWithScenes(presetWithScenes, saveAll)
    }

    fun savePreset(preset: Preset) {
        presetsDao.updatePreset(preset)
    }

    fun saveSceneWithComponents(sceneWithComponents: SceneWithComponents, saveAll: Boolean) {
        presetsDao.updateSceneWithComponents(sceneWithComponents, updateAll = saveAll)
    }

    fun saveCurrentPreset(currentPreset: PresetWithScenes) {
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