package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

val MIXER_OUTPUTS_WITH_FX = arrayListOf(1,2)
const val MIXER_STEREO_OUTPUTS_COUNT = 4
const val MIXER_INPUTS_COUNT = 8
data class SceneWithComponents (
    @Embedded var scene: Scene,

    @Relation(
        parentColumn = "sceneId",
        entityColumn = "sceneId",
        entity = FxSend::class
    )
    var fxSends: List<FxSend>,

    @Relation(
        parentColumn = "sceneId",
        entityColumn = "sceneId",
        entity = MasterChannel::class
    )
    var masterChannels: List<MasterChannel>,

    @Relation(
        parentColumn = "sceneId",
        entityColumn = "sceneId",
        entity = AudioChannel::class
    )
    var audioChannels: List<AudioChannel>
) {
    @Ignore
    val fxSendsMap = fxSends.map{it.inputIndex to it}.toMap()
    @Ignore
    val mastersByOutputsMap = masterChannels.map{it.outputIndex to it}.toMap()
    @Ignore
    val channelsByOutputsMap = audioChannels.groupBy{it.outputIndex}.toMap()

    fun copyValues(copyFrom: SceneWithComponents, sceneName: String) {
        this.scene.sceneName = sceneName
        this.scene.fxSettings = copyFrom.scene.fxSettings.copy()
        copyFxSendsValues(copyFrom)
        copyMasterChannelsValues(copyFrom)
        copyAudioChannelsValues(copyFrom)
    }

    private fun copyFxSendsValues(copyFrom: SceneWithComponents) {
        for (inputIndex in this.fxSendsMap.keys) {
            copyFrom.fxSendsMap[inputIndex]?.let {
                this.fxSendsMap[inputIndex]?.volume = it.volume
                this.fxSendsMap[inputIndex]?.isDirty = it.isDirty
            }
        }
    }

    private fun copyMasterChannelsValues(copyFrom: SceneWithComponents) {
        for (outputIndex in this.mastersByOutputsMap.keys) {
            copyFrom.mastersByOutputsMap[outputIndex]?.let {
            this.mastersByOutputsMap[outputIndex]?.fxReturn = it.fxReturn
            this.mastersByOutputsMap[outputIndex]?.mute = it.mute
            this.mastersByOutputsMap[outputIndex]?.panorama = it.panorama
            this.mastersByOutputsMap[outputIndex]?.volume = it.volume
            this.mastersByOutputsMap[outputIndex]?.isDirty = it.isDirty
            }
        }
    }

    private fun copyAudioChannelsValues(copyFrom: SceneWithComponents) {
        for (outputIndex in this.channelsByOutputsMap.keys) {
            val copyFromChannelsMap = copyFrom.channelsByOutputsMap[outputIndex]?.map { it.inputIndex to it }?.toMap()
            val copyToChannelsMap = this.channelsByOutputsMap[outputIndex]?.map { it.inputIndex to it }?.toMap()
            copyToChannelsMap?.let { copyToChannelByInputMap ->
                for (inputIndex in copyToChannelByInputMap.keys) {
                    copyFromChannelsMap?.get(inputIndex)?.let { copyFromAudioChannel->
                        copyToChannelByInputMap[inputIndex]?.panorama = copyFromAudioChannel.panorama
                        copyToChannelByInputMap[inputIndex]?.volume = copyFromAudioChannel.volume
                        copyToChannelByInputMap[inputIndex]?.mute = copyFromAudioChannel.mute
                        copyToChannelByInputMap[inputIndex]?.solo = copyFromAudioChannel.solo
                        copyToChannelByInputMap[inputIndex]?.isDirty = copyFromAudioChannel.isDirty
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(sceneName: String, presetId: String, sceneOrder: Int): SceneWithComponents {
            val scene = Scene(sceneName = sceneName, presetId = presetId, sceneOrder = sceneOrder)
            val masterChannels = mutableListOf<MasterChannel>()
            val audioChannels = mutableListOf<AudioChannel>()
            val fxSends = mutableListOf<FxSend>()

            for (outputIndex in 1 .. MIXER_STEREO_OUTPUTS_COUNT) {
                masterChannels.add(MasterChannel(outputIndex = outputIndex))
                for (inputIndex in 1 .. MIXER_INPUTS_COUNT) {
                    audioChannels.add(AudioChannel(outputIndex = outputIndex, inputIndex = inputIndex))
                }
            }

            for (inputIndex in 1 .. MIXER_INPUTS_COUNT) {
                fxSends.add(FxSend(inputIndex = inputIndex))
            }

            return SceneWithComponents(
                scene = scene,
                masterChannels = masterChannels,
                audioChannels = audioChannels,
                fxSends = fxSends
            )
        }
    }
}