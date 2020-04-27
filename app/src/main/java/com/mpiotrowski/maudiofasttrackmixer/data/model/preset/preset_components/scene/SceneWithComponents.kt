package com.mpiotrowski.maudiofasttrackmixer.data.model.preset.preset_components.scene

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Junction
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
    val channelsMap = audioChannels.groupBy{it.outputNumber}
    @Ignore
    val mastersMap = masterChannels.map{it.outputNumber to it}.toMap()
}