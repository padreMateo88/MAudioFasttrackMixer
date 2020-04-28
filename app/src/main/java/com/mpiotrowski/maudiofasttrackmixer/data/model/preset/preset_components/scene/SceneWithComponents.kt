package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.AudioChannel
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.FxSend
import com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene.scene_components.MasterChannel

const val MIXER_STEREO_OUTPUTS_NUMBER = 4
const val MIXER_INPUTS_NUMBER = 8
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
    val fxSendsMap = fxSends.map{it.channelNumber to it}.toMap()
    @Ignore
    val mastersMap = masterChannels.map{it.outputNumber to it}.toMap()
    @Ignore
    val channelsByOutputsMap = audioChannels.groupBy{it.outputNumber}.toMap()

    fun copyValues(copyFrom: SceneWithComponents, sceneOrder: Int, sceneName: String) {
        this.scene.sceneOrder = sceneOrder
        this.scene.sceneName = sceneName
        this.scene.fxSettings = copyFrom.scene.fxSettings.copy()
        copyFxSendsValues(copyFrom)
        copyMasterChannelsValues(copyFrom)
        copyAudioChannelsValues(copyFrom)
    }

    private fun copyFxSendsValues(copyFrom: SceneWithComponents) {
        for (inputIndex in this.fxSendsMap.keys) {
            copyFrom.fxSendsMap[inputIndex]?.volume?.let {
                this.fxSendsMap[inputIndex]?.volume = it
            }
        }
    }

    private fun copyMasterChannelsValues(copyFrom: SceneWithComponents) {
        for (outputIndex in this.mastersMap.keys) {
            this.masterChannels[outputIndex].fxReturn =
                copyFrom.masterChannels[outputIndex].fxReturn
            this.masterChannels[outputIndex].mute = copyFrom.masterChannels[outputIndex].mute
            this.masterChannels[outputIndex].panorama =
                copyFrom.masterChannels[outputIndex].panorama
            this.masterChannels[outputIndex].volume = copyFrom.masterChannels[outputIndex].volume
        }
    }

    private fun copyAudioChannelsValues(copyFrom: SceneWithComponents) {
        for (outputIndex in this.channelsByOutputsMap.keys) {
            val copyFromChannelsMap = copyFrom.channelsByOutputsMap[outputIndex]?.map { it.inputNumber to it }?.toMap()
            val copyToChannelsMap = this.channelsByOutputsMap[outputIndex]?.map { it.inputNumber to it }?.toMap()
            copyToChannelsMap?.let { copyToChannelByInputMap ->
                for (inputNumber in copyToChannelByInputMap.keys) {
                    copyFromChannelsMap?.get(inputNumber)?.let { copyFromAudioChannel->
                        copyToChannelByInputMap[inputNumber]?.panorama = copyFromAudioChannel.panorama
                        copyToChannelByInputMap[inputNumber]?.volume = copyFromAudioChannel.volume
                        copyToChannelByInputMap[inputNumber]?.mute = copyFromAudioChannel.mute
                        copyToChannelByInputMap[inputNumber]?.solo = copyFromAudioChannel.solo
                    }
                }
            }
        }
    }
}