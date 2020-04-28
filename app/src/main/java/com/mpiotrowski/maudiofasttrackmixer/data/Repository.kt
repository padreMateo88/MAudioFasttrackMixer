package com.mpiotrowski.maudiofasttrackmixer.data

import androidx.annotation.WorkerThread
import com.mpiotrowski.maudiofasttrackmixer.data.database.PresetsDao
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.Preset
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.PresetWithScenes
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.SCENES_IN_PRESET_NUMBER
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.SampleRate
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.*
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

class Repository(private val presetsDao: PresetsDao) {

//region get
    val presetsWitScenes = presetsDao.getPresetsWithScenes()
//endregion get

//region add
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun addPreset(presetName: String) {
        val preset = Preset(presetName = presetName, sampleRate = SampleRate.SR_48)
        val scenes = mutableListOf<SceneWithComponents>()
        for(index in 1 .. SCENES_IN_PRESET_NUMBER) {
            scenes.add(createSceneWithComponents("Scene $index", preset.presetId, index))
        }
        presetsDao.insertPresetWithScenes(PresetWithScenes(preset,scenes))
    }

    private fun createSceneWithComponents(sceneName: String, presetId: String, sceneOrder: Int): SceneWithComponents{
        val scene = Scene(sceneName = sceneName, presetId = presetId, sceneOrder = sceneOrder)
        val masterChannels = mutableListOf<MasterChannel>()
        val audioChannels = mutableListOf<AudioChannel>()
        val fxSends = mutableListOf<FxSend>()

        for (outputIndex in 1 .. MIXER_STEREO_OUTPUTS_NUMBER) {
            masterChannels.add(MasterChannel(outputNumber = outputIndex))
            for (inputIndex in 1 .. MIXER_INPUTS_NUMBER) {
                audioChannels.add(AudioChannel(outputNumber = outputIndex, inputNumber = inputIndex))
            }
        }

        for (inputIndex in 1 .. MIXER_INPUTS_NUMBER) {
            fxSends.add(FxSend(channelNumber = inputIndex))
        }

        return SceneWithComponents(
            scene = scene,
            masterChannels = masterChannels,
            audioChannels = audioChannels,
            fxSends = fxSends
        )
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

    suspend fun saveSceneWithComponents(sceneWithComponents: SceneWithComponents) {
        presetsDao.updateSceneWithComponents(sceneWithComponents)
    }

    suspend fun savePresetWithScenes(presetWithScenes: PresetWithScenes) {
        presetsDao.updatePresetWithScenes(presetWithScenes)
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