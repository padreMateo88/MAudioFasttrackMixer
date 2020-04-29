package com.mpiotrowski.maudiofasttrackmixer.data

import androidx.annotation.WorkerThread
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDao
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

class Repository(private val presetsDao: PresetsDao) {

//region get
    val presetsWithScenes = presetsDao.getPresetsWithScenes(CURRENT_PRESET_ID)

    suspend fun getCurrentPreset(): PresetWithScenes {
        val currentPresetList = presetsDao.getDefaultPreset(CURRENT_PRESET_ID)
        return if(currentPresetList.isNotEmpty())
            currentPresetList[0]
        else
            presetsDao.addPreset(Preset(CURRENT_PRESET_ID,CURRENT_PRESET_NAME))
    }
//endregion get

//region add
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addPreset(preset: Preset) {
        presetsDao.addPreset(preset)
    }
//endregion add

//endregion save
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